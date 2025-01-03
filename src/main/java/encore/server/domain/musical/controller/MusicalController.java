package encore.server.domain.musical.controller;

import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.dto.response.MusicalSeriesRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.service.MusicalService;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/musical")
@Tag(name = "Musical", description = "뮤지컬 API")
public class MusicalController {
    private final MusicalService musicalService;

    @Operation(summary = "뮤지컬 검색", description = "뮤지컬을 키워드로 검색합니다.")
    @GetMapping("/search")
    public ApplicationResponse<List<MusicalRes>> searchMusicals(@RequestParam String keyword) {
        List<MusicalRes> responses = musicalService.searchMusicalsByTitle(keyword);
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



}

