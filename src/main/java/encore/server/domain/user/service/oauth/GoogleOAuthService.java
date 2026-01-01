package encore.server.domain.user.service.oauth;

import encore.server.domain.user.converter.OAuthProfileConverter;
import encore.server.domain.user.dto.profile.GoogleUserProfile;
import encore.server.domain.user.dto.response.OAuthURLRes;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.service.UserAuthService;
import encore.server.global.config.oauth.GoogleOAuthConfig;
import encore.server.global.exception.ApplicationException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService implements OAuthLoginService {

  private final GoogleOAuthConfig config;
  private final RestTemplate restTemplate = new RestTemplate();
  private final UserAuthService userAuthService;

  @Override
  public OAuthURLRes getLoginUrl() {
    String uriString = UriComponentsBuilder.fromHttpUrl(
            "https://accounts.google.com/o/oauth2/v2/auth")
        .queryParam("client_id", config.getClientId())
        .queryParam("redirect_uri", config.getRedirectUri())
        .queryParam("response_type", "code")
        .queryParam("scope", config.getScope())
        .queryParam("access_type", "offline")
        .build().toUriString();
    return new OAuthURLRes(uriString);
  }

  @Override
  public String loginWithCode(String code) {
    String accessToken = getAccessToken(code);
    GoogleUserProfile userInfo = getUserInfo(accessToken);
    UserLoginRes login;

    try {
      login = userAuthService.login(OAuthProfileConverter.profileToLoginReq(userInfo));
    } catch (ApplicationException e) {
      userAuthService.signup(OAuthProfileConverter.profileToSignupReq(userInfo));
      login = userAuthService.login(OAuthProfileConverter.profileToLoginReq(userInfo));
    }

    String bearerAccessToken = login.accessToken().substring(7);

    return UriComponentsBuilder.fromUriString("encore://oauth")
        .queryParam("token", bearerAccessToken)
        .queryParam("isAgreedRequiredTerm", login.isAgreedRequiredTerm())
        .queryParam("isActivePenalty", login.isActivePenalty())
        .build().toUriString();
  }

  private String getAccessToken(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("code", code);
    body.add("client_id", config.getClientId());
    body.add("client_secret", config.getClientSecret());
    body.add("redirect_uri", config.getRedirectUri());
    body.add("grant_type", "authorization_code");

    HttpEntity<?> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(
        "https://oauth2.googleapis.com/token", request, Map.class
    );

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("구글 access token 요청 실패");
    }

    return (String) Objects.requireNonNull(response.getBody()).get("access_token");
  }

  private GoogleUserProfile getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<GoogleUserProfile> response = restTemplate.exchange(
        "https://www.googleapis.com/oauth2/v3/userinfo",
        HttpMethod.GET,
        request,
        GoogleUserProfile.class
    );

    return response.getBody();
  }
}
