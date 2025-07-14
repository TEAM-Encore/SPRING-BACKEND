package encore.server.global.config.oauth;

import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.service.oauth.GoogleOAuthService;
import encore.server.domain.user.service.oauth.KakaoOAuthService;
import encore.server.domain.user.service.oauth.OAuthLoginService;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthLoginServiceConfig {

  @Bean
  public Map<AuthProvider, OAuthLoginService> loginServices(
      KakaoOAuthService kakao,
      GoogleOAuthService google) {

    return Map.of(
        AuthProvider.KAKAO, kakao,
        AuthProvider.GOOGLE, google
    );
  }
}
