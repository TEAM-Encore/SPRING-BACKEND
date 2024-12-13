package encore.server.domain.review.converter;

import encore.server.domain.review.dto.response.*;
import encore.server.domain.review.entity.ReviewData;
import encore.server.domain.review.entity.ReviewTags;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.enumerate.Tag;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.entity.Review;
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
                .reviewData(req.reviewData())
                .build();

        review.addTags(toReviewTags(req.tags(), review));
        return review;
    }

    public static ReviewRes toReviewRes(Review review) {
        return ReviewRes.builder()
                .reviewId(review.getId())
                .ticketId(review.getTicket().getId())
                .userId(review.getUser().getId())
                .title(review.getTitle())
                .tags(tagToString(review.getTags()))
                .reviewDataRes(toReviewDataRes(review.getReviewData()))
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

    public static ReviewDetailRes toReviewDetailRes(Review review, Boolean isUnlocked) {
        return ReviewDetailRes.builder()
                .reviewId(review.getId())
                .ticket(toTicketRes(review.getTicket()))
                .userId(review.getUser().getId())
                .title(review.getTitle())
                .tags(tagToString(review.getTags()))
                .reviewDataRes(toReviewDataRes(review.getReviewData()))
                .isUnlocked(isUnlocked)
                .isMyReview(review.getUser().getId().equals(review.getUser().getId()))
                .build();
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
                .imageUrl(ticket.getImageUrl())
                .companions(ticket.getCompanions().stream()
                        .map(Companion::getName)
                        .collect(Collectors.toList()))
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

    public static ReviewSimpleRes toReviewSimpleRes(Review review, String elapsedTime) {
        return ReviewSimpleRes.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .title(review.getTitle())
                .nickname(review.getUser().getNickName())
                .elapsedTime(elapsedTime)
                .totalRating(review.getReviewData().getRating().getTotalRating())
                .viewCount(review.getViewCount())
                .likeCount(review.getLikeCount())
                .build();
    }
}