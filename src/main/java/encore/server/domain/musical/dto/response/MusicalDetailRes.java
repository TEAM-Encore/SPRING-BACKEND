package encore.server.domain.musical.dto.response;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MusicalDetailRes(
        Long id,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        Long runningTime,
        Long age,
        Long series,
        String imageUrl,
        List<MusicalActorRes> actors
) {
    @Builder
    public record MusicalActorRes(
            String actorName,
            String actorImageUrl,
            String roleName,
            boolean isMainActor
    ) {
    }
}

