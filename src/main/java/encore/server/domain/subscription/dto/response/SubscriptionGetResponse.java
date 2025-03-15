package encore.server.domain.subscription.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SubscriptionGetResponse(
    Long userId,
    String nickname,
    String profileImageUrl,
    LocalDateTime createdAt
) {

}
