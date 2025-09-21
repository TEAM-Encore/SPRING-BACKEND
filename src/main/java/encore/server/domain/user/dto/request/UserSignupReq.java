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
        @Schema(description = "이메일", example = "encore@gmail.com")
        @NotNull
        String email,
        @Schema(description = "로그인 제공자", example = "GOOGLE")
        @NotNull
        AuthProvider provider,
        @Schema(description = "권한", example = "BASIC")
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
