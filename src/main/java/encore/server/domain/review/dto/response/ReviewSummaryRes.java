package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewSummaryRes(
        List<ReviewRes> reviews,
        Float averageTotalRating,
        Float averageNumberRating,
        Float averageStoryRating,
        Float averageRevisitRating,
        Float averageActorRating,
        Float averagePerformanceRating
) {
}
