package encore.server.domain.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tag {
    MUSEUM_EXPERT("뮤덕n년차"),
    PERFECT_REVIEW("총평만점"),
    REVOLVING_DOOR("회전문"),
    BEST_VIEW("시야최고"),
    BEST_SOUND("음향최고"),
    BEST_FACILITIES("시설최고"),
    NO_SELECT("선택안함");

    private final String name;
}