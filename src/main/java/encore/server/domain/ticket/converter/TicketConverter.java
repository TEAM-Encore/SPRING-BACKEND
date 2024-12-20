package encore.server.domain.ticket.converter;

import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;

import java.util.List;
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

    public static TicketRes toTicketRes(Ticket ticket) {
        return TicketRes.builder()
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
}