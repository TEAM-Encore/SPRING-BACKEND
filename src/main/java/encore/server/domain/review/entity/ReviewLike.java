package encore.server.domain.review.entity;

import encore.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isLike;

    @Builder
    public ReviewLike(Review review, User user) {
        this.review = review;
        this.user = user;
        this.isLike = true;
    }

    public Boolean toggleLike(Boolean isLike) {
        this.isLike = !isLike;
        return isLike;
    }
}
