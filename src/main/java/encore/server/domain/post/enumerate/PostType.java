package encore.server.domain.post.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {
    INFORMATION("정보"),
    REVIEW("후기"),
    FREE("자유"),
    ACTOR("배우"),

    NO_SELECT("게시글 타입 미선택");

    private final String type;
}
