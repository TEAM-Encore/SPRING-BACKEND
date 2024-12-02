package encore.server.global.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    // 1000: Success Case
    SUCCESS(HttpStatus.OK, 1000, "정상적인 요청입니다."),
    CREATED(HttpStatus.CREATED, 1001, "정상적으로 생성되었습니다."),

    // 2000: Common Error
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 2000, "예기치 못한 오류가 발생했습니다."),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 2001, "존재하지 않는 리소스입니다."),
    INVALID_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, 2002, "올바르지 않은 요청 값입니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, 2003, "권한이 없는 요청입니다."),
    ALREADY_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST, 2004, "이미 삭제된 리소스입니다."),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, 2005, "인가되지 않는 요청입니다."),
    ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 2006, "이미 존재하는 리소스입니다."),
    INVALID_SORT_EXCEPTION(HttpStatus.BAD_REQUEST, 2007, "올바르지 않은 정렬 값입니다."),

    // 3000: User Error
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 3000, "존재하지 않는 사용자입니다."),

    // 4000: Post Error
    POST_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 4000, "존재하지 않는 게시글입니다."),

    // 5000: Comment Error
    COMMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 5000, "존재하지 않는 댓글입니다."),
    COMMENT_NOT_OWNER_EXCEPTION(HttpStatus.FORBIDDEN, 5001, "댓글 작성자만 수정할 수 있습니다."),

    // 6000: Like Error
    LIKE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 6000, "존재하지 않는 좋아요입니다."),

    // 7000: HashTag Error
    HASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 7000, "존재하지 않는 해시태그입니다."),

    // 8000: Review Error
    REVIEW_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 8000, "존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 8001, "이미 리뷰가 존재합니다."),
    REVIEW_TAG_NOT_FOUND_EXCEPTION(HttpStatus.BAD_REQUEST, 8002, "존재하지 않는 태그입니다."),

    // 9000: Mission Error
    MISSION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 9000, "존재하지 않는 미션입니다."),

    // 10000: Musical Error
    MUSICAL_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 10000, "존재하지 않는 뮤지컬입니다."),

    // 11000: Ticket Error
    TICKET_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 11000, "존재하지 않는 티켓입니다.");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
