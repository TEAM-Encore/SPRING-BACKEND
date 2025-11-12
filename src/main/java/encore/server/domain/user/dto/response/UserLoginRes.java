package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserLoginRes(
    @Schema(
        description = "발급된 JWT 액세스 토큰",
        example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    String accessToken,

    @Schema(
        description = "필수 약관 동의 여부 (MVP에서는 사용하지 않음)",
        example = "false",
        deprecated = true
    )
    Boolean isAgreedRequiredTerm,

    @Schema(
        description = "계정 정지 여부 (MVP에서는 사용하지 않음)",
        example = "false",
        deprecated = true
    )
    Boolean isActivePenalty,

    @Schema(
        description = "계정 제재 정보 (MVP에서는 사용하지 않음)",
        implementation = UserPenaltyInfo.class,
        deprecated = true
    )
    UserPenaltyInfo userPenaltyInfo
) {

}
