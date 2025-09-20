package encore.server.domain.musical.controller;

import encore.server.domain.musical.dto.request.MusicalCreateReq;
import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.dto.response.MusicalSeriesRes;
import encore.server.domain.musical.dto.response.MusicalSimpleRes;
import encore.server.domain.musical.service.MusicalService;
import encore.server.global.common.ApplicationResponse;
import encore.server.domain.musical.service.MusicalListService;
import encore.server.global.exception.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/musical")
@Tag(name = "Musical", description = "뮤지컬 API")
public class MusicalController {
    private final MusicalService musicalService;
    private final MusicalListService musicalListService;

    @Operation(summary = "뮤지컬 검색", description = "뮤지컬을 제목으로 검색합니다.")
    @GetMapping("/search")
    public ApplicationResponse<List<MusicalRes>> searchMusicals(@RequestParam String title) {
        List<MusicalRes> responses = musicalService.searchMusicalsByTitle(title);
        return ApplicationResponse.ok(responses);
    }

    @Operation(summary = "뮤지컬 상세조회", description = "뮤지컬을 상세조회합니다.")
    @GetMapping("/{musical_id}")
    public ApplicationResponse<MusicalDetailRes> getMusicalDetail(@PathVariable("musical_id") Long musicalId) {
        return ApplicationResponse.ok(musicalService.getMusicalDetail(musicalId));
    }

    @Operation(summary = "같은 제목의 모든 Series 조회", description = "뮤지컬 ID로 같은 제목의 모든 Series를 조회합니다.")
    @GetMapping("/{musical_id}/all-series")
    public ApplicationResponse<List<MusicalSeriesRes>> getAllSeries(@PathVariable("musical_id") Long musicalId) {
        List<MusicalSeriesRes> responses = musicalService.getAllSeriesByMusicalId(musicalId);
        return ApplicationResponse.ok(responses);
    }

//    @Operation(summary = "이달의 인기 뮤지컬 조회", description = "이달의 인기 뮤지컬 8개를 조회합니다.")
//    @GetMapping("/featured")
//    public ApplicationResponse<List<MusicalSimpleRes>> getFeaturedMusicals() {
//        List<MusicalSimpleRes> musicals = musicalService.getFeaturedMusicals();
//        return ApplicationResponse.ok(musicals);
//    }

    @Operation(summary = "개봉 예정 뮤지컬 조회", description = "개봉이 임박한 뮤지컬을 최대 8개 조회합니다.")
    @GetMapping("/upcoming")
    public ApplicationResponse<List<MusicalSimpleRes>> getUpcomingMusicals() {
        List<MusicalSimpleRes> responses = musicalService.getUpcomingMusicals();
        return ApplicationResponse.ok(responses);
    }

    // Todo: prod환경 테스트 후 스케줄링 할 예정
    @Operation(summary = "뮤지컬 정보 크롤링 및 저장", description = "Interpark 뮤지컬 페이지에서 뮤지컬 정보를 크롤링하여 저장합니다.")
    @GetMapping("/crawling")
    public ApplicationResponse<String> crawlingMusicalInfo() {
        musicalListService.crawlingMusicalInfo();
        return ApplicationResponse.ok("크롤링 완료");
    }

    @Operation(summary = "뮤지컬 추가", description = "새로운 뮤지컬을 추가합니다.")
    @PostMapping("/create")
    public ApplicationResponse<MusicalDetailRes> addMusical(@RequestBody MusicalCreateReq request) {
        MusicalDetailRes response = musicalService.addMusical(request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "뮤지컬 수정", description = "입력된 필드만 수정합니다.")
    @PatchMapping("/{musical_id}")
    public ApplicationResponse<MusicalDetailRes> updateMusical(
            @PathVariable("musical_id") Long musicalId,
            @RequestBody MusicalCreateReq request) {
        MusicalDetailRes response = musicalService.updateMusical(musicalId, request);
        return ApplicationResponse.ok(response);
    }

    @Operation(summary = "뮤지컬 삭제", description = "뮤지컬을 삭제합니다.")
    @DeleteMapping("/{musical_id}")
    public ApplicationResponse<Void> deleteMusical(@PathVariable("musical_id") Long musicalId) {
        musicalService.deleteMusical(musicalId);
        return ApplicationResponse.ok();
    }

}

