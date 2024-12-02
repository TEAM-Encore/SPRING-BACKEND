package encore.server.domain.review.controller;

import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewRes;
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

    private Long getUserId() {
        return 1L;
    }
}
