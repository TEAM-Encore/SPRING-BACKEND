package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserGetMeRes(
    Long point,
    String nickname,
    Integer numOfSubscriber,
    Integer numOfWritePost,
    List<String> preferredKeywords,
    String viewingFrequency,
    String email
) {

}
