package encore.server.domain.post.dto.response;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Integer numOfLike,
        Integer numOfComment

) {


}
