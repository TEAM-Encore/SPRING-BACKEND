package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ViewingFrequency {
    LEVEL1("연 1~3회"),
    LEVEL2("연 4~7회"),
    LEVEL3("연 8회 이상");

    private final String text;

}
