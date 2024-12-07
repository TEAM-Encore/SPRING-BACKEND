package encore.server.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.AuthProvider;
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
        @Schema(description = "비밀번호", example = "password")
        @NotNull
        String password,
        @Schema(description = "이름", example = "앙코르")
        @NotNull
        String name,
        @Schema(description = "로그인 제공자", example = "GOOGLE")
        @NotNull
        AuthProvider provider

) {

    public static UserSignupReq fromOauthAttributes(Map<String,Object> oauthLoginUserInfoAttributes) {
        return UserSignupReq.builder()
                .email((String) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.EMAIL_KEY))
                .password((String) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.EMAIL_KEY)) // 비밀번호와 이메일이 동일하게 설정되는 경우
                .name((String) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.NAME_KEY))
                .provider((AuthProvider) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.PROVIDER_KEY))
                .build();
    }

}
