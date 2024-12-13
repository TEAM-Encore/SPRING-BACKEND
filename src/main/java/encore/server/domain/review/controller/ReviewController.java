package encore.server.domain.review.controller;

import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.service.ReviewService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/review")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{ticket_id}")
    public ApplicationResponse<ReviewRes> createReview(@PathVariable("ticket_id") Long ticketId,
                                                       @RequestBody ReviewReq req) {
        Long userId = getUserId();
        return ApplicationResponse.created(reviewService.createReview(ticketId, userId, req));
    }

    @GetMapping("/view-image/{cycle}")
    public ApplicationResponse<ViewImageRes> viewImage(@PathVariable("cycle") Long cycle) {
        return ApplicationResponse.ok(reviewService.viewImage(cycle));
    }

    @PostMapping("/{review_id}/unlock")
    public ApplicationResponse<?> unlockReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        reviewService.unlockReview(userId, reviewId);
        return ApplicationResponse.ok();
    }

    @GetMapping("/{review_id}")
    public ApplicationResponse<ReviewDetailRes> getReview(@PathVariable("review_id") Long reviewId) {
        Long userId = getUserId();
        return ApplicationResponse.ok(reviewService.getReview(userId, reviewId));
    }

    private Long getUserId() {
        return 1L;
    }
}
