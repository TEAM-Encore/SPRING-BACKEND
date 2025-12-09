package encore.server.domain.review.dto.response;

import java.util.List;

public record ReviewListCursorBasedRes<T> (
    List<T> content,
    boolean hasNext,
    Long nextCursor
) {

}
