package encore.server.domain.notification.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationType {
    ACTIVITY("활동 알람"),
    HASH_TAG("해시태그 알람");

    private final String description;
}
