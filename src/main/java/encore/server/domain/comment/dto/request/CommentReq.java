package encore.server.domain.comment.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.comment.entity.Comment;
import encore.server.domain.post.entity.Post;
import encore.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommentReq(
        @Schema(description = "내용", example = "댓글 내용")
        String content,

        @Nullable
        @Schema(description = "부모 댓글 ID", example = "1")
        Long parentId
) {
}
