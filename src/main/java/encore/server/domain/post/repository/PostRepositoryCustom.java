package encore.server.domain.post.repository;

import encore.server.domain.post.dto.response.SimplePostRes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepositoryCustom {
    Slice<SimplePostRes> findPostsByCursor(Long cursor, String category,
                                           String type, String searchWord, Pageable pageable);
}
