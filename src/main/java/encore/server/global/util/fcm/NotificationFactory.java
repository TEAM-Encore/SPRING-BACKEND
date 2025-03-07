package encore.server.global.util.fcm;

import encore.server.domain.notification.entity.Notification;
import encore.server.domain.notification.enumerate.NotificationType;
import encore.server.domain.post.entity.Post;

import java.time.LocalDateTime;

public class NotificationFactory {

    public static Notification createHashtagNotification(Post post) {
        return Notification.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(LocalDateTime.now())
                .type(NotificationType.HASH_TAG)
                .build();
    }

    public static Notification createCommentNotification(Post post) {
        return Notification.builder()
                .title("답글이 달렸어요")
                .content('"' + post.getTitle() + '"' + " 게시글")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.ACTIVITY)
                .build();
    }

    public static Notification createLikeNotification(Post post) {
        return Notification.builder()
                .title("좋아요 " + post.getLikeCount() + "개를 달성했어요")
                .content('"' + post.getTitle() + '"' + " 게시글")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.ACTIVITY)
                .build();
    }

    public static Notification createRankingNotification(Post post) {
        return Notification.builder()
                .title("오늘의 TOP1 인기 게시글")
                .content('"' + post.getTitle() + '"' + " 게시글")
                .createdAt(LocalDateTime.now())
                .type(NotificationType.ACTIVITY)
                .build();
    }
}