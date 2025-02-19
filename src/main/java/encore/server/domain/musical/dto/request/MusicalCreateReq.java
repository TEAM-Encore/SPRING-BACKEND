package encore.server.domain.musical.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.musical.enumerate.Day;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MusicalCreateReq(
        String title,
        Long series,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long runningTime,
        String age,
        String imageUrl,
        boolean isFeatured,
        List<ActorRequest> actors,
        List<ShowTimeRequest> showTimes
) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ActorRequest(
            Long actorId,
            String roleName,
            boolean isMainActor
    ) {
    }
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ShowTimeRequest(
            Day day,
            String time
    ) {
    }
}