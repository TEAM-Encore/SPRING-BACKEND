package encore.server.domain.ticket.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.ticket.entity.Actor;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ActorDTO(
                       Long id,
                       String name,
                       String actorImageUrl
) {
    public static ActorDTO from(Actor actor) {
        return ActorDTO.builder()
                .id(actor.getId())
                .name(actor.getName())
                .actorImageUrl(actor.getActorImageUrl())
                .build();
    }
}
