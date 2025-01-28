package encore.server.domain.user.entity;

import encore.server.domain.user.enumerate.TermType;
import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@SQLDelete(sql = "UPDATE term_of_use SET deleted_at = NOW() where id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermOfUse extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private TermType termType;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false, columnDefinition = "tinyint(1)")
    private Boolean isOptional;

    @Column(nullable = false, columnDefinition = "bigint")
    private Long version;


}
