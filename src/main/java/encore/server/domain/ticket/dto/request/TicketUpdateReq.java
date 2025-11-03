package encore.server.domain.ticket.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketUpdateReq(
        String ticketImageUrl,
        LocalDate viewedDate,
        Long floor,
        String zone,
        String col,
        String number,
        String showTime,
        List<Long> actorIds
) { }
