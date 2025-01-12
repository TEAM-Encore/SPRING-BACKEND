package encore.server.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.PreferredKeywordEnum;
import encore.server.domain.user.enumerate.TermType;
import encore.server.domain.user.enumerate.ViewingFrequency;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserPatchReq(
    String nickName,
    String profileImageUrl,
    ViewingFrequency viewingFrequency,
    List<PreferredKeywordEnum> preferredKeywordEnums,
    List<TermType> agreeTermEnums
) {

}
