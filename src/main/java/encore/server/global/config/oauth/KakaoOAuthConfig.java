package encore.server.global.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kakao")
@Getter
@Setter
public class KakaoOAuthConfig {
  private String clientId;
  private String clientSecret;
  private String redirectUri;
  private String scope;
}
