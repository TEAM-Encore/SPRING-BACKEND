package encore.server.domain.review.service;

import encore.server.domain.review.converter.ReviewConverter;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.dto.response.ReviewSimpleRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.UserReview;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.review.repository.UserReviewRepository;
import encore.server.domain.review.repository.ViewImageRepository;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;
    private final ViewImageRepository viewImageRepository;
    private final UserReviewRepository userReviewRepository;
    private final ReviewViewService reviewViewService;

    @Transactional
    public ReviewRes createReview(Long ticketId, Long userId, ReviewReq req) {
        // validation: user, ticket, review
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Ticket ticket = ticketRepository.findByIdAndUserIdAndDeletedAtIsNull(ticketId, userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION));

        reviewRepository.findByTicketIdAndDeletedAtIsNull(ticketId)
                .ifPresent(existingReview -> {
                    throw new ApplicationException(ErrorCode.REVIEW_ALREADY_EXIST_EXCEPTION);
                });

        // business logic: create review
        Review review = ReviewConverter.toEntity(ticket, user, req);
        reviewRepository.save(review);

        // return: review response
        return ReviewConverter.toReviewRes(review);
    }

    public ViewImageRes viewImage(Long cycle) {
        //validation: cycle
        long start = (cycle - 1) * 4 + 1;
        List<ViewImage> viewImages = viewImageRepository.findByIdBetween(start, start + 3);

        //return: view image response
        return ReviewConverter.toViewImageRes(viewImages);
    }

    public ReviewDetailRes getReview(Long userId, Long reviewId) {
        // validation: user, review, isUnlocked
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Review review = reviewRepository.findReviewDetail(reviewId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

        boolean isUnlocked = Objects.equals(user.getId(), review.getUser().getId()) ||
                userReviewRepository.existsByUserIdAndReviewIdAndDeletedAtIsNull(userId, reviewId);

        if (!isUnlocked) {
            throw new ApplicationException(ErrorCode.REVIEW_LOCKED_EXCEPTION);
        }

        // 조회수 증가
        reviewViewService.addVisitedRedis(user, review);

        // return: review detail response
        return ReviewConverter.toReviewDetailRes(review, isUnlocked);
    }

    @Transactional
    public void unlockReview(Long userId, Long reviewId) {
        //validation: user, review
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

        if (userReviewRepository.existsByUserIdAndReviewIdAndDeletedAtIsNull(userId, reviewId)) {
            throw new ApplicationException(ErrorCode.REVIEW_ALREADY_UNLOCKED_EXCEPTION);
        }

        //business logic & return: unlock review
        if(user.getPoint() < 40){
            throw new ApplicationException(ErrorCode.POINT_NOT_ENOUGH_EXCEPTION);
        }

        user.usePoint(40L);
        UserReview userReview = UserReview.builder()
                .user(user)
                .review(review)
                .build();
        userReviewRepository.save(userReview);
    }

    public List<ReviewSimpleRes> getUserReviews(Long userId, Long reviewId) {
        // validation: user
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // business logic: get user reviews (현재 보고 있는 review 제외)
        List<Review> reviews = reviewRepository.findByUserIdAndDeletedAtIsNull(userId);
        if (reviews.isEmpty()) {
            throw new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
        }

        // 필터링: 현재 보고 있는 리뷰를 제외한 다른 리뷰들만 필터링
        List<Review> otherReviews = reviews.stream()
                .filter(review -> !review.getId().equals(reviewId))
                .toList();

        if (otherReviews.isEmpty()) {
            throw new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
        }

        // return: review simple response for other reviews
        return otherReviews.stream()
                .map(review -> {
                    long minutesAgo = ChronoUnit.MINUTES.between(review.getCreatedAt(), LocalDateTime.now());
                    String elapsedTime = getElapsedTime(minutesAgo);
                    return ReviewConverter.toReviewSimpleRes(review, elapsedTime);
                })
                .collect(Collectors.toList());
    }

    private String getElapsedTime(long minutesAgo) {
        if (minutesAgo < 1) {
            return "방금 전";
        } else if (minutesAgo < 60) {
            return minutesAgo + "분 전";
        } else if (minutesAgo < 1440) {
            return minutesAgo / 60 + "시간 전";
        } else {
            return minutesAgo / 1440 + "일 전";
        }
    }
}
