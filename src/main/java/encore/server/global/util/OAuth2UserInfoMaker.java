package encore.server.global.util;

import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.enumerate.UserRole;
import encore.server.global.auth.OAuth2UserInfo;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Builder
@Getter
@Component
public class OAuth2UserInfoMaker {

    public OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "apple" -> ofApple(attributes);
            default -> throw new ApplicationException(ErrorCode.INVALID_VALUE_EXCEPTION);
        };
    }

    private OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .provider(AuthProvider.GOOGLE)
                .role(UserRole.BASIC)
                .build();
    }

    private OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .provider(AuthProvider.KAKAO)
                .role(UserRole.BASIC)
                .build();
    }

    private OAuth2UserInfo ofApple(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("response");
        return OAuth2UserInfo.builder()
                .name((String) account.get("nickname"))
                .email((String) account.get("email"))
                .provider(AuthProvider.APPLE)
                .role(UserRole.BASIC)
                .build();
    }
}
