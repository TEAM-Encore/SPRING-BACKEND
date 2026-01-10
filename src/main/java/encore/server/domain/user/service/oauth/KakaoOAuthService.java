package encore.server.domain.user.service.oauth;

import encore.server.domain.user.converter.OAuthProfileConverter;
import encore.server.domain.user.dto.profile.KakaoProfileResponse;
import encore.server.domain.user.dto.response.OAuthURLRes;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.service.UserAuthService;
import encore.server.global.config.oauth.KakaoOAuthConfig;
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
public class KakaoOAuthService implements OAuthLoginService {

  private final KakaoOAuthConfig config;
  private final RestTemplate restTemplate = new RestTemplate();
  private final UserAuthService userAuthService;

  @Override
  public OAuthURLRes getLoginUrl() {
    String uriString = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
        .queryParam("client_id", config.getClientId())
        .queryParam("redirect_uri", config.getRedirectUri())
        .queryParam("response_type", "code")
        .queryParam("scope", config.getScope())
        .build().toUriString();
    return new OAuthURLRes(uriString);
  }

  @Override
  public String loginWithCode(String code) {
    String accessToken = getAccessToken(code);
    KakaoProfileResponse userInfo = getUserInfo(accessToken);
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
        .queryParam("isInitialized", login.isInitialized())
        .queryParam("isActivePenalty", login.isActivePenalty())
        .build().toUriString();
  }

  private String getAccessToken(String code) {
    // 1. Access Token 요청
    HttpHeaders tokenHeaders = new HttpHeaders();
    tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
    tokenParams.add("grant_type", "authorization_code");
    tokenParams.add("client_id", config.getClientId());
    tokenParams.add("client_secret", config.getClientSecret());
    tokenParams.add("redirect_uri", config.getRedirectUri());
    tokenParams.add("code", code);

    HttpEntity<?> tokenRequest = new HttpEntity<>(tokenParams, tokenHeaders);
    ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
        "https://kauth.kakao.com/oauth/token", tokenRequest, Map.class
    );

    if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("카카오 access token 요청 실패");
    }

    return (String) Objects.requireNonNull(tokenResponse.getBody()).get("access_token");
  }

  private KakaoProfileResponse getUserInfo(String accessToken) {
    // 2. 사용자 정보 요청
    HttpHeaders profileHeaders = new HttpHeaders();
    profileHeaders.setBearerAuth(accessToken);

    HttpEntity<?> profileRequest = new HttpEntity<>(profileHeaders);
    ResponseEntity<KakaoProfileResponse> profileResponse = restTemplate.exchange(
        "https://kapi.kakao.com/v2/user/me", HttpMethod.GET, profileRequest,
        KakaoProfileResponse.class
    );

    return profileResponse.getBody();
  }
}
