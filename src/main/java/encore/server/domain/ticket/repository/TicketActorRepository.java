package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.entity.TicketActor;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketActorRepository extends JpaRepository<TicketActor, Long> {

  @EntityGraph(attributePaths = {"actor"})
  List<TicketActor> findByTicket(Ticket ticket);

  @EntityGraph(attributePaths = {"actor"})
  List<TicketActor> findByTicketIn(List<Ticket> filteredTickets);
}
