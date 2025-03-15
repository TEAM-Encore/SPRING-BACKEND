package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserLoginRes(
    String accessToken,
    Boolean isAgreedRequiredTerm,
    Boolean isActivePenalty,
    UserPenaltyInfo userPenaltyInfo
) {

}
