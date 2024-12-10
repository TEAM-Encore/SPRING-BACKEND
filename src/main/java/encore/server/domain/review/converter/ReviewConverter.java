package encore.server.domain.review.converter;

import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.user.entity.User;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewConverter {

    public static Review toEntity(Ticket ticket, User user, ReviewReq req) {
        List<Tag> tags = convertTags(req.tags());

        return Review.builder()
                .ticket(ticket)
                .user(user)
                .title(req.title())
                .tags(tags)
                .reviewData(req.reviewData())
                .build();
    }

    public static ReviewRes toReviewRes(Review review) {
        return ReviewRes.builder()
                .reviewId(review.getId())
                .ticketId(review.getTicket().getId())
                .userId(review.getUser().getId())
                .title(review.getTitle())
                .tags(review.getTags())
                .reviewData(review.getReviewData())
                .build();
    }

    public static ViewImageRes toViewImageRes(List<ViewImage> viewImages) {
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

    private static List<Tag> convertTags(List<String> tags) {
        try {
            return tags.stream()
                    .map(Tag::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.REVIEW_TAG_NOT_FOUND_EXCEPTION);
        }
    }
}