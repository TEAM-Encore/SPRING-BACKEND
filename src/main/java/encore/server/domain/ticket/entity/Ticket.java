package encore.server.domain.ticket.entity;

import encore.server.domain.review.entity.Review;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@SQLDelete(sql = "UPDATE ticket SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ticket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, columnDefinition = "text")
    private String ticketImageUrl;

    @Column(nullable = false)
    private LocalDate viewedDate;

    @Column(nullable = true)
    private Long floor;

    @Column(nullable = true)
    private String zone;

    @Column(nullable = false)
    private String col;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String showTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", nullable = false)
    private Musical musical;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", unique = true, nullable = true)
    private Review review;

    @Builder.Default
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketActor> ticketActorList = new ArrayList<>();


    public void updateTicketImageUrl(String ticketImageUrl) {
        if (ticketImageUrl != null && !ticketImageUrl.isBlank()) {
            this.ticketImageUrl = ticketImageUrl;
        }
    }

    public void updateViewedDate(LocalDate viewedDate) {
        if (viewedDate != null) {
            this.viewedDate = viewedDate;
        }
    }

    public void updateFloor(Long floor) {
        this.floor = floor;
    }

    public void updateZone(String zone) {
        this.zone = zone;
    }

    public void updateCol(String col) {
        if (col != null && !col.isBlank()) {
            this.col = col;
        }
    }

    public void updateNumber(String number) {
        if (number != null && !number.isBlank()) {
            this.number = number;
        }
    }

    public void updateShowTime(String showTime) {
        if (showTime != null && !showTime.isBlank()) {
            this.showTime = showTime;
        }
    }

    public void updateReview(Review review) {
        this.review = review;
    }
}
