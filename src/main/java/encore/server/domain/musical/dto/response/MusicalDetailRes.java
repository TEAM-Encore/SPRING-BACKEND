package encore.server.domain.musical.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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

