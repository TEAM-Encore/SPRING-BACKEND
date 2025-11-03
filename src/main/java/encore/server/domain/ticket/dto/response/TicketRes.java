package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TicketRes(
        Long id,
        Long userId,

        Long musicalId,
        String musicalTitle,
        String location,
        String musicalImageUrl,

        LocalDate viewedDate,
        String ticketImageUrl,

        Long floor,
        String zone,
        String col,
        String number,

        List<ActorRes> actors

        // TODO: 리뷰 관련
//        Boolean hasReview,
//        Long reviewId,
//        Integer totalRating,
) { }
