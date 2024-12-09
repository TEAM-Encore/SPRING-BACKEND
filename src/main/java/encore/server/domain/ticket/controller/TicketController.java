package encore.server.domain.ticket.controller;

import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.response.PostCreateRes;
import encore.server.domain.post.service.PostService;
import encore.server.domain.ticket.dto.request.TicketReq;
import encore.server.domain.ticket.service.TicketService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ticket")
@Tag(name = "Ticket", description = "티켓 API")
public class TicketController {
    private final TicketService ticketService;




//    @PostMapping("")
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "티켓북 생성 API", description = "티켓북을 생성합니다.")
//    public ApplicationResponse<TicketReq> createPost(@RequestBody @Valid TicketReq ticketReq, BindingResult bindingResult) {
//
//        Long ticketId = ticketService.createTicket(ticketReq);
//        ApplicationResponse.ok();
//    }
}
