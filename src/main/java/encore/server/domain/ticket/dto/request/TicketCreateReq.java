package encore.server.domain.ticket.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketCreateReq(
        Long musicalId,
        LocalDate viewedDate,
        @Schema(
            type = "string",
            example = "12:30",
            description = "시간 (HH:mm)"
        )
        LocalTime showTime,
        Long floor,
        String zone,
        String col,
        String number,
        List<Long> actorIds, // Actor 리스트
        String ticketImageUrl
) {
}
