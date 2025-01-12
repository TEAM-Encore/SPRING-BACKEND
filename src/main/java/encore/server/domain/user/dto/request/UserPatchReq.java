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
    String profileImageUrl,
    @Schema(description = "관람 빈도", example = "LEVEL1")
    ViewingFrequency viewingFrequency,
    @Schema(description = "선호하는 공연 키워드", example = "[\"EMOTIONAL\", \"ENTERTAINING\"]")
    List<PreferredKeywordEnum> preferredKeywordEnums,
    @Schema(description = "동의한 약관 타입", example = "[\"SERVICE_TERMS\", \"PRIVACY_POLICY\"]")
    List<TermType> agreeTermEnums
) {

}
