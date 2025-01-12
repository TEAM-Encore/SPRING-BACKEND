package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.ReviewReport;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewReportRes(
        Long reviewId,
        Long reporterId,
        String reason
) {
    public static ReviewReportRes of(ReviewReport reviewReport){
        return ReviewReportRes.builder()
                .reviewId(reviewReport.getReview().getId())
                .reporterId(reviewReport.getReporter().getId())
                .reason(reviewReport.getReason().getReason())
                .build();
    }
}
