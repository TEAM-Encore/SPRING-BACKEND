package encore.server.domain.ticket.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketUpdateReq(
        String title,
        LocalDate viewedDate,
        String showTime,
        String seat,
        List<ActorDTO> actors,
        String ticketImageUrl
) {
}