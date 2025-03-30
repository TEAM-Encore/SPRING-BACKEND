package encore.server.global.util.fcm;

import encore.server.domain.notification.entity.Notification;
import encore.server.domain.user.entity.FCMToken;
import encore.server.domain.user.entity.User;
import encore.server.global.config.FCMConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {
    private final FCMConfig fcmConfig;

    //FCM init() 메서드 호출
    public void init() {
        fcmConfig.init();
    }

    public void sendToUser(User user, Notification notification) {
        init();
        List<String> tokens = user.getFcmTokens().stream()
                .map(FCMToken::getToken)
                .collect(Collectors.toList());

        fcmConfig.sendByTokenList(tokens, notification.getTitle(), notification.getContent());
    }

    public void sendToUsers(Set<User> users, Notification notification) {
        init();
        List<String> tokens = users.stream()
                .flatMap(user -> user.getFcmTokens().stream().map(FCMToken::getToken))
                .collect(Collectors.toList());

        fcmConfig.sendByTokenList(tokens, notification.getTitle(), notification.getContent());
    }
}