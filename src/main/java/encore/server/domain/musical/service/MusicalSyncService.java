package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalOpenApiConverter;
import encore.server.domain.musical.dto.external.MusicalOpenApiDto;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicalSyncService {

    private final MusicalRepository musicalRepository;
    private final ExternalMusicalApiClient externalMusicalApiClient;

    // API 명세상 필수라서 넣는 더미 값
    private static final String DUMMY_START = "20000101";
    private static final String DUMMY_END   = "20991231";
    private static final String INIT_START = "20250101"; // 초기 적재 기준일
    private static final int PAGE_SIZE      = 100;

    private final DateTimeFormatter yyyymmdd = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 초기 적재: INIT_START 기준으로 전체 가져와 업서트
     */
    public SyncStats initialLoad(String serviceKey) {
        log.info("[MusicalSyncService] 초기 적재 시작 ({} ~ {}, afterDate=null)", DUMMY_START, DUMMY_END);
        SyncStats total = syncAll(serviceKey, INIT_START);
        log.info("[MusicalSyncService] 초기 적재 완료: fetched={} inserted={} updated={} skipped={} failed={}",
                total.getFetched(), total.getInserted(), total.getUpdated(), total.getSkipped(), total.getFailed());
        return total;
    }

    /**
     * 일일 갱신: 어제 이후 등록/수정된 것만
     */
    public SyncStats updateDaily(String serviceKey) {
        String yesterday = LocalDate.now().minusDays(2).format(yyyymmdd);
        log.info("[MusicalSyncService] 일일 갱신 시작 (afterDate={})", yesterday);
        SyncStats total = syncAll(serviceKey, yesterday);
        log.info("[MusicalSyncService] 일일 갱신 완료: fetched={} inserted={} updated={} skipped={} failed={}",
                total.getFetched(), total.getInserted(), total.getUpdated(), total.getSkipped(), total.getFailed());
        return total;
    }

    /**
     * 수동 갱신: afterDate만 받아서 그 이후 등록/수정된 것들만 가져온다.
     */
    public SyncStats updateAfter(String serviceKey, String afterDate) {
        log.info("[MusicalSyncService] 수동 after 갱신 시작 (afterDate={})", afterDate);
        SyncStats total = syncAll(serviceKey, (afterDate == null || afterDate.isBlank()) ? null : afterDate);
        log.info("[MusicalSyncService] 수동 after 갱신 완료: fetched={} inserted={} updated={} skipped={} failed={}",
                total.getFetched(), total.getInserted(), total.getUpdated(), total.getSkipped(), total.getFailed());
        return total;
    }

    /**
     * 실제 동기화 루프
     * - stdate/eddate는 명세 충족을 위한 더미
     * - afterDate가 있으면 그 날짜 이후 등록/수정된 항목만 내려온다
     */
    private SyncStats syncAll(String serviceKey, String afterDate) {
        int page = 1;
        SyncStats total = new SyncStats();

        while (true) {
            List<MusicalOpenApiDto> batch = externalMusicalApiClient.fetchMusicals(
                    serviceKey,
                    DUMMY_START,
                    DUMMY_END,
                    page,
                    PAGE_SIZE,
                    afterDate
            );

            if (batch.isEmpty()) {
                break;
            }

            total.fetched += batch.size();
            SyncStats pageStats = upsert(batch);
            total.add(pageStats);

            if (batch.size() < PAGE_SIZE) {
                break;
            }
            page++;
        }

        return total;
    }

    /** Map 기반 Upsert (N+1 제거) */
    @Transactional
    protected SyncStats upsert(List<MusicalOpenApiDto> apiData) {
        SyncStats stats = new SyncStats();

        List<String> ids = apiData.stream()
                .map(MusicalOpenApiDto::mt20id)
                .filter(id -> id != null && !id.isBlank())
                .toList();

        if (ids.isEmpty()) {
            stats.skipped += apiData.size();
            return stats;
        }

        Map<String, Musical> existMap = musicalRepository.findByOpenApiIdIn(ids).stream()
                .collect(Collectors.toMap(Musical::getOpenApiId, m -> m));

        List<Musical> toInsert = new ArrayList<>();

        for (MusicalOpenApiDto dto : apiData) {
            try {
                String externalId = dto.mt20id();
                if (externalId == null || externalId.isBlank()) {
                    stats.skipped++;
                    continue;
                }

                Musical exist = existMap.get(externalId);
                if (exist != null) {
                    MusicalOpenApiConverter.updateEntity(exist, dto);
                    stats.updated++;
                } else {
                    toInsert.add(MusicalOpenApiConverter.toEntity(dto));
                    stats.inserted++;
                }
            } catch (Exception ex) {
                stats.failed++;
                log.warn("[MusicalSyncService] upsert 실패 id={} title={} msg={}",
                        dto.mt20id(), dto.prfnm(), ex.getMessage());
            }
        }

        if (!toInsert.isEmpty()) {
            try {
                // 1차 시도: bulk insert
                musicalRepository.saveAll(toInsert);
            } catch (DataIntegrityViolationException e) {
                log.warn("bulk insert 중 일부 유니크 충돌 발생 — 개별 저장으로 폴백");
                // 2차 시도: 개별 insert (충돌 건만 스킵)
                for (Musical m : toInsert) {
                    try {
                        musicalRepository.save(m);
                    } catch (DataIntegrityViolationException dup) {
                        stats.skipped++;
                        log.info("유니크 충돌 스킵: title='{}', location='{}'",
                                m.getTitle(), m.getLocation());
                    }
                }
            }
        }

        log.info("[MusicalSyncService] upsert 완료: inserted={} updated={} skipped={} failed={}",
                stats.inserted, stats.updated, stats.skipped, stats.failed);

        return stats;
    }

    /**
     * 컨트롤러에서 그대로 내려보낼 수 있도록 public + getter
     */
    @Getter
    public static class SyncStats {
        int fetched;
        int inserted;
        int updated;
        int skipped;
        int failed;
        void add(SyncStats o) {
            fetched  += o.fetched;
            inserted += o.inserted;
            updated  += o.updated;
            skipped  += o.skipped;
            failed   += o.failed;
        }
    }
}
