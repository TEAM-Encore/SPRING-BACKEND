package encore.server.domain.review.controller;

import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.*;
import encore.server.domain.review.service.ReviewSearchService;
import encore.server.domain.review.service.ReviewService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.util.redis.SearchLogRedis;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/review")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewSearchService reviewSearchService;

    @PostMapping("/{ticket_id}")
    @Operation(summary = "리뷰 작성", description = "티켓에 대한 리뷰를 작성합니다.")
    public ApplicationResponse<ReviewDetailRes> createReview(@PathVariable("ticket_id") Long ticketId,
                                                       @RequestBody ReviewReq req) {
        Long userId = getUserId();
        return ApplicationResponse.created(reviewService.createReview(ticketId, userId, req));
    }

    @PutMapping("/{review_id}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    public ApplicationResponse<ReviewDetailRes> updateReview(@PathVariable("review_id") Long reviewId,
                                                       @RequestBody ReviewReq req) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewService.updateReview(userId, reviewId, req));
    }

    @DeleteMapping("/{review_id}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    public ApplicationResponse<?> deleteReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        reviewService.deleteReview(userId, reviewId);
        return ApplicationResponse.ok();
    }

    @GetMapping("/view-image/{cycle}")
    @Operation(summary = "시야 이미지 조회", description = "cycle 별로 시야 이미지를 조회합니다.")
    public ApplicationResponse<ViewImageRes> viewImage(@PathVariable("cycle") Long cycle) {
        return ApplicationResponse.ok(reviewService.viewImage(cycle));
    }

    @PostMapping("/{review_id}/unlock")
    @Operation(summary = "리뷰 잠금 해제", description = "리뷰를 잠금 해제합니다.")
    public ApplicationResponse<?> unlockReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        reviewService.unlockReview(userId, reviewId);
        return ApplicationResponse.ok();
    }

    @GetMapping("/{review_id}")
    @Operation(summary = "리뷰 조회", description = "리뷰를 조회합니다.")
    public ApplicationResponse<ReviewDetailRes> getReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewService.getReview(userId, reviewId));
    }

    @GetMapping("/user-reviews")
    @Operation(summary = "유저 리뷰 조회", description = "유저의 리뷰 리스트를 조회합니다.(현재 보고 있는 review_id 제외)")
    public ApplicationResponse<List<ReviewSimpleRes>> getUserReviews(
            @RequestParam(value = "user_id") Long userId,
            @RequestParam(value = "review_id", required = false) Long reviewId) {
        return ApplicationResponse.ok(reviewService.getUserReviews(userId, reviewId));
    }

    @PatchMapping("/{review_id}/like")
    @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 누릅니다.(좋아요 생성+취소)")
    public ApplicationResponse<ReviewLikeRes> likeReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewService.likeReview(userId, reviewId));
    }

    @GetMapping("/popular-list")
    @Operation(summary = "인기 리뷰 리스트 조회", description = "인기 리뷰 리스트를 조회합니다.")
    public ApplicationResponse<List<ReviewSimpleRes>> getPopularReviewList() {
        return ApplicationResponse.ok(reviewService.getPopularReviewList());
    }

    @GetMapping("/list")
    @Operation(summary = "리뷰 리스트 페이징 조회", description = "리뷰 리스트를 조회합니다.")
    public ApplicationResponse<Slice<ReviewSimpleRes>> getReviewList(
            @RequestParam(name= "search_keyword", required = false) String keyword,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "tag", required = false) String tag,
            @PageableDefault(size = 3, sort = "createdAt") Pageable pageable) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewService.getReviewList(userId, keyword, cursor, tag, pageable));
    }

    @GetMapping("/recent-search-logs")
    @Operation(summary = "최근 검색 키워드 조회", description = "사용자의 최근 검색 키워드를 조회합니다.")
    public ApplicationResponse<Set<SearchLogRedis>> getRecentSearchLogs() {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewSearchService.getRecentSearchLogs(userId));
    }

    @DeleteMapping("/recent-search-logs")
    @Operation(summary = "최근 검색 키워드 삭제", description = "사용자의 최근 검색 키워드를 삭제합니다.")
    public ApplicationResponse<Set<SearchLogRedis>> deleteRecentSearchLog(@RequestParam("name") String name) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewSearchService.deleteRecentSearchLog(name, userId));
    }

    @DeleteMapping("/recent-search-logs/all")
    @Operation(summary = "최근 검색 키워드 전체 삭제", description = "사용자의 최근 검색 키워드를 전체 삭제합니다.")
    public ApplicationResponse<?> deleteAllRecentSearchLogs() {
        Long userId = getUserId();
        reviewSearchService.deleteAllRecentSearchLogs(userId);
        return ApplicationResponse.ok();
    }

    private Long getUserId() {
        return 1L;
    }
}
