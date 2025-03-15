package encore.server.domain.inquiry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ServiceUseRequest(
    @NotBlank(message = "content is blank")
    @Schema(description = "내용", example = "서비스에 버그가 있어요")
    String content,
    @NotBlank(message = "email_to_receive is blank")
    @Schema(description = "응답 받을 이메일 주소", example = "encore@naver.com")
    String emailToReceive,
    String imageUrl
) {

}
