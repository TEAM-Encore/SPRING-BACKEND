package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByIdAndUserId(Long ticketId, Long userId);

    List<Ticket> findByUserIdAndViewedDateAfter(Long userId, LocalDate startDate);

    @EntityGraph(attributePaths = {"review"})
    List<Ticket> findByUserId(Long userId);

    @Query("""
      select distinct t
      from Ticket t
      join fetch t.musical m
      join fetch t.user u
      left join fetch t.ticketActorList ta
      left join fetch ta.actor a
      where t.user = :user
      and t.review is null
      and t.ticketImageUrl is not null
    """)
    List<Ticket> findUnreviewedWithImageByUserFetchAll(@Param("user") User user);

    List<Ticket> findByMusical_TitleContaining(String musicalTitle);
}