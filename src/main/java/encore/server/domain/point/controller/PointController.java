package encore.server.domain.point.controller;

import encore.server.domain.point.dto.response.PointBalanceRes;
import encore.server.domain.point.dto.response.PointHistoryRes;
import encore.server.domain.point.service.PointService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/points")
@RequiredArgsConstructor
@Tag(
        name = "Point",
        description = """
                포인트 관리 API

                사용자의 포인트 적립/사용 내역을 조회하고 현재 잔액을 확인할 수 있습니다.

                **포인트 정책:**
                - 리뷰 작성: +10 포인트
                - 리뷰 열람: -10 포인트
                - 하루 한 번 좋아요: +5 포인트
                """
)
public class PointController {

    private final PointService pointService;

    @GetMapping("/history")
    @Operation(
            summary = "포인트 히스토리 조회",
            description = """
                    내 포인트 적립/사용 내역을 최신순으로 조회합니다.

                    **조회 가능한 정보:**
                    - 포인트 변화량 (적립: 양수, 사용: 음수)
                    - 변화 후 잔액
                    - 상세 사유 (예: "[레미제라블] 후기 작성")
                    - 포인트 타입 (REVIEW_WRITE, REVIEW_VIEW, DAILY_LIKE 등)
                    - 발생 시각

                    **인증:** JWT 토큰 필수 (자동으로 사용자 ID 파싱)

                    **정렬:** 최신순 (created_at DESC)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "1000",
                    description = "포인트 히스토리 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 1000,
                                              "message": "정상적인 요청입니다.",
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "change_amount": 10,
                                                    "balance_after": 120,
                                                    "description": "[레미제라블] 후기 작성",
                                                    "type": "REVIEW_WRITE",
                                                    "created_at": "2024-01-01T12:00:00"
                                                  },
                                                  {
                                                    "id": 2,
                                                    "change_amount": -10,
                                                    "balance_after": 110,
                                                    "description": "[오페라의 유령] 후기 열람",
                                                    "type": "REVIEW_VIEW",
                                                    "created_at": "2024-01-01T11:00:00"
                                                  },
                                                  {
                                                    "id": 3,
                                                    "change_amount": 5,
                                                    "balance_after": 120,
                                                    "description": "[하루 한 번 좋아요] 이벤트 참여",
                                                    "type": "DAILY_LIKE",
                                                    "created_at": "2024-01-01T10:00:00"
                                                  }
                                                ],
                                                "pageable": {
                                                  "page_number": 0,
                                                  "page_size": 20
                                                },
                                                "total_elements": 3,
                                                "total_pages": 1,
                                                "last": true,
                                                "first": true
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "3000",
                    description = "존재하지 않는 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 3000,
                                              "message": "존재하지 않는 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "14001",
                    description = "JWT 토큰 만료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 14001,
                                              "message": "JWT 가 만료되었습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    public ApplicationResponse<Page<PointHistoryRes>> getMyPointHistory(
            @Parameter(hidden = true) @LoginUserId Long userId,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "페이지 크기 (한 페이지에 표시할 항목 수)",
                    example = "20",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20")
            )
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointHistoryRes> history = pointService.getPointHistory(userId, pageable);
        return ApplicationResponse.ok(history);
    }

    @GetMapping("/balance")
    @Operation(
            summary = "포인트 잔액 조회",
            description = """
                    현재 보유 중인 포인트 잔액을 조회합니다.

                    **조회 가능한 정보:**
                    - 현재 사용 가능한 포인트 잔액

                    **인증:** JWT 토큰 필수 (자동으로 사용자 ID 파싱)

                    **활용:**
                    - 리뷰 열람 전 잔액 확인 (열람 시 10 포인트 차감)
                    - 포인트가 부족한 경우 적립 유도
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "1000",
                    description = "포인트 잔액 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 1000,
                                              "message": "정상적인 요청입니다.",
                                              "data": {
                                                "current_balance": 120
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "3000",
                    description = "존재하지 않는 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 3000,
                                              "message": "존재하지 않는 사용자입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "14001",
                    description = "JWT 토큰 만료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2024-01-01T12:00:00",
                                              "code": 14001,
                                              "message": "JWT 가 만료되었습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    public ApplicationResponse<PointBalanceRes> getMyBalance(
            @Parameter(hidden = true) @LoginUserId Long userId
    ) {
        PointBalanceRes balance = pointService.getBalance(userId);
        return ApplicationResponse.ok(balance);
    }
}
