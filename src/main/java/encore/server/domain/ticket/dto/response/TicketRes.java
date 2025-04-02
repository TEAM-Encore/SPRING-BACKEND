package encore.server.domain.ticket.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        String ticketImageUrl
) {
    public static String convertSeriesToText(Long series) {
        return switch (series.intValue()) {
            case 1 -> "초연";
            case 2 -> "재연";
            default -> series + "연";
        };
    }
    public static TicketRes from(Ticket ticket, Optional<Review> reviewOpt) {
        return TicketRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalTitle(ticket.getMusical().getTitle())
                .series(convertSeriesToText(ticket.getMusical().getSeries())) // 변환된 값 사용
                .viewedDate(ticket.getViewedDate())
                .location(ticket.getMusical().getLocation())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toList()))
                .hasReview(reviewOpt.isPresent())
                .totalRating(reviewOpt.map(r -> r.getReviewData().getRating().getTotalRating()).orElse(null))
                .ticketImageUrl(ticket.getTicketImageUrl())
                .build();
    }
}