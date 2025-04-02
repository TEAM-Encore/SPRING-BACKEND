package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketUpdateRes(
        Long id,
        Long userId,
        String musicalTitle,
        Long series,
        LocalDate viewedDate,
        String location,
        String seat,
        List<String> actors
) {
    public static TicketUpdateRes from(Ticket ticket) {
        return TicketUpdateRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalTitle(ticket.getMusical().getTitle())
                .series(ticket.getMusical().getSeries())
                .viewedDate(ticket.getViewedDate())
                .location(ticket.getMusical().getLocation())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}