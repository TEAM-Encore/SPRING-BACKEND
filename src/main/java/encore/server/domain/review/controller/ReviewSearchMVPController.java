package encore.server.domain.review.controller;

import encore.server.domain.review.service.ReviewRecentSearchService;
import encore.server.domain.review.service.ReviewRelatedSearchService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.util.redis.SearchLogRedis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mvp/review/search")
@Tag(name = "Review Search (MVP)", description = "리뷰 검색 MVP API")
public class ReviewSearchMVPController {

  private final ReviewRecentSearchService reviewRecentSearchService;
  private final ReviewRelatedSearchService reviewSearchService;

  @GetMapping("/suggestions")
  @Operation(summary = "검색어 자동완성", description = "검색어 자동완성을 제공합니다.")
  public ApplicationResponse<List<String>> getSearchSuggestions(
      @RequestParam("keyword") String keyword,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewSearchService.getAutoCompleteSuggestions(userId, keyword));
  }

  @GetMapping("/recent/logs")
  @Operation(summary = "최근 검색 키워드 조회", description = "사용자의 최근 검색 키워드를 조회합니다.")
  public ApplicationResponse<Set<SearchLogRedis>> getRecentSearchLogs(@LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewRecentSearchService.getRecentSearchLogs(userId));
  }

  @DeleteMapping("/recent/logs/{name}")
  @Operation(summary = "최근 검색 키워드 삭제", description = "사용자의 최근 검색 키워드를 삭제합니다.")
  public ApplicationResponse<Set<SearchLogRedis>> deleteRecentSearchLog(
      @PathVariable("name") String name,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewRecentSearchService.deleteRecentSearchLog(name, userId));
  }
}
