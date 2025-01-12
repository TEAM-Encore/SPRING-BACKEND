package encore.server.domain.musical.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MusicalSimpleRes(
        Long id,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String location,
        String imageUrl

) {
}
