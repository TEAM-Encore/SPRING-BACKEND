package encore.server.domain.post.converter;


import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.request.PostUpdateReq;
import encore.server.domain.post.dto.response.PostDetailsGetRes;
import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostImage;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class PostConverter {

    //PostCreateReq -> Post 로 변환
    public Post convert(PostCreateReq postCreateReq, User user){
        return new Post(user,
                postCreateReq.title(),
                postCreateReq.content(),
                postCreateReq.isNotice(),
                postCreateReq.isTemporarySave(),
                PostType.valueOf(postCreateReq.postType()),
                Category.valueOf(postCreateReq.category()),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    //PostUpdateReq -> Post 로 변환
    public Post convert(PostUpdateReq postUpdateReq, User user){
        return new Post(user,
                postUpdateReq.title(),
                postUpdateReq.content(),
                postUpdateReq.isNotice(),
                postUpdateReq.isTemporarySave(),
                PostType.valueOf(postUpdateReq.postType()),
                Category.valueOf(postUpdateReq.category()),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public PostDetailsGetRes postDetailsGetResFrom(
            Post post, List<String> hashtags, List<String> postImages,
            String userName, Integer numOfLike, Integer numOfComment
    ) {

        Boolean isModified = true;

        if (Objects.equals(post.getModifiedAt(), post.getCreatedAt())) {
            isModified = false;
        }

        return new PostDetailsGetRes(
                post.getId(),
                userName,
                post.getTitle(),
                post.getContent(),
                post.getIsNotice(),
                post.getIsTemporarySave(),
                post.getPostType().name(),
                post.getCategory().name(),
                hashtags,
                postImages,
                post.getCreatedAt(),
                post.getModifiedAt(),
                isModified,
                numOfLike,
                numOfComment
        );

    }

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
