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
    List<Ticket> findByUserIdAndViewedDateAfterAndDeletedAtIsNull(Long userId, LocalDate date);
    List<Ticket> findByUserIdAndDeletedAtIsNull(Long userId);

    @Modifying
    @Query("UPDATE Ticket t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = :ticketId")
    void softDeleteByTicketId(@Param("ticketId") Long ticketId);

    Optional<Ticket> findByIdAndUserIdAndDeletedAtIsNull(Long ticketId, Long userId);
}