package encore.server.domain.notification.repository;

import encore.server.domain.notification.entity.Notification;
import encore.server.domain.notification.enumerate.NotificationType;
import encore.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndType(User user, NotificationType notificationType);
}
