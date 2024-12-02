package encore.server.domain.review.service;

import encore.server.domain.review.converter.ReviewConverter;
import encore.server.domain.review.dto.request.ReviewReq;
import encore.server.domain.review.dto.response.ReviewRes;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewRes createReview(Long ticketId, Long userId, ReviewReq req) {
        //validation
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TICKET_NOT_FOUND_EXCEPTION));

        if(!Objects.equals(ticket.getUser().getId(), userId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
        }

        if(ticket.getReview() != null) {
            throw new ApplicationException(ErrorCode.REVIEW_ALREADY_EXIST_EXCEPTION);
        }

        //create review
        Review review = ReviewConverter.toEntity(ticket, user, req);
        reviewRepository.save(review);

        return ReviewConverter.toResponse(review);

    }
}
