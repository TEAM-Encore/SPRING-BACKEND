package encore.server.domain.post.converter;

import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.user.entity.User;

public class PostConverter {
    public static final SimplePostRes toSimplePostRes(Post post, User user) {
        return SimplePostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory() != null ? post.getCategory().getCategory() : null)
                .type(post.getPostType() != null ? post.getPostType().getType() : null)
                .thumbnail(post.getPostImages().isEmpty() ? null : post.getPostImages().get(0).getUrl())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .userId(user.getId())
                .nickname(user.getNickName())
                .build();
    }
}