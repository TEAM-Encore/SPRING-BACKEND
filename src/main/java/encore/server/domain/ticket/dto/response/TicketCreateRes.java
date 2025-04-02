package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketCreateRes(
        Long id,
        Long userId,
        Long musicalId,
        LocalDate viewedDate,
        String showTime,
        String seat,
        List<ActorDTO> actors,
        String ticketImageUrl
) {
    public static TicketCreateRes from(Ticket ticket) {
        return TicketCreateRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalId(ticket.getMusical().getId())
                .viewedDate(ticket.getViewedDate())
                .showTime(ticket.getShowTime())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(ActorDTO::from)  // ActorDTO로 변환
                        .collect(Collectors.toList()))
                .ticketImageUrl(ticket.getTicketImageUrl())
                .build();
    }
}