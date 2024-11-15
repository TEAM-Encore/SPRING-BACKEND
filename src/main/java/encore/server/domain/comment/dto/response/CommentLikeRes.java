package encore.server.domain.comment.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommentLikeRes(
        Long commentId,
        Long userId,
        Boolean isLiked,
        Long likeCount){
}
