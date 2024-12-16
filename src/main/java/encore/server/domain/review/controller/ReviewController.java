package encore.server.domain.review.controller;

import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.dto.response.ReviewSimpleRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.service.ReviewService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/review")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{ticket_id}")
    @Operation(summary = "리뷰 작성", description = "티켓에 대한 리뷰를 작성합니다.")
    public ApplicationResponse<ReviewRes> createReview(@PathVariable("ticket_id") Long ticketId,
                                                       @RequestBody ReviewReq req) {
        Long userId = getUserId();
        return ApplicationResponse.created(reviewService.createReview(ticketId, userId, req));
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

    private Long getUserId() {
        return 1L;
    }
}
