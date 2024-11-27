package encore.server.domain.post.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailsGetRes(

        Long postId,
        Long userId,
        String nickName,
        String profileImageUrl,
        String title,
        String content,
        Boolean isNotice,
        Boolean isTemporarySave,
        String postType,
        String category,
        List<String> hashtags,
        List<String> postImages,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Boolean isModified,
        Long numOfLike,
        Long numOfComment,
        Boolean isLiked

) {


}
