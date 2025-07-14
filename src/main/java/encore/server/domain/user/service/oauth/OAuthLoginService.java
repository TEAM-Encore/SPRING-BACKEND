package encore.server.domain.user.service.oauth;

import encore.server.domain.user.dto.response.OAuthURLRes;

public interface OAuthLoginService {
  OAuthURLRes getLoginUrl();
  String loginWithCode(String code);
}
