package encore.server.domain.notification.controller;

import encore.server.domain.notification.dto.response.NotificationRes;
import encore.server.domain.user.service.UserFcmService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {
    private final UserFcmService userFcmService;

    @GetMapping("/{type}")
    public List<NotificationRes> getNotifications(
            @PathVariable("type") String type
    ) {
        Long userId = 1L;
        return userFcmService.getNotifications(userId, type);
    }
}
