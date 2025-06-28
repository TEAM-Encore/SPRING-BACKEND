package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketRes(
        Long id,
        Long userId,
        String musicalTitle,
        String series, // 숫자 -> 문자열 변경
        LocalDate viewedDate,
        String location,
        String seat,
        List<String> actors,
        Boolean hasReview,
        Float totalRating,
        String ticketImageUrl,
        String musicalImageUrl
) {
}