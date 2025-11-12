package encore.server.domain.review.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
    INAPPROPRIATE_REVIEW("주제와 관련없는 내용"),
    COMMERCIAL_SELLING("상업적 판매/도배"),
    PROFANITY_INSULT("욕설/비하"),
    PORNOGRAPHY_INAPPROPRIATE("음란물/불건전한 대화"),
    LEAK_IMPERSONATION("유출/사칭");

    private final String reason;
}
