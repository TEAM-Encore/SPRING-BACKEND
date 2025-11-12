package encore.server.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.enumerate.UserRole;
import encore.server.global.auth.OAuth2UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserSignupReq(

        @Schema(
            description = "회원 이메일 주소.",
            example = "encore@gmail.com",
            required = true
        )
        @NotNull
        String email,

        @Schema(
            description = "로그인 제공자. OAuth 인증을 통해 들어온 플랫폼 정보입니다.",
            example = "GOOGLE",
            required = true,
            allowableValues = {"GOOGLE", "KAKAO"}
        )
        @NotNull
        AuthProvider provider,

        @Schema(
            description = "사용자 권한. MVP 에서는 권한에 따른 로직 분리가 없습니다.",
            example = "BASIC",
            required = true,
            allowableValues = {"BASIC", "MANAGER"}
        )
        @NotNull
        UserRole role
) {

    public static UserSignupReq fromOauthAttributes(Map<String,Object> oauthLoginUserInfoAttributes) {
        return UserSignupReq.builder()
                .email((String) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.EMAIL_KEY))
                .provider((AuthProvider) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.PROVIDER_KEY))
                .role((UserRole) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.ROLE_KEY))
                .build();
    }

}
