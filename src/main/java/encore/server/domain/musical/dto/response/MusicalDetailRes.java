package encore.server.domain.musical.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.entity.MusicalActor;
import encore.server.domain.musical.entity.ShowTime;
import encore.server.domain.musical.enumerate.Day;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public static MusicalDetailRes from(Musical musical) {
        return MusicalDetailRes.builder()
                .id(musical.getId())
                .title(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .location(musical.getLocation())
                .runningTime(musical.getRunningTime())
                .age(musical.getAge())
                .series(musical.getSeries())
                .imageUrl(musical.getImageUrl())
                .isFeatured(musical.isFeatured())
                .actors(musical.getMusicalActors().stream()
                        .map(MusicalActorRes::from)
                        .collect(Collectors.toList()))
                .showTimes(musical.getShowTimes().stream()
                        .map(ShowTimeRes::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Builder
    public record MusicalActorRes(
            String actorName,
            String actorImageUrl,
            String roleName,
            boolean isMainActor
    ) {
        public static MusicalActorRes from(MusicalActor actor) {
            return MusicalActorRes.builder()
                    .actorName(actor.getActor().getName())
                    .actorImageUrl(actor.getActor().getActorImageUrl())
                    .roleName(actor.getRoleName())
                    .isMainActor(actor.isMainActor())
                    .build();
        }
    }

    @Builder
    public record ShowTimeRes(
            Day day,
            String time
    ) {
        public static ShowTimeRes from(ShowTime showTime) {
            return ShowTimeRes.builder()
                    .day(showTime.getDay())
                    .time(showTime.getTime())
                    .build();
        }
    }
}
