package encore.server.domain.ticket.controller;



import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.service.TicketService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ticket")
@Tag(name = "Ticket", description = "티켓 API")
public class TicketController {
    private final TicketService ticketService;


    @PostMapping
    public ResponseEntity<TicketCreateRes> createTicket(@RequestBody TicketCreateReq request) {
        TicketCreateRes response = ticketService.createTicket(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배우 검색", description = "배우 이름을 키워드로 검색합니다.")
    @GetMapping("/actors/search")
    public ApplicationResponse<List<ActorDTO>> searchActors(@RequestParam String keyword) {
        List<ActorDTO> responses = ticketService.searchActorsByName(keyword);
        return ApplicationResponse.ok(responses);
    }


}
