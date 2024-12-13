package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.util.List;


@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewRes(
        Long reviewId,
        Long ticketId,
        Long userId,
        String title,
        List<String> tags,
        ReviewDataRes reviewDataRes
) {
}
