package encore.server.domain.ticket.service;

import encore.server.domain.image.service.ImageService;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.ticket.converter.TicketConverter;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.request.TicketUpdateReq;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.dto.response.TicketUpdateRes;
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
                .orElseThrow(() -> new RuntimeException("Musical not found"));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //actordto->actor
        List<Actor> actors = request.actors().stream()
                .map(actorDTO -> Actor.builder()
                        .id(actorDTO.id())
                        .name(actorDTO.name())
                        .actorImageUrl(actorDTO.actorImageUrl())
                        .build())
                .collect(Collectors.toList());

        //business logic
        //create ticket
        Ticket ticket = Ticket.builder()
                .user(user)
                .musical(musical)
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

    //티켓북 조회
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
                            .id(actorDTO.id())
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
}
