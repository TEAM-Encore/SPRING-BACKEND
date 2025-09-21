package encore.server.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.PreferredKeywordEnum;
import encore.server.domain.user.enumerate.TermType;
import encore.server.domain.user.enumerate.ViewingFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserPatchReq(
    @Schema(description = "닉네임", example = "encore123")
    String nickName,
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/images/profile.png")
    String profileImageUrl,
    @Schema(description = "동의한 약관 타입", example = "[\"SERVICE_TERMS\", \"PRIVACY_POLICY\"]")
    List<TermType> agreeTermEnums
) {

}
