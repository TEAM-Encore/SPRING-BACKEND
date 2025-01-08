package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.enumerate.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewLikeRes(
        @Schema(description = "좋아요 종류", example = "FOLLOW_UP_RECOMMENDATION | FULL_OF_TIPS | THOROUGH_ANALYSIS")
        LikeType likeType,

        LikeCountRes likeCountRes

) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record LikeCountRes(

            @Schema(description = "총 좋아요 개수", example = "100")
            Long totalLikeCount,
            @Schema(description = "후속 추천 좋아요 개수", example = "100")
            Long followUpLikeCount,

            @Schema(description = "꿀팁 가득 좋아요 개수", example = "100")
            Long fullOfTipsLikeCount,

            @Schema(description = "꼼꼼 분석 좋아요 개수", example = "100")
            Long thoroughAnalysisLikeCount
    ){
        public static LikeCountRes of(Review review) {
            return LikeCountRes.builder()
                    .totalLikeCount(review.getLikeCount())
                    .followUpLikeCount(review.getFollowUpLikeCount())
                    .fullOfTipsLikeCount(review.getFullOfTipsLikeCount())
                    .thoroughAnalysisLikeCount(review.getThoroughAnalysisLikeCount())
                    .build();
        }
    }

    public static ReviewLikeRes of(LikeType likeType, Review review) {
        return ReviewLikeRes.builder()
                .likeType(likeType != null ? likeType : LikeType.NONE)
                .likeCountRes(LikeCountRes.of(review))
                .build();
    }
}
