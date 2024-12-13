package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketCreateRes(
        Long id,
        Long musicalId,
        LocalDate viewedDate,
        String showTime,
        String seat,
        Long userId
) {
    public TicketCreateRes(Ticket ticket) {
        this(
                ticket.getId(),
                ticket.getMusical().getId(),
                ticket.getViewedDate(),
                ticket.getShowTime(),
                ticket.getSeat(),
                ticket.getUser().getId()
        );
    }
}