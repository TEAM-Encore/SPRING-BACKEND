package encore.server.domain.ticket.dto.response;

import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record TicketRes(
        Long id,
        String musicalTitle,
        Long series,
        LocalDate viewedDate,
        String venue,
        String seat,
        List<String> actors
) {
    public TicketRes(Ticket ticket) {
        this(
                ticket.getId(),
                ticket.getMusical().getTitle(),
                ticket.getMusical().getSeries(),
                ticket.getViewedDate(),
                ticket.getMusical().getLocation(),
                ticket.getSeat(),
                ticket.getActors().stream()
                        .map(actor -> actor.getName())
                        .collect(Collectors.toList())
        );
    }
}