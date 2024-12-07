package encore.server.domain.review.entity;

import encore.server.domain.review.Tag;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Getter
@Entity
@SQLDelete(sql = "UPDATE review SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "tag", nullable = false)
    private List<Tag> tags;

    @Embedded
    private ReviewData reviewData;

    @Builder
    public Review(User user, Ticket ticket, String title, List<Tag> tags, ReviewData reviewData) {
        this.user = user;
        this.ticket = ticket;
        this.title = title;
        this.tags = tags;
        this.reviewData = reviewData;
    }
}