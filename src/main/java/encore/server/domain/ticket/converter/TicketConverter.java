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

}