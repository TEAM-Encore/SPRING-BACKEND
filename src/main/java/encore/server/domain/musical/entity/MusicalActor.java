package encore.server.domain.musical.entity;

import encore.server.domain.ticket.entity.Actor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "musical_actor")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicalActor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musical_id", nullable = false)
    private Musical musical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String roleName; // 배역 이름

    @Column(nullable = false)
    private boolean isMainActor; // 주연 여부

    @Builder
    public MusicalActor(Musical musical, Actor actor, String roleName, boolean isMainActor) {
        this.musical = musical;
        this.actor = actor;
        this.roleName = roleName;
        this.isMainActor = isMainActor;
    }
}