package encore.server.domain.musical.converter;

import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;

public class MusicalResponseConverter {
    public static MusicalRes toResponse(Musical musical) {
        return MusicalRes.builder()
                .musicalId(musical.getId())
                .title(musical.getTitle())
                .location(musical.getLocation())
                .imageUrl(musical.getImageUrl())
                .build();
    }

    public static MusicalDetailRes toMusicalDetailRes(Musical musical) {
        return MusicalDetailRes.builder()
                .id(musical.getId())
                .title(musical.getTitle())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .location(musical.getLocation())
                .imageUrl(musical.getImageUrl())
                .build();
    }

//    private static List<MusicalDetailRes.MusicalActorRes> toActorResponses(List<MusicalActor> musicalActors) {
//        return musicalActors.stream()
//                .map(actor -> MusicalDetailRes.MusicalActorRes.builder()
//                        .actorName(actor.getActor().getName())
//                        .actorImageUrl(actor.getActor().getActorImageUrl())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
