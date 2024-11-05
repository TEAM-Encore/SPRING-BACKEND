package encore.server.domain.post.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    // 정보 게시판
    OPERA_GLASS_RENTAL("오페라글라스 대여"),
    MUSICAL_TERMS("뮤지컬 용어"),
    EVENTS("이벤트"),

    // 후기 게시판
    VIEW_REVIEW("시야 후기"),
    GOODS_REVIEW("굿즈 후기"),
    PERFORMANCE_REVIEW("공연 감상 후기");

    private final String category;
}