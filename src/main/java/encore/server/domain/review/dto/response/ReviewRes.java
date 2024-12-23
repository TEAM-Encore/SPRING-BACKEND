package encore.server.domain.review.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewRes(
        String title,
        String nickName,
        Float totalRating,
        Long viewCount,
        Long likeCount
) {
}
