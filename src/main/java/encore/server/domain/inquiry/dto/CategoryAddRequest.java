package encore.server.domain.inquiry.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import encore.server.domain.post.enumerate.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CategoryAddRequest(
    @NotBlank(message = "reason is blank")
    @Schema(description = "카테고리 추가 이유", example = "카테고리가 필요하여 추가 요청합니다.")
    String reason,

    @NotNull(message = "request_post_type is Null")
    @Schema(description = "카테고리가 추가될 게시판", example = "INFORMATION")
    PostType requestPostType,

    @NotBlank(message = "request_category_name is blank")
    @Schema(description = "추가할 카테고리 이름", example = "새로운카테고리")
    String requestCategoryName
) {

}
