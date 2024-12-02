package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByIdAndDeletedAtIsNull(Long ticketId);
}
