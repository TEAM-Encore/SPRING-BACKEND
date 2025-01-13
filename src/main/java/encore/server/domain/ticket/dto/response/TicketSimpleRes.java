package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketSimpleRes(
        Long id,
        Long userId,
        String musicalTitle,
        String series, // 숫자 -> 문자열 변경
        LocalDate viewedDate,
        String location,
        String seat,
        List<String> actors,
        String ticketImageUrl
) {
}