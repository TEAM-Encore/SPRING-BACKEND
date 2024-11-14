package encore.server.domain.post.controller;

import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.service.PostService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    @GetMapping("/list")
    @Operation(summary = "게시글 페이징 조회", description = "게시글을 커서 기반 페이징으로 조회합니다.")
    public ApplicationResponse<Slice<SimplePostRes>> getPostPagination(@RequestParam(name = "cursor", required = false) Long cursor,
                                                              @RequestParam(name = "category", required = false) String category,
                                                              @RequestParam(name = "type", required = false) String type,
                                                              @RequestParam(name = "search_word", required = false) String searchWord,
                                                              @PageableDefault(size = 3) Pageable pageable) {
        return ApplicationResponse.ok(postService.getPostPagination(cursor, category, type, searchWord, pageable));
    }

    @GetMapping("/hashtag-list")
    @Operation(summary = "해시태그별 게시글 페이징 조회", description = "해시태그별 게시글을 커서 기반 페이징으로 조회합니다.")
    public ApplicationResponse<Slice<SimplePostRes>> getPostPaginationByHashtag(@RequestParam(name = "cursor", required = false) Long cursor,
                                                              @RequestParam(name = "hashtag", required = false) String hashtag,
                                                              @PageableDefault(size = 3) Pageable pageable) {
        return ApplicationResponse.ok(postService.getPostPaginationByHashtag(cursor, hashtag, pageable));
    }
}
