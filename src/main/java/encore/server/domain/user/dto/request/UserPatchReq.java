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
    @Schema(
        description = "변경할 닉네임.",
        example = "encore123",
        required = false
    )
    String nickName,

    @Schema(
        description = "변경할 프로필 이미지의 URL.",
        example = "https://cdn.encore.com/profile/encore123.png",
        required = false
    )
    String profileImageUrl,

    @Schema(
        description = "동의한 약관 목록. 약관 종류는 Enum(TermType)으로 정의되어 있습니다.",
        example = "[\"SERVICE_TERMS\", \"PRIVACY_POLICY\", \"MARKETING_CONSENT\"]",
        required = false
    )
    List<TermType> agreeTermEnums
) {

}
