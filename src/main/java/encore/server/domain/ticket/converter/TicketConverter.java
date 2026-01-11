package encore.server.domain.ticket.converter;

import encore.server.domain.musical.entity.Musical;
import encore.server.domain.ticket.dto.response.ActorRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;

import java.util.List;
import java.util.stream.Collectors;

public class TicketConverter {

    public static TicketRes toTicketRes(Ticket ticket) {
        Musical musical = ticket.getMusical();

        // TODO: N+1 문제 해결
        List<ActorRes> actors = ticket.getTicketActorList().stream()
                .map(ticketActor -> ActorRes.builder()
                        .id(ticketActor.getActor().getId())
                        .name(ticketActor.getActor().getName())
                        .actorImageUrl(ticketActor.getActor().getActorImageUrl())
                        .build())
                .collect(Collectors.toList());

        return TicketRes.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())

                .musicalId(musical.getId())
                .musicalTitle(musical.getTitle())
                .location(musical.getLocation())
                .musicalImageUrl(musical.getImageUrl())

                .viewedDate(ticket.getViewedDate())
                .ticketImageUrl(ticket.getTicketImageUrl())

                .floor(ticket.getFloor())
                .zone(ticket.getZone())
                .col(ticket.getCol())
                .number(ticket.getNumber())

                .actors(actors)

                .build();
    }

    public static ActorRes toActorRes(Actor actor) {
        return ActorRes.builder()
                .id(actor.getId())
                .name(actor.getName())
                .actorImageUrl(actor.getActorImageUrl())
                .build();
    }
}
