package encore.server.global.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "google")
@Component
@Getter
@Setter
public class GoogleOAuthConfig {
  private String clientId;
  private String clientSecret;
  private String redirectUri;
  private String scope;
}
