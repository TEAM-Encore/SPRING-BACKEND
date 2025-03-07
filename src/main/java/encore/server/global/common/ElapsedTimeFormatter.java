package encore.server.global.common;

import encore.server.domain.notification.entity.Notification;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class ElapsedTimeFormatter {

    public static List<String> formatElapsedTimes(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> formatElapsedTime(notification.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public static String formatElapsedTime(LocalDateTime createdAt) {
        long minutesAgo = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());

        if (minutesAgo < 1) return "방금 전";
        if (minutesAgo < 60) return minutesAgo + "분 전";
        if (minutesAgo < 1440) return (minutesAgo / 60) + "시간 전";
        return (minutesAgo / 1440) + "일 전";
    }
}
