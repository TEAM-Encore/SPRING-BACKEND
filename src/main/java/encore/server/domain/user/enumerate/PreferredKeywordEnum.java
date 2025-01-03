package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PreferredKeywordEnum {
    EMOTIONAL("깊은 감동을 받게되는"),
    ENTERTAINING("흥미롭고 재미있는"),
    QUALITY("넘버 퀄리티가 뛰어난"),
    STAGE_DESIGN("무대 연출력이 뛰어난"),
    CASTING("캐스팅 페어합이 좋은"),
    ACTING("배우의 연기력이 좋은"),
    ACTOR("최애 배우가 출연하는"),
    LIGHT("가볍게 보기 좋은"),
    STORY("스토리 라인이 탄탄한");

    private final String keywordText;
}
