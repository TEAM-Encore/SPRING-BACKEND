package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TermType {
  SERVICE_TERMS("서비스 이용약관"),
  PRIVACY_POLICY("개인정보 수집 및 이용 동의"),
  MARKETING_CONSENT("마케팅 정보 수신 동의");

  private final String description;
}
