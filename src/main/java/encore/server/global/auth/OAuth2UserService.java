package encore.server.global.auth;

import encore.server.global.util.OAuth2UserInfoMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserInfoMaker oAuth2UserInfoMaker;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기 (Json 형태, provider 마다 형식이 다름)
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. provider id 가져오기 ex) google, kakao, apple
        String providerId = userRequest.getClientRegistration().getRegistrationId();

        // 3.OAuth 유저 정보 생성 후 반환
        return oAuth2UserInfoMaker.of(providerId, oAuth2UserAttributes);
    }

}
