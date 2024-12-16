package encore.server.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewDetailRes(
        @Schema(description = "리뷰 ID", example = "1")
        Long reviewId,

        @Schema(description = "티켓 정보")
        TicketRes ticket,

        @Schema(description = "유저 ID", example = "1")
        Long userId,

        @Schema(description = "리뷰 제목", example = "위키드 리뷰")
        String title,

        @Schema(description = "태그 목록",
                example = "[\"MUSEUM_EXPERT\", \"PERFECT_REVIEW\", \"REVOLVING_DOOR\", \"BEST_VIEW\", " +
                        "\"BEST_SOUND\", \"BEST_FACILITIES\", \"NO_SELECT\"]")
        List<String> tags,

        @Schema(description = "리뷰 데이터")
        ReviewDataRes reviewDataRes,

        @Schema(description = "리뷰 잠금 해제 여부", example = "true")
        Boolean isUnlocked,

        @Schema(description = "나의 리뷰인지 여부", example = "true")
        Boolean isMyReview,

        @Schema(description = "조회수", example = "100")
        Long viewCount,

        @Schema(description = "좋아요 관련 데이터")
        ReviewLikeRes likeRes
) {
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record TicketRes(
            @Schema(description = "티켓 ID", example = "1")
            Long ticketId,

            @Schema(description = "티켓 제목", example = "위키드")
            String ticketTitle,

            @Schema(description = "공연 좌석", example = "세종문화회관 A구역 6열 4번")
            String seat,

            @Schema(description = "공연 날짜 및 시간", example = "2021-08-01 19:30")
            LocalDate viewedDate,

            @Schema(description = "공연 포스터", example = "https://www.image.com")
            String imageUrl,

            @Schema(description = "배우 리스트", example = "[\"김뮤뮤\", \"이뮤뮤\"]")
            List<String> actors
    ) {
    }
}
