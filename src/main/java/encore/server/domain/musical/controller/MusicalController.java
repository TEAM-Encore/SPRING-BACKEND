package encore.server.domain.musical.controller;

import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.service.MusicalService;
import encore.server.domain.musical.service.MusicalSyncService;
import encore.server.domain.musical.service.MusicalSyncService.SyncStats;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/musicals")
@Tag(name = "Musical", description = "뮤지컬 API")
public class MusicalController {

    private final MusicalService musicalService;
    private final MusicalSyncService musicalSyncService;

    @Operation(summary = "뮤지컬 검색", description = "뮤지컬을 키워드로 검색합니다. (?keyword=뮤지컬제목&page=0&size=20)")
    @GetMapping("/search")
    public ApplicationResponse<Page<MusicalRes>> searchMusicals(
            @RequestParam String keyword,
            @ParameterObject @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        Page<MusicalRes> responses = musicalService.searchMusicalsByTitle(keyword, pageable);
        return ApplicationResponse.ok(responses);
    }

    @Operation(summary = "뮤지컬 단건 조회", description = "뮤지컬을 id로 가져옵니다.")
    @GetMapping("/{id}")
    public ApplicationResponse<MusicalDetailRes> getMusical(@PathVariable Long id) {
        MusicalDetailRes responses = musicalService.getMusicalDetail(id);
        return ApplicationResponse.ok(responses);
    }

    /**
     * 초기 적재
     */
    @Operation(
            summary = "뮤지컬 전체 동기화(초기 적재)",
            description = "외부 공연 API에서 전체 기간(더미)으로 조회하여 DB에 업서트합니다."
    )
    @PostMapping("/admin/update-all")
    public ApplicationResponse<SyncStats> initialLoad(@RequestParam String serviceKey) {
        SyncStats stats = musicalSyncService.initialLoad(serviceKey);
        return ApplicationResponse.ok(stats);
    }

    /**
     * 일일 업데이트
     * - 서비스에서 어제(afterDate = now-1) 이후만 가져옴
     */
    @Operation(
            summary = "뮤지컬 일일 동기화",
            description = "어제 이후 등록/수정된 공연만 다시 불러와 DB에 반영합니다."
    )
    @PostMapping("/admin/daily-update")
    public ApplicationResponse<SyncStats> updateDaily(@RequestParam String serviceKey) {
        SyncStats stats = musicalSyncService.updateDaily(serviceKey);
        return ApplicationResponse.ok(stats);
    }

    /**
     * 특정 afterDate(yyyyMMdd) 이후로 등록/수정된 데이터만 다시 가져온다.
     */
    @Operation(
            summary = "afterDate 기준 동기화",
            description = "요청한 일자(yyyyMMdd) 이후 등록/수정된 공연만 다시 가져와 업서트합니다. 비우면 전체입니다."
    )
    @PostMapping("/admin/update-after")
    public ApplicationResponse<SyncStats> updateAfter(
            @RequestParam String serviceKey,
            @RequestParam(required = false) String afterDate // yyyyMMdd, 없으면 전체
    ) {
        SyncStats stats = musicalSyncService.updateAfter(serviceKey, afterDate);
        return ApplicationResponse.ok(stats);
    }
}
