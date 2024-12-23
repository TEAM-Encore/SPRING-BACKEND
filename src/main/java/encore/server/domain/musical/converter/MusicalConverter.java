package encore.server.domain.musical.converter;

import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;

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
}
