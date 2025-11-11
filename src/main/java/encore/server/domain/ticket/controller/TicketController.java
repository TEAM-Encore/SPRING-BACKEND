package encore.server.domain.ticket.controller;



import encore.server.domain.ticket.dto.response.ActorRes;
import encore.server.domain.ticket.dto.request.TicketCreateReq;
import encore.server.domain.ticket.dto.request.TicketUpdateReq;
import encore.server.domain.ticket.dto.response.*;
import encore.server.domain.ticket.service.ActorSeedService;
import encore.server.domain.ticket.service.TicketService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/tickets")
@Tag(name = "Ticket", description = "티켓 API")
public class TicketController {
    private final TicketService ticketService;
    private final ActorSeedService actorSeedService;

    @Operation(summary = "티켓 내역 생성")
    @PostMapping
    public ApplicationResponse<TicketRes> createTicket(@LoginUserId Long userId, @RequestBody TicketCreateReq request) {
        TicketRes response = ticketService.createTicket(request, userId);
        return ApplicationResponse.created(response);
    }

    @Operation(summary = "배우 검색", description = "배우 이름을 키워드로 검색합니다.")
    @GetMapping("/actors/search")
    public ApplicationResponse<List<ActorRes>> searchActors(@RequestParam String keyword) {
        List<ActorRes> responses = ticketService.searchActorsByName(keyword);
        return ApplicationResponse.ok(responses);
    }

    @PostMapping("/actors/seed")
    public ResponseEntity<ActorSeedRes> seedActors() {
        var result = actorSeedService.upsertFromClasspathCsv();
        return ResponseEntity.ok(new ActorSeedRes(result.inserted(), result.updated(), result.skipped()));
    }

    @Operation(summary = "티켓 내역 리스트 조회", description = "기간별 티켓 내역 리스트를 조회합니다.")
    @GetMapping("/list")
    public ApplicationResponse<List<TicketRes>> getTicketList(@LoginUserId Long userId, @RequestParam String dateRange) {
        List<TicketRes> ticketList = ticketService.getTicketList(userId, dateRange);
        return ApplicationResponse.ok(ticketList);
    }

    @Operation(summary = "티켓 내역 조회", description = "티켓을 id로 조회합니다.")
    @GetMapping("/{ticketId}")
    public ApplicationResponse<TicketRes> getTicketDetail(@LoginUserId Long userId, @PathVariable Long ticketId) {
        TicketRes detail = ticketService.getTicket(ticketId, userId);
        return ApplicationResponse.ok(detail);
    }

    @Operation(summary = "티켓 내역 수정", description = "티켓 내역을 수정합니다.")
    @PatchMapping("/{ticketId}")
    public ApplicationResponse<TicketRes> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketUpdateReq request
    ) {
        TicketRes updatedTicket = ticketService.updateTicket(ticketId, request);
        return ApplicationResponse.ok(updatedTicket);
    }

    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "티켓 내역 삭제", description = "티켓 내역을 삭제합니다.")
    public ApplicationResponse deletePost(@PathVariable("ticketId") Long ticketIdToDelete){

        ticketService.deleteTicket(ticketIdToDelete);

        return ApplicationResponse.ok();
    }

//    @Operation(summary = "배우 추가", description = "새로운 배우를 추가합니다.")
//    @PostMapping("/actors")
//    public ApplicationResponse<ActorDTO> addActor(@RequestBody ActorCreateReq actorCreateReq) {
//        Actor actor = ticketService.createNewActor(actorCreateReq);  // 배우 생성
//        return ApplicationResponse.ok(TicketConverter.toActorDTO(actor));
//    }

//    @Operation(summary = "뮤지컬 제목으로 티켓 내역 검색", description = "뮤지컬 제목을 통해 티켓 내역 목록을 검색합니다.")
//    @GetMapping("/search")
//    public ApplicationResponse<List<TicketSimpleRes>> getTicketsByMusicalTitle(@RequestParam String title) {
//        List<TicketSimpleRes> ticketList = ticketService.getTicketsByMusicalTitle(title);
//        return ApplicationResponse.ok(ticketList);
//    }
//
//    @Operation(summary = "리뷰 작성되지 않은 티켓 내역 리스트 조회", description = "리뷰가 작성되지 않은 티켓 내역 목록을 조회합니다.")
//    @GetMapping("/unreviewed")
//    public ApplicationResponse<List<TicketRes>> getUnreviewedTicketList(@LoginUserId Long userId) {
//        List<TicketRes> tickets = ticketService.getUnreviewedTicketList(userId);
//        return ApplicationResponse.ok(tickets);
//    }
}
