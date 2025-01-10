package encore.server.domain.review.enumerate;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeType {
    FOLLOW_UP_RECOMMENDATION("후속추천"),
    FULL_OF_TIPS("꿀팁가득"),
    THOROUGH_ANALYSIS("꼼꼼분석"),
    NONE("좋아요 없음");

    private final String name;
}

