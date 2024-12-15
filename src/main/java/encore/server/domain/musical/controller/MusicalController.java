package encore.server.domain.musical.controller;

import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.service.MusicalService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}

