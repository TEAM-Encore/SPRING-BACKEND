package encore.server.domain.post.dto.response;

import encore.server.domain.term.dto.MusicalTermRes;

import java.time.LocalDateTime;
import java.util.List;

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
        Boolean isLiked,
        Integer numOfLike,
        Long numOfComment,
        List<MusicalTermRes> terms
) {
}
