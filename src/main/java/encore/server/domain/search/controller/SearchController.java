package encore.server.domain.search.controller;


import encore.server.domain.post.service.PostRelatedSearchService;
import encore.server.domain.review.service.ReviewRelatedSearchService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
@Tag(name = "Search", description = "전체검색 API")
public class SearchController {

    private final ReviewRelatedSearchService reviewSearchService;
    private final PostRelatedSearchService postSearchService;

    @GetMapping("/search-suggestions")
    @Operation(summary = "전체 검색어 자동완성", description = "게시판과 리뷰의 검색어 자동완성을 제공합니다.")
    public ApplicationResponse<List<String>> getSearchSuggestions(@RequestParam("keyword") String keyword) {
        Long userId = getUserId();

        // 각 서비스에서 자동완성 결과 가져오기
        List<String> postSuggestions = postSearchService.getAutoCompleteSuggestions(userId, keyword);
        List<String> reviewSuggestions = reviewSearchService.getAutoCompleteSuggestions(userId, keyword);

        // 두 결과 리스트 합치기
        Set<String> combinedSuggestions = new HashSet<>();
        combinedSuggestions.addAll(postSuggestions);
        combinedSuggestions.addAll(reviewSuggestions);

        // 정렬된 리스트로 반환
        List<String> sortedSuggestions = new ArrayList<>(combinedSuggestions);
        Collections.sort(sortedSuggestions);

        return ApplicationResponse.ok(sortedSuggestions);
    }


    private Long getUserId() {
        return 1L;
    }
}
