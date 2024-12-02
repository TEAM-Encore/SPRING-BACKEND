package encore.server.domain.review.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ViewImageRes(
        List<ViewImage> viewImages
) {
    @Builder
    public static record ViewImage(
            Long id,
            String url,
            Long level
    ) {
    }
}
