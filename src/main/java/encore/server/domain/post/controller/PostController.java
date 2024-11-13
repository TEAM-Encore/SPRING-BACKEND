package encore.server.domain.post.controller;

import encore.server.domain.post.dto.request.PostLikeReq;
import encore.server.domain.post.service.PostLikeService;
import encore.server.global.common.HttpResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static encore.server.global.common.HttpResponseEntity.success;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "Post", description = "게시글 API")
public class PostController {
    private final PostLikeService postLikeService;

    @PostMapping("/likes")
    public HttpResponseEntity.ResponseResult<?> toggleLike(@RequestBody @Valid PostLikeReq postLikeReq) throws Exception {
        postLikeService.toggleLike(postLikeReq);
        return success(null);
    }
}
