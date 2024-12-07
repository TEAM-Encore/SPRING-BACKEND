package encore.server.domain.user.entity;

import encore.server.domain.user.enumerate.PreferredKeywordEnum;
import encore.server.global.common.BaseTimeEntity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Entity
@SQLDelete(sql = "UPDATE preferred_keyword SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferredKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(unique = true, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private PreferredKeywordEnum preferredKeyword;
}
