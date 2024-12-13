package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewLikeRes(
        @Schema(description = "좋아요 여부", example = "true")
        Boolean isLike,

        @Schema(description = "좋아요 수", example = "100")
        Long likeCount
) {
}
