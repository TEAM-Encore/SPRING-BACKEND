package encore.server.domain.point.enumerate;

import lombok.Getter;

@Getter
public enum PointType {
    // 적립
    REVIEW_WRITE("리뷰 작성"),
    DAILY_LIKE("하루 한 번 좋아요"),
    MISSION_COMPLETE("미션 완료"),
    SIGN_UP_BONUS("가입 보너스"),
    ADMIN_GRANT("운영자 지급"),

    // 사용
    REVIEW_VIEW("리뷰 열람"),
    PURCHASE("상품 구매"),
    ADMIN_DEDUCT("운영자 차감"),

    // 취소
    REVIEW_DELETE("리뷰 삭제로 인한 회수"),
    REFUND("환불");

    private final String description;

    PointType(String description) {
        this.description = description;
    }
}
