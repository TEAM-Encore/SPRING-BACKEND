package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewSimpleRes(
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

        @Schema(description = "리뷰 총평점과 내용")
        ReviewDataRes.Rating rating
) {
        public static ReviewSimpleRes of(Review review, String elapsedTime) {
                ReviewDataRes.Rating rating = review.getReviewData() != null ? ReviewDataRes.of(review.getReviewData()).rating() : null;
                return ReviewSimpleRes.builder()
                        .reviewId(review.getId())
                        .userId(review.getUser().getId())
                        .title(review.getTitle())
                        .nickname(review.getUser().getNickName())
                        .elapsedTime(elapsedTime)
                        .viewCount(review.getViewCount())
                        .likeCount(review.getLikeCount())
                        .rating(rating)
                        .build();
        }
}
