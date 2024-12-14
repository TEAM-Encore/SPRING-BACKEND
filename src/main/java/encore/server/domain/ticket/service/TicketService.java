package encore.server.domain.ticket.service;

import encore.server.domain.image.service.ImageService;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.ticket.converter.TicketConverter;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.ActorRepository;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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


        return new TicketCreateRes(ticket);

    }

    //배우 검색
    public List<ActorDTO> searchActorsByName(String keyword) {
        List<Actor> actors = actorRepository.findByNameContaining(keyword);
        return actors.stream()
                .map(TicketConverter::toActorDTO) // Converter를 활용한 변환
                .toList();
    }

    //티켓북 조회
    public List<TicketRes> getTicketList(String dateRange) {
        LocalDate startDate = switch (dateRange) {
            case "WEEK" -> LocalDate.now().minusWeeks(1);
            case "MONTH" -> LocalDate.now().minusMonths(1);
            case "YEAR" -> LocalDate.now().minusYears(1);
            default -> null; // ALL: 전체 조회
        };

        List<Ticket> tickets;

        if (startDate != null) {
            tickets = ticketRepository.findByViewedDateAfter(startDate);
        } else {
            tickets = ticketRepository.findAll();
        }

        return tickets.stream()
                .map(TicketRes::new)
                .collect(Collectors.toList());
    }

}
