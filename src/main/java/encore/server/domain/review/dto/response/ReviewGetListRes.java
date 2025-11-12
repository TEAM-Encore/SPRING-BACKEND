package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewGetListRes(
    @Schema(description = "리뷰 ID", example = "1")
    Long reviewId,

    @Schema(description = "유저 ID", example = "1")
    Long userId,

    @Schema(description = "리뷰 제목", example = "위키드 리뷰")
    String title,

    @Schema(description = "닉네임", example = "김철수")
    String nickname,

    @Schema(description = "업로드 시점", example = "11분 전")
    String elapsedTime,

    @Schema(description = "리뷰 조회수", example = "100")
    Long viewCount,

    @Schema(description = "리뷰 좋아요 개수", example = "100")
    Long likeCount,

    @Schema(description = "리뷰 내용", example = "넘버 퀄리티부터 배우합까지, 전반적으로 모두 만족스러웠습니다.")
    String content
) {
  public static ReviewGetListRes of(Review review, String elapsedTime) {

    String ratingReview = "";
    if (review.getReviewData() != null) {
      if (review.getReviewData().getRating() != null) {
        ratingReview = Objects.requireNonNullElse(review.getReviewData().getRating().getRatingReview(), "");
      }
    }

    return ReviewGetListRes.builder()
        .reviewId(review.getId())
        .userId(review.getUser().getId())
        .title(review.getTitle())
        .nickname(review.getUser().getNickName())
        .elapsedTime(elapsedTime)
        .viewCount(review.getViewCount())
        .likeCount(review.getLikeCount())
        .content(ratingReview)
        .build();
  }
}
