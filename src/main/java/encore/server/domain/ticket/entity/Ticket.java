package encore.server.domain.ticket.entity;

import encore.server.domain.review.entity.Review;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.ticket.dto.request.ActorDTO;
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
@SQLDelete(sql = "UPDATE ticket SET deleted_at = NOW() where id = ?")
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
    private String ticketImageUrl;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate viewedDate;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String seat;

    //관람 회차 시간
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private String showTime;

    @ManyToMany
    @JoinTable(
            name = "ticket_actors",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public Ticket(User user, Musical musical, String title, String ticketImageUrl, LocalDate viewedDate, String seat, String showTime, List<Actor> actors) {
        this.user = user;
        this.musical = musical;
        this.title = title;
        this.ticketImageUrl = ticketImageUrl;
        this.viewedDate = viewedDate;
        this.seat = seat;
        this.showTime = showTime;
        this.actors = actors;
    }

    // Setter 메서드 추가
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTicketImageUrl(String ticketImageUrl) {
        this.ticketImageUrl = ticketImageUrl;
    }

    public void setViewedDate(LocalDate viewedDate) {
        this.viewedDate = viewedDate;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
}
