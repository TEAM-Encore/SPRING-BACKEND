package encore.server.domain.ticket.service;

import encore.server.domain.image.service.ImageService;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.ticket.converter.TicketConverter;
import encore.server.domain.ticket.dto.request.ActorCreateReq;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.request.TicketUpdateReq;
import encore.server.domain.ticket.dto.response.*;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.ActorRepository;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.BadRequestException;
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
    private final ImageService imageService;
    private final ReviewRepository reviewRepository;


    @Transactional
    public TicketCreateRes createTicket(TicketCreateReq request) {

        //validation
        Musical musical = musicalRepository.findById(request.musicalId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        // actorIds로 배우를 조회하여 리스트로 반환
        List<Actor> actors = actorRepository.findAllByIdIn(request.actorIds());
        if (actors.size() != request.actorIds().size()) {
            throw new ApplicationException(ErrorCode.ACTOR_NOT_FOUND_EXCEPTION);
        }

        //business logic
        //create ticket
        Ticket ticket = Ticket.builder()
                .user(user)
                .musical(musical)
                .title(musical.getTitle())
                .viewedDate(request.viewedDate())
                .showTime(request.showTime())
                .seat(request.seat())
                .actors(actors)
                .ticketImageUrl(request.ticketImageUrl())
                .build();

        ticketRepository.save(ticket);

        // TicketConverter 이용
        return TicketConverter.toTicketCreateRes(ticket);

    }

    //배우 검색
    public List<ActorDTO> searchActorsByName(String keyword) {
        List<Actor> actors = actorRepository.findByNameContaining(keyword);
        return actors.stream()
                .map(TicketConverter::toActorDTO) // Converter를 이용해 변환
                .toList();
    }

    // 새로운 배우 추가
    @Transactional
    public Actor createNewActor(ActorCreateReq actorCreateReq) {
        // actorImageUrl이 null일 경우 그냥 null로 설정
        String actorImageUrl = actorCreateReq.actorImageUrl();

        Actor actor = Actor.builder()
                .name(actorCreateReq.name())
                .actorImageUrl(actorCreateReq.actorImageUrl())  // null도 허용
                .build();

        return actorRepository.save(actor);
    }

    //티켓북 리스트 조회
    public List<TicketRes> getTicketList(Long userId, String dateRange) {

        // validation: user
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));


        LocalDate startDate = switch (dateRange) {
            case "WEEK" -> LocalDate.now().minusWeeks(1);
            case "MONTH" -> LocalDate.now().minusMonths(1);
            case "YEAR" -> LocalDate.now().minusYears(1);
            default -> null; // ALL: 전체 조회
        };

        List<Ticket> tickets;

        if (startDate != null) {
            tickets = ticketRepository.findByUserIdAndViewedDateAfterAndDeletedAtIsNull(userId, startDate);
        } else {
            tickets = ticketRepository.findByUserIdAndDeletedAtIsNull(userId);
        }

        return tickets.stream()
                .map(ticket -> {
                    Optional<Review> review = reviewRepository.findByTicketIdAndDeletedAtIsNull(ticket.getId());
                    return TicketConverter.toTicketRes(ticket, review);
                })
                .collect(Collectors.toList());
    }

    //티켓북 상세 조회
    @Transactional(readOnly = true)
    public TicketDetailRes getTicketDetail(Long ticketId, Long userId) {

        // validation: user, ticket
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        Ticket ticket = ticketRepository.findByIdAndUserIdAndDeletedAtIsNull(ticketId, userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TICKET_NOT_FOUND_EXCEPTION));

        Musical musical = ticket.getMusical();

        //연관 리뷰 정보
        Optional<Review> reviewOpt = reviewRepository.findByTicketIdAndUserIdAndDeletedAtIsNull(ticketId, userId);
        boolean hasReview = reviewOpt.isPresent();
        Long reviewId = hasReview ? reviewOpt.get().getId() : null;

        return TicketDetailRes.builder()
                .ticketId(ticket.getId())
                .ticketTitle(ticket.getTitle())
                .seat(ticket.getSeat())
                .showTime(ticket.getShowTime())
                .ticketImageUrl(ticket.getTicketImageUrl())
                .viewedDate(ticket.getViewedDate())
                .hasReview(hasReview)
                .reviewId(reviewId)
                .musicalId(musical.getId())
                .musicalTitle(musical.getTitle())
                .location(musical.getLocation())
                .startDate(musical.getStartDate())
                .endDate(musical.getEndDate())
                .runningTime(musical.getRunningTime())
                .age(musical.getAge())
                .imageUrl(musical.getImageUrl())
                .series(musical.getSeries())
                .actors(ticket.getActors().stream()
                        .map(TicketConverter::toActorDTO)
                        .toList())
                .build();
    }



    //티켓북 수정
    @Transactional
    public TicketUpdateRes updateTicket(Long ticketId, TicketUpdateReq request) {
        //Validation
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (request.title() != null) {
            ticket.setTitle(request.title());
        }
        if (request.viewedDate() != null) {
            ticket.setViewedDate(request.viewedDate());
        }
        if (request.showTime() != null) {
            ticket.setShowTime(request.showTime());
        }
        if (request.seat() != null) {
            ticket.setSeat(request.seat());
        }
        if (request.actors() != null) {
            List<Actor> actors = request.actors().stream()
                    .map(actorDTO -> Actor.builder()
                            .name(actorDTO.name())
                            .actorImageUrl(actorDTO.actorImageUrl())
                            .build())
                    .collect(Collectors.toList());
            ticket.setActors(actors);
        }
        if (request.ticketImageUrl() != null) {
            ticket.setTicketImageUrl(request.ticketImageUrl());
        }

        ticketRepository.save(ticket);
        return TicketConverter.toTicketUpdateRes(ticket);
    }

    //티켓북 삭제
    @Transactional
    public void deleteTicket(Long ticketId) {

        if(!ticketRepository.existsById(ticketId)){
            throw new BadRequestException("Ticket does not exist");
        }
        ticketRepository.softDeleteByTicketId(ticketId);
    }

    public List<TicketSimpleRes> getTicketsByMusicalTitle(String musicalTitle) {
        List<Ticket> tickets = ticketRepository.findByMusical_TitleContainingAndDeletedAtIsNull(musicalTitle);

        return tickets.stream()
                .map(TicketConverter::toTicketSimpleRes) // 간단한 DTO 변환 사용
                .collect(Collectors.toList());
    }

    //리뷰 작성하지 않은 티켓북만 조회
    public List<TicketRes> getUnreviewedTicketList(Long userId) {
        // validation
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        //business login
        // 전체 티켓 조회
        List<Ticket> tickets = ticketRepository.findByUserIdAndDeletedAtIsNull(userId);

        // 리뷰 없는 티켓만 필터링
        return tickets.stream()
                .filter(ticket -> reviewRepository.findByTicketIdAndDeletedAtIsNull(ticket.getId()).isEmpty())
                .map(ticket -> TicketConverter.toTicketRes(ticket, Optional.empty()))
                .collect(Collectors.toList());
    }



}
