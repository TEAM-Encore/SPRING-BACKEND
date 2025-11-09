package encore.server.domain.review.entity;

import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.enumerate.LikeType;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Entity
@SQLDelete(sql = "UPDATE review SET deleted_at = NOW() where id = ?")
@SQLRestriction("deleted_at IS NULL")
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

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long viewCount;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long likeCount;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long followUpLikeCount;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long fullOfTipsLikeCount;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long thoroughAnalysisLikeCount;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTags> tags;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes;

    @Embedded
    private ReviewData reviewData;

    @Builder
    public Review(User user, Ticket ticket, String title, List<ReviewTags> tags, ReviewData reviewData) {
        this.user = user;
        this.ticket = ticket;
        this.title = title;
        this.tags = tags;
        this.reviewData = reviewData;
        this.viewCount = 0L;
        this.followUpLikeCount = 0L;
        this.fullOfTipsLikeCount = 0L;
        this.thoroughAnalysisLikeCount = 0L;
        this.likeCount = 0L;
    }

    public void addTags(List<ReviewTags> reviewTags) {
        this.tags = reviewTags;
    }

    public void setLikeCount(LikeType likeType, Long likeCount) {
        switch (likeType) {
            case FOLLOW_UP_RECOMMENDATION:
                this.followUpLikeCount = likeCount;
                break;
            case FULL_OF_TIPS:
                this.fullOfTipsLikeCount = likeCount;
                break;
            case THOROUGH_ANALYSIS:
                this.thoroughAnalysisLikeCount = likeCount;
                break;
        }
    }

    public void setTotalLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }


    public void updateReview(ReviewReq req, ReviewData reviewData) {
        this.title = req.title();
        this.reviewData = reviewData;
    }
}