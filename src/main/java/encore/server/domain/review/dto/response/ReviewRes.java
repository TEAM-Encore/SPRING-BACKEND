package encore.server.domain.review.dto.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewRes(
        @Schema(description = "리뷰 제목", example = "위키드 리뷰")
        String title,

        @Schema(description = "유저 닉네임", example = "뮤사랑")
        String nickName,

        @Schema(description = "업로드 시점", example = "11분 전")
        String elapsedTime,

        @Schema(description = "총점", example = "4.5")
        Float totalRating,

        @Schema(description = "조회수", example = "100")
        Long viewCount,

        @Schema(description = "좋아요수", example = "50")
        Long likeCount


) {
}
