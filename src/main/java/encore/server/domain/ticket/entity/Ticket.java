package encore.server.domain.ticket.entity;

import encore.server.domain.review.entity.Review;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", nullable = false, columnDefinition = "bigint")
    private Musical musical;

    @Column(nullable = true, columnDefinition = "varchar(255)")
    private String title;

    @Column(nullable = true, columnDefinition = "text")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate viewedDate;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String seat;

    //관람 회차 시간, 배우 필드 추가
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String showTime;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ticket_actors", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "actor_name")
    private List<String> actors;

    @OneToMany(mappedBy = "ticket")
    private List<Companion> companions;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public Ticket(User user, Musical musical, String title, String imageUrl, LocalDate viewedDate, String seat, String showTime, List<String> actors) {
        this.user = user;
        this.musical = musical;
        this.title = title;
        this.imageUrl = imageUrl;
        this.viewedDate = viewedDate;
        this.seat = seat;
        this.showTime = showTime;
        this.actors = actors;

    }
}
