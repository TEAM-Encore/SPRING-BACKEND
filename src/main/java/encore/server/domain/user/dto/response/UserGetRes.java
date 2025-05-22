package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserGetRes(
    Long userId,
    String nickname,
    Long numOfSubscriber,
    Long numOfWritePost,
    List<String> preferredKeywords,
    String viewingFrequency
) {

}
