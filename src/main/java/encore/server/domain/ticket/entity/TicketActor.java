package encore.server.domain.ticket.entity;


import jakarta.persistence.*;
import lombok.Builder;

public class TicketActor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, columnDefinition = "bigint")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false, columnDefinition = "bigint")
    private Actor actor;

    @Builder
    public TicketActor(Long id, Ticket ticket, Actor actor) {
        this.id = id;
        this.ticket = ticket;
        this.actor = actor;
    }

}
