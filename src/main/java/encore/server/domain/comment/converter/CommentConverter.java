package encore.server.domain.comment.converter;

import encore.server.domain.comment.dto.request.CommentReq;
import encore.server.domain.comment.dto.response.CommentRes;
import encore.server.domain.comment.entity.Comment;
import encore.server.domain.post.entity.Post;
import encore.server.domain.user.entity.User;

public class CommentConverter {
    public static Comment toEntity(Post post, User user, Comment parentComment, CommentReq req) {
        return Comment.builder()
                .post(post)
                .user(user)
                .parentId(parentComment)
                .content(req.content())
                .build();
    }

    public static CommentRes toResponse(Comment comment) {
        return CommentRes.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParentId() == null ? null : comment.getParentId().getId())
                .content(comment.getContent())
                .isPostOwner(comment.getUser().getId().equals(comment.getPost().getUser().getId()))
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
