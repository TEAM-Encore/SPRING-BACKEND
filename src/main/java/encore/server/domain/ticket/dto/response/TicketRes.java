package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
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

        List<ActorRes> actors,

        @Schema(
            type = "string",
            example = "12:30",
            description = "시간 (HH:mm)"
        )
        @JsonFormat(pattern = "HH:mm")
        LocalTime showTime

        // TODO: 리뷰 관련
//        Boolean hasReview,
//        Long reviewId,
//        Integer totalRating,
) { }
