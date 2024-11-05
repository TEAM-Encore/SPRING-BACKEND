package encore.server.domain.ticket.entity;

import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "bigint")
    private User user;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime viewedDate;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Companion> companions = new ArrayList<>();

    @Builder
    public Ticket(User user, String title, String imageUrl, LocalDateTime viewedDate, List<Companion> companions) {
        this.user = user;
        this.title = title;
        this.imageUrl = imageUrl;
        this.viewedDate = viewedDate;
        this.companions = companions;
    }
}
