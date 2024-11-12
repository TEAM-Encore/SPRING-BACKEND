package encore.server.domain.image.dto;

import lombok.Builder;

@Builder
public record ImageNameDTO(
        String imageName
){}