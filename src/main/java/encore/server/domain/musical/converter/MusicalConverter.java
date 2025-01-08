package encore.server.domain.musical.converter;

import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.entity.MusicalActor;

import java.util.List;
import java.util.stream.Collectors;

public class MusicalConverter {
    public static MusicalRes toResponse(Musical musical) {
        return MusicalRes.builder()
                .musicalId(musical.getId())
                .title(musical.getTitle())
                .series(musical.getSeries())
                .location(musical.getLocation())
                .showTimes(musical.getShowTimes())
                .build();
    }

    public static MusicalDetailRes toMusicalDetailRes(Musical musical) {
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
                .actors(toActorResponses(musical.getMusicalActors()))
                .build();
    }

    private static List<MusicalDetailRes.MusicalActorRes> toActorResponses(List<MusicalActor> musicalActors) {
        return musicalActors.stream()
                .map(actor -> MusicalDetailRes.MusicalActorRes.builder()
                        .actorName(actor.getActor().getName())
                        .actorImageUrl(actor.getActor().getActorImageUrl())
                        .roleName(actor.getRoleName())
                        .isMainActor(actor.isMainActor())
                        .build())
                .collect(Collectors.toList());
    }
}
