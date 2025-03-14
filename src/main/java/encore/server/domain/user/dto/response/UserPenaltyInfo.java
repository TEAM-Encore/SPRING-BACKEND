package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.PenaltyStatus;
import encore.server.domain.user.enumerate.PenaltyType;
import java.time.LocalDateTime;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserPenaltyInfo(
    PenaltyType penaltyType,
    PenaltyStatus penaltyStatus,
    String reason,
    LocalDateTime issuedAt,
    LocalDateTime expiresAt
) {

}
