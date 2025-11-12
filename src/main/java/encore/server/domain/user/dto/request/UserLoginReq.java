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
public record UserLoginReq(

        @Schema(
            description = "로그인할 사용자의 이메일 주소.",
            example = "encore@gmail.com",
            required = true
        )
        @NotNull
        String email
) {
    public static UserLoginReq fromOauthAttributes(Map<String,Object> oauthLoginUserInfoAttributes) {
        return UserLoginReq.builder()
                .email((String) oauthLoginUserInfoAttributes.get(OAuth2UserInfo.EMAIL_KEY))
                .build();
    }
}
