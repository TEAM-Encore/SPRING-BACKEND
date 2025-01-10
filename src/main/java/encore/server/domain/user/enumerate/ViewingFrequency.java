package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ViewingFrequency {
    LEVEL1("가끔 기회되면 공연을 보러가요"),
    LEVEL2("일년에 3회 이상 보러가요"),
    LEVEL3("한달에 3회 이상 보러가요");

    private final String text;

}
