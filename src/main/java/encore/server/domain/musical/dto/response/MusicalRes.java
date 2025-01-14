package encore.server.domain.musical.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.util.List;

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
}