package encore.server.domain.ticket.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketCreateReq(
        Long musicalId,
        Long userId,
        LocalDate viewedDate,
        String showTime,
        String seat,
        List<Long> actorIds, // Actor 리스트
        String ticketImageUrl
) {
}
