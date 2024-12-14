package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByViewedDateAfter(LocalDate date);
}
