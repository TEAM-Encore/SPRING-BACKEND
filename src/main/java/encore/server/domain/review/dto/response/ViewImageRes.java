package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ViewImageRes(
        List<ViewImage> viewImages
) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static record ViewImage(
            Long id,

            @Schema(description = "시야 이미지 URL", example = "https://www.image.com")
            String url,

            @Schema(description = "시야 레벨", example = "3")
            Long level
    ) {
    }

    public static ViewImageRes of(List<encore.server.domain.review.entity.ViewImage> viewImages) {
        return ViewImageRes.builder()
                .viewImages(viewImages.stream()
                        .map(viewImage -> ViewImageRes.ViewImage.builder()
                                .id(viewImage.getId())
                                .url(viewImage.getUrl())
                                .level(viewImage.getLevel())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
