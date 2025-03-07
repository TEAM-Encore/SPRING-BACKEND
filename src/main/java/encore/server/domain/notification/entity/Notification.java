package encore.server.domain.notification.entity;

import encore.server.domain.notification.enumerate.NotificationType;
import encore.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean isRead;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private NotificationType type;

    @Builder
    public Notification(User user, String title, String content,  LocalDateTime createdAt, NotificationType type) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.isRead = false;
        this.createdAt = createdAt;
    }
}
