package encore.server.domain.ticket.converter;

import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public class TicketConverter {

    // Ticket -> TicketCreateRes 변환
    public static TicketCreateRes toCreateResponse(Ticket ticket) {
        return TicketCreateRes.builder()
                .id(ticket.getId())
                .musicalId(ticket.getMusical().getId())
                .viewedDate(ticket.getViewedDate())
                .showTime(ticket.getShowTime())
                .seat(ticket.getSeat())
                .actors(ticket.getActors().stream()
                        .map(actor -> new ActorDTO(actor.getId(), actor.getName(), actor.getActorImageUrl()))
                        .collect(Collectors.toList()))
                .ticketImageUrl(ticket.getTicketImageUrl())
                .build();
    }

    // Ticket -> TicketRes 변환
    public static TicketRes toResponse(Ticket ticket) {
        return new TicketRes(ticket);
    }

    // ActorDTO -> Actor 변환
    public static List<Actor> toActorList(List<ActorDTO> actorDTOs) {
        return actorDTOs.stream()
                .map(dto -> Actor.builder()
                        .id(dto.id())
                        .name(dto.name())
                        .actorImageUrl(dto.actorImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
