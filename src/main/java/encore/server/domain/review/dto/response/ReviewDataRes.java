package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewDataRes(
        View view,
        Sound sound,
        Facility facility,
        Rating rating
) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record View(
            Long viewLevel,
            String viewReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Sound(
            Long soundLevel,
            String soundReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Facility(
            Long facilityLevel,
            String facilityReview
    ) {
    }

    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Rating(
            Long numberRating,
            Long storyRating,
            Long revisitRating,
            Long actorRating,
            Long performanceRating,
            Float totalRating,
            String ratingReview
    ) {
    }
}
