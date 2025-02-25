package encore.server.domain.musical.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.musical.enumerate.Day;
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
        String age,
        Long series,
        String imageUrl,
        boolean isFeatured,
        List<MusicalActorRes> actors,
        List<ShowTimeRes> showTimes
) {
    @Builder
    public record MusicalActorRes(
            String actorName,
            String actorImageUrl,
            String roleName,
            boolean isMainActor
    ) {
    }
    @Builder
    public record ShowTimeRes(
            Day day,
            String time
    ) {
    }
}

