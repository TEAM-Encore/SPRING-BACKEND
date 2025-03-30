package encore.server.domain.notification.dto.response;

import encore.server.domain.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NotificationRes(
        @Schema(description = "알림 ID", example = "1")
        Long id,

        @Schema(description = "알림 제목", example = "알림 제목")
        String title,

        @Schema(description = "알림 내용", example = "알림 내용")
        String content,

        @Schema(description = "알림 타입", example = "ACTIVITY")
        String type,

        @Schema(description = "알림 읽음 여부", example = "false")
        Boolean isRead,

        @Schema(description = "알림 생성일", example = "2021-07-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "알림 경과 시간", example = "1시간 전")
        String elapsedTime
) {
    public static NotificationRes of(Notification notification, String elapsedTime) {
        return NotificationRes.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType().toString())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .elapsedTime(elapsedTime)
                .build();
    }

    public static List<NotificationRes> listOf(List<Notification> notifications, List<String> elapsedTimes) {
        return notifications.stream()
                .map(notification -> of(notification, elapsedTimes.get(notifications.indexOf(notification))))
                .toList();
    }
}
