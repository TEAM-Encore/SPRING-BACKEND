package encore.server.domain.user.service;

import encore.server.domain.hashtag.entity.PostHashtag;
import encore.server.domain.hashtag.entity.UserHashtag;
import encore.server.domain.hashtag.repository.UserHashtagRepository;
import encore.server.domain.notification.dto.response.NotificationRes;
import encore.server.domain.notification.entity.Notification;
import encore.server.domain.notification.enumerate.NotificationType;
import encore.server.domain.notification.repository.NotificationRepository;
import encore.server.domain.post.entity.Post;
import encore.server.domain.user.entity.FCMToken;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.FcmTokenRepository;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.common.ElapsedTimeFormatter;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import encore.server.global.util.fcm.FcmNotificationService;
import encore.server.global.util.fcm.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFcmService {
    private final UserRepository userRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserHashtagRepository userHashtagRepository;
    private final NotificationRepository notificationRepository;
    private final FcmNotificationService fcmNotificationService;

    @Transactional
    public void addFCMToken(Long userId, String token) {
        // validation
        Optional<User> user = userRepository.findById(userId);

        // business logic
        FCMToken fcmToken = FCMToken.builder()
                .user(user.orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION)))
                .token(token)
                .build();
        fcmTokenRepository.save(fcmToken);
    }

    @Transactional
    public void notifyUsersByHashtag(Post post, List<PostHashtag> postHashtags) {
        Set<User> usersToNotify = userHashtagRepository.findByHashtagNameIn(
                postHashtags.stream().map(ph -> ph.getHashtag().getName()).collect(Collectors.toList())
        ).stream().map(UserHashtag::getUser).collect(Collectors.toSet());

        Notification notification = NotificationFactory.createHashtagNotification(post);

        fcmNotificationService.sendToUsers(usersToNotify, notification);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyUserByComment(Post post) {
        Notification notification = NotificationFactory.createCommentNotification(post);
        fcmNotificationService.sendToUser(post.getUser(), notification);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyUserByLike(Post post) {
        Notification notification = NotificationFactory.createLikeNotification(post);
        fcmNotificationService.sendToUser(post.getUser(), notification);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyUsersByRanking(User user, Post post) {
        Notification notification = NotificationFactory.createRankingNotification(post);
        fcmNotificationService.sendToUser(user, notification);
        notificationRepository.save(notification);
    }

    public List<NotificationRes> getNotifications(Long userId, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        List<Notification> notifications = notificationRepository.findByUserAndType(user, NotificationType.valueOf(type));

        return NotificationRes.listOf(notifications, ElapsedTimeFormatter.formatElapsedTimes(notifications));
    }
}