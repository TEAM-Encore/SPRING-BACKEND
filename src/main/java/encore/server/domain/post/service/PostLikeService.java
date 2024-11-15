package encore.server.domain.post.service;

import encore.server.domain.post.dto.request.PostLikeReq;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.entity.PostLike;
import encore.server.domain.post.repository.PostLikeRepository;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void toggleLike(PostLikeReq postLikeReq) throws Exception {

        User user = userRepository.findById(postLikeReq.getUserId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Post post = postRepository.findById(postLikeReq.getPostId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        // PostLike 객체가 있는지 확인
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post).orElse(null);

        if (postLike == null) {
            // 없으면 새로 생성
            postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .liked(true)
                    .build();
        } else {
            // 있으면 liked 상태를 토글
            postLike.setLiked(!postLike.isLiked());
        }

        postLikeRepository.save(postLike);
    }
}