package encore.server.domain.comment.controller;


import encore.server.domain.comment.dto.request.CommentReq;
import encore.server.domain.comment.dto.response.CommentRes;
import encore.server.domain.comment.service.CommentService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성 API", description = "특정 게시글에 댓글/대댓글을 작성합니다.")
    @PostMapping("/{post-id}")
    public ApplicationResponse<CommentRes> createComment(@PathVariable(value = "post-id") Long postId,
                                                         @Valid @RequestBody CommentReq commentReq) {
        Long userId = 1L; //수동 설정
        return ApplicationResponse.ok(commentService.create(postId, userId, commentReq));
    }

    @Operation(summary = "댓글 수정 API", description = "특정 댓글을 수정합니다.")
    @PutMapping("/{post-id}/{comment-id}")
    public ApplicationResponse<CommentRes> updateComment(@PathVariable(value = "post-id") Long postId,
                                                         @PathVariable(value = "comment-id") Long commentId,
                                                         @Valid @RequestBody CommentReq commentReq) {
        Long userId = 1L; //수동 설정
        return ApplicationResponse.ok(commentService.update(postId, commentId, userId, commentReq));
    }
}
