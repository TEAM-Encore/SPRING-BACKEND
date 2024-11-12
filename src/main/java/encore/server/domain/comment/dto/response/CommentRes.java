package encore.server.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommentRes(
        @Schema(description = "댓글 ID", example = "1")
        Long id,

        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "부모 댓글 ID", example = "1")
        Long parentCommentId,

        @Schema(description = "내용", example = "댓글 내용")
        String content,

        @Schema(description = "작성자 여부", example = "true")
        Boolean isPostOwner,

        @Schema(description = "나의 댓글인지 여부", example = "true")
        Boolean isMyComment,

        @Schema(description = "생성일시", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일시", example = "2021-07-01T00:00:00")
        LocalDateTime modifiedAt
) {}
