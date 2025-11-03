package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Ticket;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByIdAndUserId(Long ticketId, Long userId);

    List<Ticket> findByUserIdAndViewedDateAfter(Long userId, LocalDate startDate);

    List<Ticket> findByUserId(Long userId);

    List<Ticket> findByMusical_TitleContaining(String musicalTitle);
}