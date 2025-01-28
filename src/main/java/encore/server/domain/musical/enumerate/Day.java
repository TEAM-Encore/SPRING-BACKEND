package encore.server.domain.musical.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Day {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일"),
    HOLIDAY("공휴일");

    private final String day;
}
