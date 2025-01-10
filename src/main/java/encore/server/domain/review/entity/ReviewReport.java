package encore.server.domain.review.entity;

import encore.server.domain.review.enumerate.ReportReason;
import encore.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User reporter;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Builder
    public ReviewReport(Review review, User reporter, ReportReason reason) {
        this.review = review;
        this.reporter = reporter;
        this.reason = reason;
    }
}
