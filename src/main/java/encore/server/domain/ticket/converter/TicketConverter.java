package encore.server.domain.ticket.converter;

import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.dto.response.TicketSimpleRes;
import encore.server.domain.ticket.dto.response.TicketUpdateRes;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketConverter {

    public static ActorDTO toActorDTO(Actor actor) {
        return ActorDTO.builder()
                .id(actor.getId())
                .name(actor.getName())
                .actorImageUrl(actor.getActorImageUrl())
                .build();
    }

    public static TicketCreateRes toTicketCreateRes(Ticket ticket) {
        return TicketCreateRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalId(ticket.getMusical().getId())
                .viewedDate(ticket.getViewedDate())
                .showTime(ticket.getShowTime())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(actor -> ActorDTO.builder()
                                .id(actor.getId())
                                .name(actor.getName())
                                .actorImageUrl(actor.getActorImageUrl())
                                .build())
                        .collect(Collectors.toList()))
                .ticketImageUrl(ticket.getTicketImageUrl())
                .build();
    }

    public static TicketUpdateRes toTicketUpdateRes(Ticket ticket) {
        return TicketUpdateRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalTitle(ticket.getMusical().getTitle())
                .series(ticket.getMusical().getSeries())
                .viewedDate(ticket.getViewedDate())
                .location(ticket.getMusical().getLocation())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    public static String convertSeriesToText(Long series) {
        return switch (series.intValue()) {
            case 1 -> "초연";
            case 2 -> "재연";
            default -> series + "연";
        };
    }


    public static TicketRes toTicketRes(Ticket ticket, Optional<Review> reviewOpt) {
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

    public static TicketSimpleRes toTicketSimpleRes(Ticket ticket) {
        return TicketSimpleRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .musicalTitle(ticket.getMusical().getTitle())
                .series(convertSeriesToText(ticket.getMusical().getSeries())) // 숫자 -> 문자열 변환
                .viewedDate(ticket.getViewedDate())
                .location(ticket.getMusical().getLocation())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(Actor::getName)
                        .collect(Collectors.toList())) // 배우 이름 리스트로 변환
                .ticketImageUrl(ticket.getTicketImageUrl())
                .build();
    }

}