package encore.server.domain.ticket.controller;



import encore.server.domain.ticket.converter.TicketConverter;
import encore.server.domain.ticket.dto.request.ActorCreateReq;
import encore.server.domain.ticket.dto.request.ActorDTO;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.request.TicketUpdateReq;
import encore.server.domain.ticket.dto.response.TicketCreateRes;
import encore.server.domain.ticket.dto.response.TicketRes;
import encore.server.domain.ticket.dto.response.TicketSimpleRes;
import encore.server.domain.ticket.dto.response.TicketUpdateRes;
import encore.server.domain.ticket.entity.Actor;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ticket")
@Tag(name = "Ticket", description = "티켓 API")
public class TicketController {
    private final TicketService ticketService;

    @Operation(summary = "티켓북 생성", description = "티켓북을 생성합니다.")
    @PostMapping
    public ApplicationResponse<TicketCreateRes> createTicket(@RequestBody TicketCreateReq request) {
        TicketCreateRes response = ticketService.createTicket(request);
        return ApplicationResponse.created(response);
    }

    @Operation(summary = "배우 검색", description = "배우 이름을 키워드로 검색합니다.")
    @GetMapping("/actors/search")
    public ApplicationResponse<List<ActorDTO>> searchActors(@RequestParam String keyword) {
        List<ActorDTO> responses = ticketService.searchActorsByName(keyword);
        return ApplicationResponse.ok(responses);
    }

    @Operation(summary = "배우 추가", description = "새로운 배우를 추가합니다.")
    @PostMapping("/actors")
    public ApplicationResponse<ActorDTO> addActor(@RequestBody ActorCreateReq actorCreateReq) {
        Actor actor = ticketService.createNewActor(actorCreateReq);  // 배우 생성
        return ApplicationResponse.ok(TicketConverter.toActorDTO(actor));
    }


    @Operation(summary = "티켓북 리스트 조회", description = "기간별 티켓북 리스트를 조회합니다.")
    @GetMapping("/list")
    public ApplicationResponse<List<TicketRes>> getTicketList(@RequestParam String dateRange) {
        Long userId = getUserId();
        List<TicketRes> ticketList = ticketService.getTicketList(userId, dateRange);
        return ApplicationResponse.ok(ticketList);
    }

    @Operation(summary = "티켓북 수정", description = "티켓북을 수정합니다.")
    @PatchMapping("/{ticketId}")
    public ApplicationResponse<TicketUpdateRes> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketUpdateReq request
    ) {
        TicketUpdateRes updatedTicket = ticketService.updateTicket(ticketId, request);
        return ApplicationResponse.ok(updatedTicket);
    }

    @DeleteMapping("/{ticket_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "티켓북 삭제 API", description = "티켓북을 삭제합니다.")
    public ApplicationResponse deletePost(@PathVariable("ticket_id") Long ticketIdToDelete){

        ticketService.deleteTicket(ticketIdToDelete);

        return ApplicationResponse.ok();
    }

    @Operation(summary = "뮤지컬 제목으로 티켓북 검색", description = "뮤지컬 제목을 통해 티켓북 목록을 검색합니다.")
    @GetMapping("/search")
    public ApplicationResponse<List<TicketSimpleRes>> getTicketsByMusicalTitle(@RequestParam String title) {
        List<TicketSimpleRes> ticketList = ticketService.getTicketsByMusicalTitle(title);
        return ApplicationResponse.ok(ticketList);
    }



    private Long getUserId() {
        return 1L; // Mock user ID
    }

}
