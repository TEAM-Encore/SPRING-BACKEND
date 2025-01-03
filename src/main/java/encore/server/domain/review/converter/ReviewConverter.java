package encore.server.domain.review.converter;

import encore.server.domain.review.dto.request.ReviewDataReq;
import encore.server.domain.review.entity.ReviewData;
import encore.server.domain.review.entity.ReviewTags;
import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.user.entity.User;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewConverter {

    public static Review toEntity(Ticket ticket, User user, ReviewReq req) {
        Review review = Review.builder()
                .ticket(ticket)
                .user(user)
                .title(req.title())
                .reviewData(toReviewData(req.reviewDataReq()))
                .build();

        review.addTags(toReviewTags(req.tags(), review));
        return review;
    }

    private static List<ReviewTags> toReviewTags(List<String> tags, Review review) {
        try {
            return tags.stream()
                    .map(tag -> ReviewTags.builder()
                            .tag(Tag.valueOf(tag))
                            .review(review)
                            .build())
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.REVIEW_TAG_NOT_FOUND_EXCEPTION);
        }
    }

    private static ReviewData toReviewData(ReviewDataReq req){
        return ReviewData.builder()
                .view(ReviewData.View.builder()
                        .viewLevel(req.view().viewLevel())
                        .viewReview(req.view().viewReview()
                        ).build())
                .sound(ReviewData.Sound.builder()
                        .soundLevel(req.sound().soundLevel())
                        .soundReview(req.sound().soundReview())
                        .build())
                .facility(ReviewData.Facility.builder()
                        .facilityLevel(req.facility().facilityLevel())
                        .facilityReview(req.facility().facilityReview())
                        .build())
                .rating(ReviewData.Rating.builder()
                        .numberRating(req.rating().numberRating())
                        .storyRating(req.rating().storyRating())
                        .revisitRating(req.rating().revisitRating())
                        .actorRating(req.rating().actorRating())
                        .performanceRating(req.rating().performanceRating())
                        .totalRating(req.rating().totalRating())
                        .ratingReview(req.rating().ratingReview())
                        .build())
                .build();
    }
}