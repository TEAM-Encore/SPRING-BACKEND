package encore.server.domain.review.dto.request;

import encore.server.domain.review.entity.ReviewData;

import java.util.List;

public record ReviewReq(
        String title,
        List<String> tags,
        ReviewData reviewData
) {
}
