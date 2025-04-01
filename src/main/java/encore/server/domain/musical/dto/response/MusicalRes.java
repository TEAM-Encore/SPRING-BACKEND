package encore.server.domain.musical.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.musical.entity.Musical;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MusicalRes(
        Long musicalId,
        String title,
        Long series,
        String location,
        List<String> showTimes,
        String imageUrl
) {
    public static MusicalRes from(Musical musical) {
        return MusicalRes.builder()
                .musicalId(musical.getId())
                .title(musical.getTitle())
                .series(musical.getSeries())
                .location(musical.getLocation())
                .showTimes(musical.getShowTimes().stream()
                        .map(showTime -> showTime.getDay() + " " + showTime.getTime())
                        .collect(Collectors.toList()))
                .imageUrl(musical.getImageUrl())
                .build();
    }
}