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
        String title,
        LocalDate viewedDate,
        String showTime,
        String seat,
        List<ActorDTO> actors,
        String ticketImageUrl
) {
}