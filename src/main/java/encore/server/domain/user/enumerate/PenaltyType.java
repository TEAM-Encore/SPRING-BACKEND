package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PenaltyType {
  ONE_WEEK("1주일 정지"),
  TWO_WEEK("2주일 정지"),
  ONE_MONTH("1달 정지"),
  PERM_BAN("영구 정지");

  private final String description;
}
