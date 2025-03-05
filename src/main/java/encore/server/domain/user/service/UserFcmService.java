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
import encore.server.global.config.FCMConfig;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final FCMConfig fcmConfig;
    private final NotificationRepository notificationRepository;

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
    public void notifyUsersByHashtag(Post post, List<PostHashtag> postHashtags){
        // 게시글 해시태그 리스트 추출
        List<String> postHashtagNames = postHashtags.stream()
                .map(ph -> ph.getHashtag().getName())
                .collect(Collectors.toList());

        // 해당 해시태그를 설정한 사용자 찾기
        List<UserHashtag> matchedUserHashtags = userHashtagRepository.findByHashtagNameIn(postHashtagNames);

        // 알림 보낼 사용자 목록
        Set<User> usersToNotify = matchedUserHashtags.stream()
                .map(UserHashtag::getUser)
                .collect(Collectors.toSet());

        Notification notification = Notification.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(LocalDateTime.now())
                .type(NotificationType.HASH_TAG)
                .build();

        // FCM을 통해 알림 전송
        for (User user : usersToNotify) {
            fcmConfig.sendByTokenList(
                    user.getFcmTokens().stream()
                            .map(FCMToken::getToken)
                            .collect(Collectors.toList()),
                    post.getTitle(),
                    post.getUser().getName());
        }

        notificationRepository.save(notification);
    }


    public List<NotificationRes> getNotifications(Long userId, String type) {
        // validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // business logic
        List<Notification> notifications = notificationRepository.findByUserAndType(user, NotificationType.valueOf(type));

        // response
        List<String> elapsedTimes = notifications.stream()
                .map(notification -> getElapsedTime(
                        ChronoUnit.MINUTES.between(notification.getCreatedAt(), LocalDateTime.now())
                ))
                .collect(Collectors.toList());

        return NotificationRes.listOf(notifications, elapsedTimes);
    }

    private String getElapsedTime(long minutesAgo) {
        if (minutesAgo < 1) {
            return "방금 전";
        } else if (minutesAgo < 60) {
            return String.format("%d분 전", minutesAgo);
        } else if (minutesAgo < 1440) {
            return String.format("%d시간 전", minutesAgo / 60);
        } else {
            return String.format("%d일 전", minutesAgo / 1440);
        }
    }
}
