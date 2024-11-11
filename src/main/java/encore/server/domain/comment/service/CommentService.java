package encore.server.domain.comment.service;

import encore.server.domain.comment.converter.CommentConverter;
import encore.server.domain.comment.dto.request.CommentReq;
import encore.server.domain.comment.dto.response.CommentRes;
import encore.server.domain.comment.entity.Comment;
import encore.server.domain.comment.repository.CommentRepository;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentRes create(Long postId, Long userId, CommentReq req){
        //validation: user, post 유효성 확인
        User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId).orElseThrow(() -> new ApplicationException(ErrorCode.POST_NOT_FOUND_EXCEPTION));

        //business logic: 부모 ID가 있으면 대댓글, 없으면 댓글 생성
        Comment parentComment;
        Comment comment;
        if(req.parentId() != null){
            parentComment = commentRepository.findByIdAndPostAndDeletedAtIsNull(req.parentId(), post).orElseThrow(() -> new ApplicationException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
            comment = CommentConverter.toEntity(post, user, parentComment, req);
        } else {
            comment = CommentConverter.toEntity(post, user, null, req);
        }
        commentRepository.save(comment);

        //return: 생성된 댓글 정보 반환
        return CommentConverter.toResponse(comment);
    }
}
