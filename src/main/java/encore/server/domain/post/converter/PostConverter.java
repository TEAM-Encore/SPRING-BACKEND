package encore.server.domain.post.converter;


import encore.server.domain.term.converter.MusicalTermConverter;
import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.response.PostDetailsGetRes;
import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.enumerate.Category;
import encore.server.domain.post.enumerate.PostType;
import encore.server.domain.term.entity.Term;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class PostConverter {

    private final UserRepository userRepository;

    public PostConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //PostCreateReq -> Post 로 변환
    public Post convert(PostCreateReq postCreateReq, User user){

        return Post.builder()
                .user(user)
                .title(postCreateReq.title())
                .content(postCreateReq.content())
                .isNotice(postCreateReq.isNotice())
                .isTemporarySave(postCreateReq.isTemporarySave())
                .postType(PostType.valueOf(postCreateReq.postType()))
                .category(Category.valueOf(postCreateReq.category()))
                .postHashtags(new ArrayList<>())  // 빈 리스트 초기화
                .postImages(new ArrayList<>())  // 빈 리스트 초기화
                .likeCount(0L)
                .commentCount(0L)
                .build();

    }



    public PostDetailsGetRes postDetailsGetResFrom(
            Post post, List<String> hashtags, List<String> postImages, Long numOfLike, Long numOfComment,boolean isLiked, List<Term> musicalTerms
    ) {

        Boolean isModified = true;

        if (Objects.equals(post.getModifiedAt(), post.getCreatedAt())) {
            isModified = false;
        }
        User user = userRepository.findById(1L).orElseThrow();

        return new PostDetailsGetRes(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickName(),
                post.getUser().getProfileImageUrl(),
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
                numOfComment,
                isLiked,
                MusicalTermConverter.toMusicalTermResList(musicalTerms)
        );

    }

    //likeCount인자 추가
    public static final SimplePostRes toSimplePostRes(Post post, User user, Boolean isLiked) {
        return SimplePostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory() != null ? post.getCategory().name() : null)
                .type(post.getPostType() != null ? post.getPostType().name() : null)
                .thumbnail(post.getPostImages().isEmpty() ? null : post.getPostImages().get(0).getUrl())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .userId(user.getId())
                .nickname(user.getNickName())
                .createdAt(post.getCreatedAt())
                .isLiked(isLiked)
                .build();
    }

}