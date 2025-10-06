package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalOpenApiConverter;
import encore.server.domain.musical.dto.external.MusicalOpenApiDto;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final String START_DATE = "20250101";      // 초기 적재 기준
    private static final String END_DATE   = "20991231";      // 무제한
    private static final int PAGE_SIZE     = 100;

    private final DateTimeFormatter yyyymmdd = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public void initialLoad(String serviceKey) {
        log.info("[MusicalSyncService] 초기 적재 시작 ({} ~ {})", START_DATE, END_DATE);
        SyncStats total = syncRange(serviceKey, START_DATE, END_DATE, null);
        log.info("[MusicalSyncService] 초기 적재 완료: fetched={} inserted={} updated={} skipped={} failed={}",
                total.fetched, total.inserted, total.updated, total.skipped, total.failed);
    }

    @Transactional
    public void updateDaily(String serviceKey) {
        String today = LocalDate.now().format(yyyymmdd);
        log.info("[MusicalSyncService] 일일 갱신 시작 (afterDate={})", today);
        SyncStats total = syncRange(serviceKey, START_DATE, END_DATE, today);
        log.info("[MusicalSyncService] 일일 갱신 완료: fetched={} inserted={} updated={} skipped={} failed={}",
                total.fetched, total.inserted, total.updated, total.skipped, total.failed);
    }

    private SyncStats syncRange(String serviceKey, String startDate, String endDate, String afterDate) {
        int page = 1;
        SyncStats total = new SyncStats();

        while (true) {
            List<MusicalOpenApiDto> batch = externalMusicalApiClient.fetchMusicals(
                    serviceKey, startDate, endDate, page, PAGE_SIZE, afterDate);

            if (batch.isEmpty()) break;

            total.fetched += batch.size();
            SyncStats pageStats = upsert(batch);
            total.add(pageStats);

            if (batch.size() < PAGE_SIZE) break;
            page++;
        }
        return total;
    }

    /** Map 기반 Upsert (N+1 제거) */
    private SyncStats upsert(List<MusicalOpenApiDto> apiData) {
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
                    MusicalOpenApiConverter.updateEntity(exist, dto); // 엔티티 필드 갱신 (점 포맷 날짜 파서)
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
            musicalRepository.saveAll(toInsert);
        }

        log.info("[MusicalSyncService] upsert: inserted={} updated={} skipped={} failed={}",
                stats.inserted, stats.updated, stats.skipped, stats.failed);

        return stats;
    }

    /** 간단 집계용 */
    private static class SyncStats {
        int fetched; int inserted; int updated; int skipped; int failed;
        void add(SyncStats o) {
            fetched += o.fetched; inserted += o.inserted; updated += o.updated; skipped += o.skipped; failed += o.failed;
        }
    }
}
