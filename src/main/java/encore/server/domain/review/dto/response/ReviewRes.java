package encore.server.domain.review.dto.response;

import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.review.entity.ReviewData;
import lombok.Builder;

import java.util.List;

@Builder
public record ReviewRes(
        Long reviewId,
        Long ticketId,
        Long userId,
        String title,
        List<Tag> tags,
        ReviewData reviewData
) {
}
