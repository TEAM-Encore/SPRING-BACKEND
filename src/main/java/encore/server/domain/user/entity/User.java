package encore.server.domain.user.entity;

import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String nickName;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long point;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @Builder
    public User(String nickName, Long point) {
        this.nickName = nickName;
        this.point = point;
    }
}
