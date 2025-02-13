package encore.server.domain.comment.converter;

import encore.server.domain.comment.dto.request.CommentReq;
import encore.server.domain.comment.dto.response.CommentLikeRes;
import encore.server.domain.comment.dto.response.CommentRes;
import encore.server.domain.comment.entity.Comment;
import encore.server.domain.comment.entity.CommentLike;
import encore.server.domain.post.entity.Post;
import encore.server.domain.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentConverter {
    public static Comment toEntity(Post post, User user, Comment parentComment, CommentReq req) {
        return Comment.builder()
                .post(post)
                .user(user)
                .parentId(parentComment)
                .content(req.content())
                .build();
    }

    public static CommentRes toResponse(Comment comment, User user, Boolean isLiked) {

        return CommentRes.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParentId() == null ? null : comment.getParentId().getId())
                .content(comment.getContent())
                .isPostOwner(comment.getUser().getId().equals(comment.getPost().getUser().getId()))
                .isMyComment(comment.getUser().getId().equals(user.getId()))
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickName())
                .profileImageUrl(comment.getUser().getProfileImageUrl())
                .likeCount((long) comment.getLikes().size())
                .isLiked(isLiked)
                .childCommentCount((long) comment.getChildComments().size())
                .build();
    }

    public static List<CommentRes> toListResponse(Map<Comment, Boolean> comments, User user) {
        return comments.entrySet().stream()
                .map(entry -> toResponse(entry.getKey(), user, entry.getValue()))
                .collect(Collectors.toList());
    }

    public static CommentLikeRes toLikeResponse(CommentLike commentlike, Long count) {
        return CommentLikeRes.builder()
                .commentId(commentlike.getComment().getId())
                .userId(commentlike.getUser().getId())
                .isLiked(commentlike.isLiked())
                .likeCount(count)
                .build();
    }
}