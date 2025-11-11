package encore.server.domain.ticket.service;

import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.ticket.converter.TicketConverter;
import encore.server.domain.ticket.dto.response.ActorRes;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.request.TicketUpdateReq;
import encore.server.domain.ticket.dto.response.*;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.entity.TicketActor;
import encore.server.domain.ticket.repository.ActorRepository;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final MusicalRepository musicalRepository;
    private final UserRepository userRepository;
    private final ActorRepository actorRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public TicketRes createTicket(TicketCreateReq request, Long userId) {
        // 1. Validation
        Musical musical = musicalRepository.findById(request.musicalId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        List<Actor> actors = actorRepository.findAllByIdIn(request.actorIds());
        if (actors.size() != request.actorIds().size()) {
            throw new ApplicationException(ErrorCode.ACTOR_NOT_FOUND_EXCEPTION);
        }

        Ticket ticket = Ticket.builder()
                .musical(musical)
                .user(user)
                .viewedDate(request.viewedDate())
                .showTime(request.showTime())
                .ticketImageUrl(request.ticketImageUrl())
                .floor(request.floor())
                .zone(request.zone())
                .col(request.col())
                .number(request.number())
                .build();

        // 조회한 Actor 리스트를 TicketActor 엔티티로 변환
        List<TicketActor> ticketActors = actors.stream()
                .map(actor -> TicketActor.builder()
                        .ticket(ticket)
                        .actor(actor)
                        .build())
                .toList();

        ticket.getTicketActorList().addAll(ticketActors);

        return TicketConverter.toTicketRes(ticketRepository.save(ticket));
    }

    //배우 검색
    public List<ActorRes> searchActorsByName(String keyword) {
        List<Actor> actors = actorRepository.findByNameContaining(keyword);
        return actors.stream()
                .map(TicketConverter::toActorRes)
                .toList();
    }

    // 티켓 리스트 조회
    public List<TicketRes> getTicketList(Long userId, String dateRange) {

        if (!userRepository.existsById(userId)) {throw new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION);}

        LocalDate startDate = switch (dateRange) {
            case "WEEK" -> LocalDate.now().minusWeeks(1);
            case "MONTH" -> LocalDate.now().minusMonths(1);
            default -> null; // ALL: 전체 조회
        };

        List<Ticket> tickets;

        if (startDate != null) {
            tickets = ticketRepository.findByUserIdAndViewedDateAfter(userId, startDate);
        } else {
            tickets = ticketRepository.findByUserId(userId);
        }

        return tickets.stream()
                .map(TicketConverter::toTicketRes)
                .collect(Collectors.toList());
    }

    // 티켓 개별 조회
    @Transactional(readOnly = true)
    public TicketRes getTicket(Long ticketId, Long userId) {

        // validation: user, ticket
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Ticket ticket = ticketRepository.findByIdAndUserId(ticketId, userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TICKET_NOT_FOUND_EXCEPTION));

        Musical musical = ticket.getMusical();

        //연관 리뷰 정보
        Optional<Review> reviewOpt = reviewRepository.findByTicketIdAndUserId(ticketId, userId);
        boolean hasReview = reviewOpt.isPresent();
        Long reviewId = hasReview ? reviewOpt.get().getId() : null;

        return TicketConverter.toTicketRes(ticket);
    }

    // 티켓 수정
    @Transactional
    public TicketRes updateTicket(Long ticketId, TicketUpdateReq request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TICKET_NOT_FOUND_EXCEPTION));

        ticket.updateTicketImageUrl(request.ticketImageUrl());
        ticket.updateViewedDate(request.viewedDate());
        ticket.updateFloor(request.floor());
        ticket.updateZone(request.zone());
        ticket.updateCol(request.col());
        ticket.updateNumber(request.number());
        ticket.updateShowTime(request.showTime());

        if (request.actorIds() != null) {
            ticket.getTicketActorList().clear();

            List<Actor> newActors = actorRepository.findAllByIdIn(request.actorIds());
            if (newActors.size() != request.actorIds().size()) {
                throw new ApplicationException(ErrorCode.ACTOR_NOT_FOUND_EXCEPTION);
            }

            List<TicketActor> updatedActors = newActors.stream()
                    .map(actor -> TicketActor.builder()
                            .ticket(ticket)
                            .actor(actor)
                            .build())
                    .toList();

            ticket.getTicketActorList().addAll(updatedActors);
        }

        // TODO: 리뷰 업데이트
        // ticket.updateReview(reviewRepository.findById(request.reviewId()).orElse(null));

        return TicketConverter.toTicketRes(ticket);
    }

    // 티켓 삭제
    @Transactional
    public void deleteTicket(Long ticketId) {
        if(!ticketRepository.existsById(ticketId)){
            throw new ApplicationException(ErrorCode.TICKET_NOT_FOUND_EXCEPTION);
        }
        ticketRepository.deleteById(ticketId);
    }

    //    // 새로운 배우 추가
//    @Transactional
//    public Actor createNewActor(ActorCreateReq actorCreateReq) {
//        // actorImageUrl이 null일 경우 그냥 null로 설정
//        String actorImageUrl = actorCreateReq.actorImageUrl();
//
//        Actor actor = Actor.builder()
//                .name(actorCreateReq.name())
//                .actorImageUrl(actorCreateReq.actorImageUrl())  // null도 허용
//                .build();
//
//        return actorRepository.save(actor);
//    }

//    public List<TicketSimpleRes> getTicketsByMusicalTitle(String musicalTitle) {
//        List<Ticket> tickets = ticketRepository.findByMusical_TitleContaining(musicalTitle);
//
//        return tickets.stream()
//                .map(TicketConverter::toTicketSimpleRes) // 간단한 DTO 변환 사용
//                .collect(Collectors.toList());
//    }
//
//    //리뷰 작성하지 않은 티켓북만 조회
//    public List<TicketRes> getUnreviewedTicketList(Long userId) {
//        // validation
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
//
//        //business login
//        // 전체 티켓 조회
//        List<Ticket> tickets = ticketRepository.findByUserId(userId);
//
//        // 리뷰 없는 티켓만 필터링
//        return tickets.stream()
//                .filter(ticket -> reviewRepository.findByTicketId(ticket.getId()).isEmpty())
//                .map(ticket -> TicketConverter.toTicketRes(ticket, Optional.empty()))
//                .collect(Collectors.toList());
//    }
}
