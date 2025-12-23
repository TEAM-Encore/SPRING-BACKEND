package encore.server.domain.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record UserGetMeRes(
    @Schema(
        description = "사용자의 현재 포인트 (앱 내 활동 또는 보상 포인트)",
        example = "120"
    )
    Long point,

    @Schema(
        description = "사용자 닉네임",
        example = "앙코르123"
    )
    String nickname,

    @Schema(
        description = "사용자가 작성한 게시글 수 (MVP에서는 사용하지 않음)",
        example = "5",
        deprecated = true
    )
    Long numOfWritePost,

    @Schema(
        description = "사용자 이메일 주소",
        example = "encore@gmail.com"
    )
    String email,

    @Schema(
        description = "사용자 프로필 조회용 Presigned URL",
        example = "https://s2example/dynamic/uuid"
    )
    String profileImageUrl
) {

}
