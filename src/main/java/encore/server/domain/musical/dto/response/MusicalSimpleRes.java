package encore.server.domain.musical.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MusicalSimpleRes(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        String imageUrl

) {
}
