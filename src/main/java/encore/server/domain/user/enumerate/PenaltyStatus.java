package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PenaltyStatus {
  ACTIVE("현재 적용 중"),
  EXPIRED("만료됨"),
  REVOKED("해제됨");

  private final String description;
}
