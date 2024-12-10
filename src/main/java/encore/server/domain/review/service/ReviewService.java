package encore.server.domain.review.service;

import encore.server.domain.review.converter.ReviewConverter;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.dto.response.ViewImageRes;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.entity.ViewImage;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.review.repository.ViewImageRepository;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;
    private final ViewImageRepository viewImageRepository;

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
}
