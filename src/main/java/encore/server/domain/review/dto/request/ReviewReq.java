package encore.server.domain.review.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewReq(

        @Schema(description = "리뷰 제목", example = "위키드 리뷰")
        @Size(min = 1, max = 30, message = "제목은 30자 이하로 입력해주세요")
        String title,

        @Schema(description = "태그 목록",
                example = "[\"MUSEUM_EXPERT\", \"PERFECT_REVIEW\", \"REVOLVING_DOOR\", \"BEST_VIEW\", " +
                        "\"BEST_SOUND\", \"BEST_FACILITIES\", \"NO_SELECT\"]")
        List<String> tags,

        @Schema(description = "리뷰 데이터")
        ReviewDataReq reviewDataReq
) {
}
