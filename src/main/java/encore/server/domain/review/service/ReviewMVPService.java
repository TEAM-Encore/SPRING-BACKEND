package encore.server.domain.review.service;

import encore.server.domain.review.converter.ReviewConverter;
import encore.server.domain.review.dto.request.ReviewCreateReq;
import encore.server.domain.review.dto.request.ReviewUpdateReq;
import encore.server.domain.review.dto.response.ReviewCreateRes;
import encore.server.domain.review.dto.response.ReviewDeleteRes;
import encore.server.domain.review.dto.response.ReviewDetailRes;
import encore.server.domain.review.dto.response.ReviewGetListRes;
import encore.server.domain.review.dto.response.ReviewListCursorBasedRes;
import encore.server.domain.review.dto.response.ReviewMVPLikeRes;
import encore.server.domain.review.dto.response.ReviewReportRes;
import encore.server.domain.review.dto.response.ReviewUpdateRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ReviewData;
import encore.server.domain.review.entity.ReviewLike;
import encore.server.domain.review.entity.ReviewReport;
import encore.server.domain.review.entity.UserReview;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.enumerate.LikeType;
import encore.server.domain.review.enumerate.ReportReason;
import encore.server.domain.review.mapping.LikeTypeMapping;
import encore.server.domain.review.repository.ReviewLikeRepository;
import encore.server.domain.review.repository.ReviewReportRepository;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.review.repository.UserReviewRepository;
import encore.server.domain.review.repository.ViewImageRepository;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewMVPService {

  private final UserRepository userRepository;
  private final TicketRepository ticketRepository;
  private final ReviewRepository reviewRepository;
  private final ViewImageRepository viewImageRepository;
  private final UserReviewRepository userReviewRepository;
  private final ReviewViewService reviewViewService;
  private final ReviewLikeRepository reviewLikeRepository;
  private final ReviewRecentSearchService reviewRecentSearchService;
  private final ReviewRelatedSearchService reviewRelatedSearchService;
  private final ReviewReportRepository reviewReportRepository;

  public ReviewDetailRes getReview(Long userId, Long reviewId) {
    // validation: user, review, isUnlocked
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findReviewDetail(reviewId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

    boolean isUnlocked = (Objects.equals(user.getId(), review.getUser().getId())) ||
        userReviewRepository.existsByUserIdAndReviewIdAndDeletedAtIsNull(userId, reviewId);

    if (!isUnlocked) {
      throw new ApplicationException(ErrorCode.REVIEW_LOCKED_EXCEPTION);
    }

    // 조회수 증가
    reviewViewService.addVisitedRedis(user, review);

    // likeType
    Optional<LikeTypeMapping> likeTypeMapping = reviewLikeRepository.findLikeTypeByReviewAndUser(review, user);
    LikeType likeType = likeTypeMapping.map(LikeTypeMapping::getLikeType).orElse(LikeType.NONE);

    // 업로드 시점
    String elapsedTime = review.getElapsedTime();

    // return: review detail response
    return ReviewDetailRes.of(review, isUnlocked, likeType, elapsedTime);
  }

  public ReviewListCursorBasedRes<ReviewGetListRes> getReviewList(Long userId, String searchKeyword, Long cursor, Pageable pageable) {

    // business logic: get review list
    //0. 검색어 로그에 저장
    if (searchKeyword != null && !searchKeyword.isBlank()) {
      reviewRecentSearchService.saveRecentSearchLog(searchKeyword, userId);
    }

    //1. cursor 기반으로 리뷰 리스트 조회
    List<Review> reviews = reviewRepository.findReviewListByCursorAndSearchKeyword(searchKeyword, cursor, pageable);

    //2. 리뷰 리스트가 없는 경우
    if (reviews.isEmpty()) {
      return new ReviewListCursorBasedRes<>(List.of(), false, null);
    }

    //3. 리뷰 리스트 조회 성공
    boolean hasNext = reviews.size() > pageable.getPageSize();

    List<Review> pageReviews =
        reviews.stream().limit(pageable.getPageSize()).toList();

    Long nextCursor = null;
    if (hasNext) {
      nextCursor = pageReviews.get(pageReviews.size() - 1).getId();
    }

    List<ReviewGetListRes> reviewSimpleResList = pageReviews.stream()
        .map(review -> ReviewGetListRes.of(review, review.getElapsedTime()))
        .collect(Collectors.toList());

    return new ReviewListCursorBasedRes<>(reviewSimpleResList, hasNext, nextCursor);
  }

  public ReviewListCursorBasedRes<ReviewGetListRes> getMyReviewList(Long userId,
      Long cursor, Pageable pageable) {

    // business logic: get review list
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // 커서 페이징에서는 offset 의미 없게 0 고정
    Pageable firstPageable = PageRequest.of(
        0,
        pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "id")
    );

    //1. cursor 기반으로 리뷰 리스트 조회
    Slice<Review> slice = reviewRepository.findReviewListByUserAndCursor(user, cursor, firstPageable);
    List<Review> reviews = slice.getContent();

    //2. 리뷰 리스트가 없는 경우
    if (reviews.isEmpty()) {
      return new ReviewListCursorBasedRes<>(List.of(), false, null);
    }

    //3. 리뷰 리스트 조회 성공
    boolean hasNext = slice.hasNext();
    Long nextCursor = reviews.get(reviews.size() - 1).getId();

    List<ReviewGetListRes> reviewSimpleResList = reviews.stream()
        .map(review -> ReviewGetListRes.of(review, review.getElapsedTime()))
        .collect(Collectors.toList());

    return new ReviewListCursorBasedRes<>(reviewSimpleResList, hasNext, nextCursor);
  }

  public ReviewListCursorBasedRes<ReviewGetListRes> getMyLikedReviewList(Long userId, Long cursor,
      Pageable pageable) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // 커서 페이징에서는 offset 의미 없게 0 고정
    Pageable firstPageable = PageRequest.of(
        0,
        pageable.getPageSize(),
        Sort.by(Sort.Direction.DESC, "id")
    );

    Slice<ReviewLike> slice = reviewLikeRepository.findByUserAndCursor(user, cursor, firstPageable);
    List<ReviewLike> reviewLikes = slice.getContent();

    // 보통 커서 리스트는 0개면 예외 말고 빈 리스트 반환
    if (reviewLikes.isEmpty()) {
      return new ReviewListCursorBasedRes<>(List.of(), false, null);
    }

    //3. 리뷰 리스트 조회 성공
    boolean hasNext = slice.hasNext();
    Long nextCursor = reviewLikes.get(reviewLikes.size() - 1).getId();

    List<ReviewGetListRes> reviewSimpleResList = reviewLikes.stream()
        .map(ReviewLike::getReview)
        .map(review -> ReviewGetListRes.of(review, review.getElapsedTime()))
        .collect(Collectors.toList());

    return new ReviewListCursorBasedRes<>(reviewSimpleResList, hasNext, nextCursor);
  }

  @Transactional
  public ReviewCreateRes createReview(Long userId, ReviewCreateReq req) {
    // validation: user, ticket, review
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Ticket ticket = ticketRepository.findByIdAndUserId(req.ticketId(), userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION));

    reviewRepository.findByTicketId(req.ticketId())
        .ifPresent(existingReview -> {
          throw new ApplicationException(ErrorCode.REVIEW_ALREADY_EXIST_EXCEPTION);
        });

    // business logic: create review
    Review review = ReviewConverter.toEntity(ticket, user, req);
    reviewRepository.save(review);
    reviewRelatedSearchService.updateAllSuggestions(userId, review);

    // 작성자에게 포인트 제공
    user.addPoint(50L);

    // return: review response
    return ReviewCreateRes.builder()
        .reviewId(review.getId())
        .success(true)
        .build();
  }

  @Transactional
  public ReviewUpdateRes updateReview(Long userId, Long reviewId, ReviewUpdateReq req) {
    //validation: user, review
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

    if (!Objects.equals(review.getUser().getId(), user.getId())) {
      throw new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
    }

    //business logic: update review
    ReviewData reviewData = ReviewConverter.toReviewData(req.reviewDataReq());
    review.updateReview(req.title(), reviewData);


    boolean isUnlocked =  (Objects.equals(user.getId(), review.getUser().getId()))  ||
        userReviewRepository.existsByUserIdAndReviewIdAndDeletedAtIsNull(userId, reviewId);

    if (!isUnlocked) {
      throw new ApplicationException(ErrorCode.REVIEW_LOCKED_EXCEPTION);
    }

    // likeType
    Optional<LikeTypeMapping> likeTypeMapping = reviewLikeRepository.findLikeTypeByReviewAndUser(review, user);
    LikeType likeType = likeTypeMapping.map(LikeTypeMapping::getLikeType).orElse(LikeType.NONE);

    // 업로드 시점
    String elapsedTime = review.getElapsedTime();

    // return: review detail response
    return ReviewUpdateRes.builder()
        .reviewId(reviewId)
        .success(true)
        .build();
  }

  @Transactional
  public ReviewDeleteRes deleteReview(Long userId, Long reviewId) {
    //validation: user, review
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

    if (!Objects.equals(review.getUser().getId(), user.getId())) {
      throw new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
    }

    //business logic: delete review
    reviewRepository.delete(review);

    return ReviewDeleteRes.builder()
        .reviewId(reviewId)
        .success(true)
        .build();
  }

  @Transactional
  public void unlockReview(Long userId, Long reviewId) {
    //validation: user, review
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findById(reviewId)
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

  @Transactional
  public ReviewMVPLikeRes likeReview(Long userId, Long reviewId) {
    // validation: user, review
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

    // business logic: like review
    ReviewLike reviewLike = reviewLikeRepository.findByReviewAndUser(review, user)
        .orElse(null);

    if (reviewLike == null) {
      reviewLike = ReviewLike.builder()
          .review(review)
          .user(user)
          .likeType(LikeType.FOLLOW_UP_RECOMMENDATION)
          .build();
      reviewLikeRepository.save(reviewLike);
    } else {
      reviewLikeRepository.delete(reviewLike);
    }

    long totalLikeCount = reviewLikeRepository.countByReview(review);
    review.setTotalLikeCount(totalLikeCount);

    // 리뷰 업데이트
    reviewRepository.save(review);

    // return: like
    return ReviewMVPLikeRes.builder()
        .totalLikeCount(totalLikeCount)
        .build();
  }

  @Transactional
  public ReviewReportRes reportReview(Long userId, Long reviewId, ReportReason reason) {
    //validation: user, review, self report, duplicated report
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION));

    if (!Objects.equals(review.getUser().getId(), user.getId())) {
      throw new ApplicationException(ErrorCode.REVIEW_SELF_REPORT_EXCEPTION);
    }

    if(reviewReportRepository.existsByReporterAndReview(user, review)){
      throw new ApplicationException(ErrorCode.REVIEW_REPORT_ALREADY_EXIST_EXCEPTION);
    }

    //business logic: report review
    ReviewReport reviewReport = ReviewReport.builder()
        .reporter(user)
        .review(review)
        .reason(reason)
        .build();

    reviewReportRepository.save(reviewReport);

    //return
    return ReviewReportRes.of(reviewReport);
  }

  public ViewImageRes viewImage() {
    //validation: cycle
    List<ViewImage> viewImages = viewImageRepository.findRandom4();

    //return: view image response
    return ViewImageRes.of(viewImages);
  }
}
