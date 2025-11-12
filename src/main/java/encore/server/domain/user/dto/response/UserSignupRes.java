package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserSignupRes(
    @Schema(description = "회원가입에 성공한 사용자 이메일", example = "encore@naver.com")
    String email
) {

}
