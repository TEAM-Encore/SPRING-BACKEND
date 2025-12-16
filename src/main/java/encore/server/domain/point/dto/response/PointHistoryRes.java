package encore.server.domain.point.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "포인트 히스토리 응답 DTO", example = """
        {
          "id": 1,
          "change_amount": 10,
          "balance_after": 120,
          "description": "[레미제라블] 후기 작성",
          "type": "REVIEW_WRITE",
          "created_at": "2024-01-01T12:00:00"
        }
        """)
public record PointHistoryRes(
        @Schema(description = "포인트 히스토리 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "포인트 변화량 (양수: 적립, 음수: 사용)",
                example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = Long.class
        )
        Long changeAmount,

        @Schema(
                description = "변화 후 잔액 (포인트 변동 후 남은 포인트)",
                example = "120",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long balanceAfter,

        @Schema(
                description = "포인트 변화 사유 (리뷰 제목 또는 이벤트 내용 포함)",
                example = "[레미제라블] 후기 작성",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String description,

        @Schema(
                description = """
                        포인트 타입:
                        - REVIEW_WRITE: 리뷰 작성 (+10)
                        - REVIEW_VIEW: 리뷰 열람 (-10)
                        - DAILY_LIKE: 하루 한 번 좋아요 (+5)
                        - MISSION_COMPLETE: 미션 완료
                        - SIGN_UP_BONUS: 가입 보너스
                        - ADMIN_GRANT: 운영자 지급
                        - PURCHASE: 상품 구매
                        - ADMIN_DEDUCT: 운영자 차감
                        """,
                example = "REVIEW_WRITE",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {
                        "REVIEW_WRITE", "REVIEW_VIEW", "DAILY_LIKE",
                        "MISSION_COMPLETE", "SIGN_UP_BONUS", "ADMIN_GRANT",
                        "PURCHASE", "ADMIN_DEDUCT", "REVIEW_DELETE", "REFUND"
                }
        )
        String type,

        @Schema(
                description = "포인트 변동 발생 시각",
                example = "2024-01-01T12:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string",
                format = "date-time"
        )
        LocalDateTime createdAt
) {
}
