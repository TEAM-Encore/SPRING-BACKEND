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
    POINT_NOT_ENOUGH_EXCEPTION(HttpStatus.BAD_REQUEST, 2008, "포인트가 부족합니다."),
    SEARCH_LOG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 2009, "존재하지 않는 검색 로그입니다."),

    // 3000: User Error
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 3000, "존재하지 않는 사용자입니다."),
    PASSWORD_MISMATCH_EXCEPTION(HttpStatus.FORBIDDEN, 3001, "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 3002, "이미 존재하는 사용자입니다."),
    USER_HASHTAG_ALREADY_EXISTS_EXCEPTION(HttpStatus.BAD_REQUEST, 3005, "이미 존재하는 해시태그입니다."),
    USER_HASHTAG_LIMIT_EXCEEDED_EXCEPTION(HttpStatus.BAD_REQUEST, 3008, "해시태그는 최대 10개까지만 추가할 수 있습니다."),
    USER_HASHTAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 3009, "존재하지 않는 해시태그입니다."),

    //Related to User Nickname
    USER_NICKNAME_ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 3003, "이미 존재하는 닉네임입니다."),
    USER_NICKNAME_TOO_LONG_EXCEPTION(HttpStatus.BAD_REQUEST, 3004, "닉네임은 6자 이내 3자 이상이여야 합니다."),
    USER_NICKNAME_TOO_SHORT_EXCEPTION(HttpStatus.BAD_REQUEST, 3005, "닉네임은 3자 이상 6자 이내여야 합니다."),
    USER_NICKNAME_INVALID_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, 3006, "닉네임은 한글, 영어, 숫자로만 구성되어야 합니다."),
    USER_NICKNAME_CONTAINS_WHITESPACE_EXCEPTION(HttpStatus.BAD_REQUEST, 3007 ,"닉네임에 공백이 포함되면 안됩니다." ),


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
    REVIEW_LOCKED_EXCEPTION(HttpStatus.FORBIDDEN, 8003, "리뷰가 잠겨있습니다."),
    REVIEW_ALREADY_UNLOCKED_EXCEPTION(HttpStatus.BAD_REQUEST, 8004, "이미 리뷰 잠금이 해제되었습니다."),
    REVIEW_LIKE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 8007, "존재하지 않는 리뷰 좋아요입니다."),
    REVIEW_SELF_REPORT_EXCEPTION(HttpStatus.FORBIDDEN,8005,"본인의 리뷰는 신고할 수 없습니다." ),
    REVIEW_REPORT_ALREADY_EXIST_EXCEPTION(HttpStatus.BAD_REQUEST, 8006, "이미 신고한 리뷰입니다."),
    INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, 8007, "Sort 필드가 잘못되었습니다."),

    // 9000: Mission Error
    MISSION_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 9000, "존재하지 않는 미션입니다."),

    // 10000: Musical Error
    MUSICAL_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 10000, "존재하지 않는 뮤지컬입니다."),
    KOPIS_SERVICE_KEY_INVALID(HttpStatus.UNAUTHORIZED, 10001, "KOPIS 서비스키가 유효하지 않습니다."),
    KOPIS_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 10002, "KOPIS 서버 에러입니다."),

    // 11000: Ticket Error
    TICKET_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 11000, "존재하지 않는 티켓입니다."),

    // 12000: Actor Error
    ACTOR_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, 12000, "존재하지 않는 배우입니다."),

    // 13000: Subscription Error
    ALREADY_SUBSCRIBE_EXCEPTION(HttpStatus.BAD_REQUEST, 13000, "이미 구독중입니다."),
    ALREADY_UNSUBSCRIBE_EXCEPTION(HttpStatus.BAD_REQUEST, 13001, "이미 구독중이 아닙니다."),

    // 14000: JWT Error
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, 14000, "유효하지 않은 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 14001, "JWT 가 만료되었습니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, 14002, "지원되지 않는 JWT 입니다."),
    JWT_CLAIMS_IS_EMPTY(HttpStatus.UNAUTHORIZED, 14003, "JWT 가 포함되지 않았습니다."),
    JWT_NOT_FOUND(HttpStatus.NOT_FOUND, 14004, "헤더에서 JWT 를 찾을 수 없습니다.");



    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
