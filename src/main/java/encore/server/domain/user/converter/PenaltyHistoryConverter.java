package encore.server.domain.user.converter;

import encore.server.domain.user.dto.request.UserImposePenaltyReq;
import encore.server.domain.user.entity.PenaltyHistory;
import encore.server.domain.user.entity.User;
import java.time.LocalDateTime;

public class PenaltyHistoryConverter {

  public static PenaltyHistory toEntity(UserImposePenaltyReq request, User user,
      LocalDateTime expiresAt) {
    return PenaltyHistory.builder()
        .type(request.type())
        .reason(request.reason())
        .issuedAt(LocalDateTime.now())
        .user(user)
        .status(request.status())
        .expiresAt(expiresAt)
        .build();
  }

}
