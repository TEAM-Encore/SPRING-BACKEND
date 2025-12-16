package encore.server.domain.point.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "포인트 잔액 응답 DTO", example = """
        {
          "current_balance": 120
        }
        """)
public record PointBalanceRes(
        @Schema(
                description = "현재 보유 중인 포인트 잔액 (사용 가능한 포인트)",
                example = "120",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "0"
        )
        Long currentBalance
) {
}
