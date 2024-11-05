package encore.server.domain.review.entity;

import encore.server.domain.musical.entity.Musical;
import encore.server.domain.user.entity.User;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

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

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String title;

    @Column(nullable = false, columnDefinition = "varchar(1000)")
    private String content;

    @Builder
    public Review(User user, Musical musical, String title, String content) {
        this.user = user;
        this.musical = musical;
        this.title = title;
        this.content = content;
    }
}
