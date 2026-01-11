package encore.server.domain.review.controller;

import encore.server.domain.review.dto.request.ReviewCreateReq;
import encore.server.domain.review.dto.request.ReviewUpdateReq;
import encore.server.domain.review.dto.response.ReviewCreateRes;
import encore.server.domain.review.dto.response.ReviewDeleteRes;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.domain.review.dto.response.ReviewGetListRes;
import encore.server.domain.review.dto.response.ReviewListCursorBasedRes;
import encore.server.domain.review.dto.response.ReviewMVPLikeRes;
import encore.server.domain.review.dto.response.ReviewReportRes;
import encore.server.domain.review.dto.response.ReviewUpdateRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.enumerate.ReportReason;
import encore.server.domain.review.service.ReviewMVPService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mvp/review")
@Tag(name = "Review (MVP)", description = "리뷰 MVP API")
public class ReviewMVPController {

  private final ReviewMVPService reviewService;

  @GetMapping("/list")
  @Operation(summary = "리뷰 리스트 페이징 조회", description = "리뷰 리스트를 조회합니다.")
  public ApplicationResponse<ReviewListCursorBasedRes<ReviewGetListRes>> getReviewList(
      @RequestParam(name = "search_keyword", required = false) String keyword,
      @RequestParam(name = "cursor", required = false) Long cursor,
      @PageableDefault(size = 3, sort = "id") Pageable pageable,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.getReviewList(userId, keyword, cursor, pageable));
  }

  @GetMapping("/list/me")
  public ApplicationResponse<ReviewListCursorBasedRes<ReviewGetListRes>> getMyReviewList(
      @RequestParam(name = "cursor", required = false) Long cursor,
      @PageableDefault(size = 3, sort = "id") Pageable pageable,
      @Parameter(hidden = true) @LoginUserId Long userId
  ){
    return ApplicationResponse.ok(reviewService.getMyReviewList(userId, cursor, pageable));
  }

  @GetMapping("/list/me/like")
  public ApplicationResponse<ReviewListCursorBasedRes<ReviewGetListRes>> getMyLikedReviewList(
      @RequestParam(name = "cursor", required = false) Long cursor,
      @PageableDefault(size = 3, sort = "id") Pageable pageable,
      @Parameter(hidden = true) @LoginUserId Long userId
  ) {
    return ApplicationResponse.ok(reviewService.getMyLikedReviewList(userId, cursor, pageable));
  }

  @PostMapping("/")
  @Operation(summary = "리뷰 작성", description = "티켓에 대한 리뷰를 작성합니다.")
  public ApplicationResponse<ReviewCreateRes> createReview(
      @RequestBody ReviewCreateReq req,
      @LoginUserId Long userId) {
    return ApplicationResponse.created(reviewService.createReview(userId, req));
  }

  @PutMapping("/{review_id}")
  @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
  public ApplicationResponse<ReviewUpdateRes> updateReview(
      @PathVariable("review_id") Long reviewId,
      @RequestBody ReviewUpdateReq req,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.updateReview(userId, reviewId, req));
  }

  @DeleteMapping("/{review_id}")
  @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
  public ApplicationResponse<ReviewDeleteRes> deleteReview(
      @PathVariable("review_id") Long reviewId,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.deleteReview(userId, reviewId));
  }

  @GetMapping("/{review_id}")
  @Operation(summary = "리뷰 조회", description = "리뷰를 조회합니다.")
  public ApplicationResponse<ReviewDetailRes> getReview(
      @PathVariable("review_id") Long reviewId,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.getReview(userId, reviewId));
  }

  @PostMapping("/{review_id}/unlock")
  @Operation(summary = "리뷰 잠금 해제", description = "리뷰를 잠금 해제합니다.")
  public ApplicationResponse<?> unlockReview(
      @PathVariable("review_id") Long reviewId,
      @LoginUserId Long userId) {
    reviewService.unlockReview(userId, reviewId);
    return ApplicationResponse.ok();
  }

  @PatchMapping("/{review_id}/like")
  @Operation(summary = "리뷰 좋아요", description = "리뷰에 좋아요를 누릅니다.(좋아요 생성+취소)")
  public ApplicationResponse<ReviewMVPLikeRes> likeReview(
      @PathVariable("review_id") Long reviewId,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.likeReview(userId, reviewId));
  }

  @PostMapping("/{review_id}/report")
  @Operation(summary = "리뷰 신고", description = "리뷰를 신고합니다.")
  public ApplicationResponse<ReviewReportRes> reportReview(
      @PathVariable("review_id") Long reviewId,
      @Valid @RequestParam("reason") ReportReason reason,
      @LoginUserId Long userId) {
    return ApplicationResponse.ok(reviewService.reportReview(userId, reviewId, reason));
  }

  @GetMapping("/view-image")
  @Operation(summary = "시야 이미지 조회", description = "랜덤 시야 이미지 4개를 조회합니다.")
  public ApplicationResponse<ViewImageRes> viewImage() {
    return ApplicationResponse.ok(reviewService.viewImage());
  }
}
