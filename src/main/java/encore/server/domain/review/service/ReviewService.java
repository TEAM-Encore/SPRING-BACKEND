package encore.server.domain.review.service;

import encore.server.domain.review.converter.ReviewConverter;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.*;
import encore.server.domain.review.entity.*;
import encore.server.domain.review.repository.ReviewLikeRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewSearchService reviewSearchService;

    @Transactional
    public ReviewDetailRes createReview(Long ticketId, Long userId, ReviewReq req) {
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

        // 작성자에게 포인트 제공
        user.addPoint(50L);

        // 업로드 시점
        String elapsedTime = getElapsedTime(ChronoUnit.MINUTES.between(review.getCreatedAt(), LocalDateTime.now()));

        // return: review response
        return ReviewConverter.toReviewDetailRes(review, true, false, elapsedTime);
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

        // 좋아요 여부
        boolean isLike = reviewLikeRepository.existsByReviewAndUserAndIsLikeTrue(review, user);

        // 업로드 시점
        String elapsedTime = getElapsedTime(ChronoUnit.MINUTES.between(review.getCreatedAt(), LocalDateTime.now()));

        // return: review detail response
        return ReviewConverter.toReviewDetailRes(review, isUnlocked, isLike, elapsedTime);
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
        List<Review> otherReviews = reviewRepository.findUserReviews(userId, reviewId);

        if (otherReviews.isEmpty()) {
            throw new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
        }

        // return: review simple response for other reviews
        return otherReviews.stream()
                .map(this::convertToReviewSimpleRes)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewLikeRes likeReview(Long userId, Long reviewId) {
        // validation: user, review
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

        // business logic: like review
        ReviewLike reviewLike = reviewLikeRepository.findByReviewAndUser(review, user)
                .orElse(null);

        if (reviewLike == null) {
            reviewLike = ReviewLike.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewLikeRepository.save(reviewLike);
        } else {
            reviewLike.toggleLike(reviewLike.getIsLike());
        }

        long likeCount = reviewLikeRepository.countByReviewAndIsLikeTrue(review);
        review.setLikeCount(likeCount);
        reviewRepository.save(review);

        // return: like count
        return ReviewConverter.toReviewLikeRes(reviewLike.getIsLike(), likeCount);
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

    public List<ReviewSimpleRes> getPopularReviewList() {
        // business logic: get popular review list
        List<Review> reviews = reviewRepository.findPopularReviews();
        if (reviews.isEmpty()) {
            throw new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
        }

        // return: popular review list
       return reviews.stream()
                .map(this::convertToReviewSimpleRes)
                .collect(Collectors.toList());
    }

    public Slice<ReviewSimpleRes> getReviewList(Long userId, String searchKeyword, Long cursor, String tag, Pageable pageable) {
        // validation: user, review
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // business logic: get review list
        //0. 검색어 로그에 저장
        reviewSearchService.saveRecentSearchLog(searchKeyword, userId);

        //1. cursor 기반으로 리뷰 리스트 조회
        List<Review> reviews = reviewRepository.findReviewListByCursor(searchKeyword, cursor, tag, pageable);

        //2. 리뷰 리스트가 없는 경우
        if (reviews.isEmpty()) {
            throw new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
        }

        //3. 리뷰 리스트 조회 성공
        boolean hasNext = reviews.size() > pageable.getPageSize();
        List<ReviewSimpleRes> reviewSimpleResList = reviews.stream()
                .limit(pageable.getPageSize())
                .map(this::convertToReviewSimpleRes)
                .collect(Collectors.toList());

        return new SliceImpl<>(reviewSimpleResList, pageable, hasNext);
    }

    private ReviewSimpleRes convertToReviewSimpleRes(Review review) {
        long minutesAgo = ChronoUnit.MINUTES.between(review.getCreatedAt(), LocalDateTime.now());
        String elapsedTime = getElapsedTime(minutesAgo);
        Boolean isLike = reviewLikeRepository.existsByReviewAndUserAndIsLikeTrue(review, review.getUser());
        return ReviewConverter.toReviewSimpleRes(review, elapsedTime, isLike);
    }

    @Transactional
    public ReviewDetailRes updateReview(Long userId, Long reviewId, ReviewReq req) {
        //validation: user, review
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

        if (!Objects.equals(review.getUser().getId(), user.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        //business logic: update review
        ReviewData reviewData = ReviewConverter.toReviewData(req.reviewDataReq());
        review.updateReview(req, reviewData);

        //좋아요 여부
        boolean isLike = reviewLikeRepository.existsByReviewAndUserAndIsLikeTrue(review, user);

        // 업로드 시점
        String elapsedTime = getElapsedTime(ChronoUnit.MINUTES.between(review.getCreatedAt(), LocalDateTime.now()));

        // return: review response
        return ReviewConverter.toReviewDetailRes(review, true, isLike, elapsedTime);
    }

}
