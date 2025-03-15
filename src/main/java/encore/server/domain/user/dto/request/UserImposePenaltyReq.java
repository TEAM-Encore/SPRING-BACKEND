package encore.server.domain.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.user.enumerate.PenaltyStatus;
import encore.server.domain.user.enumerate.PenaltyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserImposePenaltyReq(
    @NotNull(message = "정지할 유저 아이디가 Null 입니다.")
    @Schema(description = "정지할 유저 아이디", example = "2")
    Long userId,

    @NotNull(message = "패널티 종류가 Null 입니다.")
    @Schema(description = "패널티 종류", example = "ONE_WEEK")
    PenaltyType type,

    @NotBlank(message = "정지 사유가 Blank 입니다.")
    @Schema(description = "정지 사유", example = "비정상적 활동")
    String reason,

    @NotNull(message = "정지 상태가 Null 입니다.")
    @Schema(description = "정지 상태", example = "ACTIVE")
    PenaltyStatus status
) {

}
