package encore.server.domain.ticket.service;

import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.entity.Ticket;
import encore.server.domain.ticket.repository.TicketRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final MusicalRepository musicalRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketCreateRes createTicket(TicketCreateReq request) {
        // 뮤지컬 ID를 이용해 뮤지컬을 찾아옴
        Musical musical = musicalRepository.findById(request.musicalId())
                .orElseThrow(() -> new RuntimeException("Musical not found"));

        // 사용자 ID를 이용해 User를 찾아옴
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 기본 티켓 생성
        Ticket ticket = Ticket.builder()
                .user(user)  // User 객체 설정
                .musical(musical)
                .viewedDate(request.viewedDate())
                .showTime(request.showTime())
                .seat(request.seat())
                .build();

        ticketRepository.save(ticket);

        // 티켓 생성 후, 반환
        return new TicketCreateRes(ticket);
    }

}
