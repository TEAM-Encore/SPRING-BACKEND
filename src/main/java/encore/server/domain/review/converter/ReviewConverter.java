package encore.server.domain.review.converter;

import encore.server.domain.review.dto.request.ReviewDataReq;
import encore.server.domain.review.dto.response.*;
import encore.server.domain.review.entity.ReviewData;
import encore.server.domain.review.entity.ReviewTags;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.entity.Review;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Companion;
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

    private static List<String> tagToString(List<ReviewTags> tags) {
        return tags.stream()
                .map(reviewTag -> reviewTag.getTag().name())
                .collect(Collectors.toList());
    }

    //Todo: 티켓 완성 시 패키지 옮기기
    private static ReviewDetailRes.TicketRes toTicketRes(Ticket ticket) {
        return ReviewDetailRes.TicketRes.builder()
                .ticketId(ticket.getId())
                .ticketTitle(ticket.getTitle())
                .seat(ticket.getSeat())
                .viewedDate(ticket.getViewedDate())
                .imageUrl(ticket.getTicketImageUrl())
                .actors(ticket.getActors()
                        .stream()
                        .map(Actor::getName)
                        .collect(Collectors.toList()))
                .build();
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

    private static ReviewDataRes toReviewDataRes(ReviewData reviewData) {
        return ReviewDataRes.builder()
                .view(ReviewDataRes.View.builder()
                        .viewLevel(reviewData.getView().getViewLevel())
                        .viewReview(reviewData.getView().getViewReview())
                        .build())
                .sound(ReviewDataRes.Sound.builder()
                        .soundLevel(reviewData.getSound().getSoundLevel())
                        .soundReview(reviewData.getSound().getSoundReview())
                        .build())
                .facility(ReviewDataRes.Facility.builder()
                        .facilityLevel(reviewData.getFacility().getFacilityLevel())
                        .facilityReview(reviewData.getFacility().getFacilityReview())
                        .build())
                .rating(ReviewDataRes.Rating.builder()
                        .numberRating(reviewData.getRating().getNumberRating())
                        .storyRating(reviewData.getRating().getStoryRating())
                        .revisitRating(reviewData.getRating().getRevisitRating())
                        .actorRating(reviewData.getRating().getActorRating())
                        .performanceRating(reviewData.getRating().getPerformanceRating())
                        .totalRating(reviewData.getRating().getTotalRating())
                        .ratingReview(reviewData.getRating().getRatingReview())
                        .build())
                .build();
    }

    public static ReviewRes toReviewRes(Review review, String elapsedTime) {
        return ReviewRes.builder()
                .title(review.getTitle())
                .nickName(review.getUser().getNickName())
                .elapsedTime(elapsedTime)
                .totalRating(review.getReviewData().getRating().getTotalRating())
                .viewCount(review.getViewCount())
                .likeCount(review.getLikeCount())
                .build();
    }

    public static ReviewSummaryRes toReviewSummaryRes(List<ReviewRes> reviewResList, List<Review> reviews) {

        double averageTotalRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getTotalRating())
                .average().orElse(0.0);

        double averageNumberRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getNumberRating())
                .average().orElse(0.0);

        double averageStoryRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getStoryRating())
                .average().orElse(0.0);

        double averageRevisitRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getRevisitRating())
                .average().orElse(0.0);

        double averageActorRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getActorRating())
                .average().orElse(0.0);

        double averagePerformanceRating = reviews.stream()
                .mapToDouble(r -> r.getReviewData().getRating().getPerformanceRating())
                .average().orElse(0.0);

        return ReviewSummaryRes.builder()
                .reviews(reviewResList)
                .averageTotalRating((float) averageTotalRating)
                .averageNumberRating((float) averageNumberRating)
                .averageStoryRating((float) averageStoryRating)
                .averageRevisitRating((float) averageRevisitRating)
                .averageActorRating((float) averageActorRating)
                .averagePerformanceRating((float) averagePerformanceRating)
                .build();
    }
}