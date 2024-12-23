package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewDataRes(
        @Schema(description = "시야 데이터")
        View view,

        @Schema(description = "음향 데이터")
        Sound sound,

        @Schema(description = "시설 데이터")
        Facility facility,

        @Schema(description = "평점 데이터")
        Rating rating
) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record View(
            @Schema(description = "시야 점수", example = "3")
            Long viewLevel,

            @Schema(description = "시야 리뷰", example = "시야가 너무 좋았어요!")
            String viewReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Sound(
            @Schema(description = "음향 점수", example = "3")
            Long soundLevel,

            @Schema(description = "음향 리뷰", example = "음향이 너무 좋았어요!")
            String soundReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Facility(
            @Schema(description = "시설 점수", example = "3")
            Long facilityLevel,

            @Schema(description = "시설 리뷰", example = "시설이 너무 좋았어요!")
            String facilityReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Rating(
            @Schema(description = "넘버 별점", example = "5")
            Long numberRating,

            @Schema(description = "스토리 구성 별점", example = "5")
            Long storyRating,

            @Schema(description = "재관람 의사 별점", example = "5")
            Long revisitRating,

            @Schema(description = "배우합 별점", example = "5")
            Long actorRating,

            @Schema(description = "퍼포먼스 별점", example = "5")
            Long performanceRating,

            @Schema(description = "총 별점", example = "5.0")
            Float totalRating,

            @Schema(description = "평점 리뷰", example = "평점이 너무 좋았어요!")
            String ratingReview
    ) {
    }
}
