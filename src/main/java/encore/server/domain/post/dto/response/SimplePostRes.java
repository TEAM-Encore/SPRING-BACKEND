package encore.server.domain.post.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SimplePostRes(

        @Schema(description = "게시글 ID", example = "1")
        Long id,

        @Schema(description = "게시글 제목", example = "게시글 제목", maxLength = 30)
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용", maxLength = 30)
        String content,

        @Schema(description = "게시글 카테고리", example = "OPERA_GLASS_RENTAL | MUSICAL_TERMS | EVENTS | VIEW_REVIEW | GOODS_REVIEW | PERFORMANCE_REVIEW")
        String category,

        @Schema(description = "게시글 타입", example = "INFORMATION | FREE | ACTOR | REVIEW")
        String type,

        @Schema(description = "게시글 썸네일", example = "게시글 썸네일")
        String thumbnail,

        @Schema(description = "게시글 좋아요 수", example = "1")
        Long likeCount,

        @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "true")
        Boolean isLiked,

        @Schema(description = "게시글 댓글 수", example = "1")
        Long commentCount,

        @Schema(description = "게시글 작성자 ID", example = "1")
        Long userId,

        @Schema(description = "게시글 작성자 닉네임", example = "작성자 닉네임")
        String nickname,

        @Schema(description = "게시글 생성일", example = "2021-07-01 00:00:00")
        LocalDateTime createdAt
) {
}