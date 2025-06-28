package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.dto.request.ActorDTO;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketDetailRes(
        Long ticketId,
        String ticketTitle,
        String seat,
        String showTime,
        String ticketImageUrl,
        LocalDate viewedDate,
        boolean hasReview,
        Long reviewId,

        // 뮤지컬 정보
        Long musicalId,
        String musicalTitle,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long runningTime,
        String age,
        String imageUrl,
        Long series,

        // 배우 목록
        List<ActorDTO> actors
) {}
