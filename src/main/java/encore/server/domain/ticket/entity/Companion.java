package encore.server.domain.ticket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Companion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, columnDefinition = "bigint")
    private Ticket ticket;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String name;

    @Builder
    public Companion(Ticket ticket, String name) {
        this.ticket = ticket;
        this.name = name;
    }
}
