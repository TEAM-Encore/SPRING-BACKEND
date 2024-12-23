package encore.server.domain.ticket.entity;

import encore.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Actor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String actorImageUrl;


    public Actor(Long id, String name, String actorImageUrl) {
        this.id = id;
        this.name = name;
        this.actorImageUrl = actorImageUrl;
    }
}
