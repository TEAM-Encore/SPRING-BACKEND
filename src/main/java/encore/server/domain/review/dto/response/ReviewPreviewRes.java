package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.enumerate.LikeType;
import encore.server.domain.ticket.entity.Actor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewPreviewRes(
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

        @Schema(description = "리뷰 좋아요 데이터")
        ReviewLikeRes likeData,

        @Schema(description = "리뷰 총평점과 내용")
        ReviewDataRes.Rating rating,

        @Schema(description = "장소")
        String location,

        @Schema(description = "좌석")
        String seat,

        @Schema(description = "배우", example = "김철수")
        List<String> actors
) {

    public static ReviewPreviewRes of(Review review, String elapsedTime, LikeType likeType) {
        return ReviewPreviewRes.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .title(review.getTitle())
                .nickname(review.getUser().getNickName())
                .elapsedTime(elapsedTime)
                .viewCount(review.getViewCount())
                .likeData(ReviewLikeRes.of(likeType, review))
                .rating(ReviewDataRes.of(review.getReviewData()).rating())
                .location(review.getTicket().getMusical().getLocation())
                .build();

    }
}
