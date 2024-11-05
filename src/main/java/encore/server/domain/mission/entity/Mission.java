package encore.server.domain.mission.entity;

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
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "bigint")
    private User user;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long reward;

    @Column(nullable = false, columnDefinition = "tinyint default false")
    private Boolean isCompleted;

    @Column(nullable = false, columnDefinition = "tinyint default false")
    private Boolean isRewarded;

    @Builder
    public Mission(User user, String name, Long reward, Boolean isCompleted, Boolean isRewarded) {
        this.user = user;
        this.name = name;
        this.reward = reward;
        this.isCompleted = isCompleted;
        this.isRewarded = isRewarded;
    }
}
