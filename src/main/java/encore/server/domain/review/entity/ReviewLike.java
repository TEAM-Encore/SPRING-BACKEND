package encore.server.domain.review.entity;

import encore.server.domain.review.enumerate.LikeType;
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

    @Enumerated(EnumType.STRING)
    private LikeType likeType;

    @Builder
    public ReviewLike(Review review, User user, LikeType likeType) {
        this.review = review;
        this.user = user;
        this.likeType = likeType;
    }

    public void toggleLike(LikeType likeType) {
        this.likeType = this.likeType == likeType ? null : likeType;
    }
}
