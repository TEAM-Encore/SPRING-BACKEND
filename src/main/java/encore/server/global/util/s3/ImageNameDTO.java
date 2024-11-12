package encore.server.global.util.s3;

import lombok.Builder;

@Builder
public record ImageNameDTO(
        String imageName
){}