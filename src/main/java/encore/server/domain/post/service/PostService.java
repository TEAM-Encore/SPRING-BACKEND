package encore.server.domain.post.service;

import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Slice<SimplePostRes> getPostPagination(Long cursor, String category,
                                                  String type, String searchWord, Pageable pageable) {

        return postRepository.findPostsByCursor(cursor, category, type, searchWord, pageable);
    }
}