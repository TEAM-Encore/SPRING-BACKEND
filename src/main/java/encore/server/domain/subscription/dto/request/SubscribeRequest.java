package encore.server.domain.subscription.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SubscribeRequest(
    @NotNull(message = "구독할 유저 아이디가 Null 입니다.")
    @Schema(description = "구독할 유저 아이디", example = "2")
    Long followingUserId
) {

}
