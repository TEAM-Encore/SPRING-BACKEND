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
@Tag(name = "댓글 API", description = "댓글 작성/수정/조회/삭제 API입니다.")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성 API", description = "특정 게시글에 댓글/대댓글을 작성합니다.")
    @PostMapping("/{post_id}")
    public ApplicationResponse<CommentRes> createComment(@PathVariable(value = "post_id") Long postId,
                                                         @Valid @RequestBody CommentReq commentReq) {
        Long userId = 1L; //수동 설정
        return ApplicationResponse.ok(commentService.create(postId, userId, commentReq));
    }

    @Operation(summary = "댓글 수정 API", description = "특정 댓글을 수정합니다.")
    @PutMapping("/{post_id}/{comment_id}")
    public ApplicationResponse<CommentRes> updateComment(@PathVariable(value = "post_id") Long postId,
                                                         @PathVariable(value = "comment_id") Long commentId,
                                                         @Valid @RequestBody CommentReq commentReq) {
        Long userId = 1L; //수동 설정
        return ApplicationResponse.ok(commentService.update(postId, commentId, userId, commentReq));
    }

    @Operation(summary = "댓글 조회 API", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @GetMapping("/{post_id}")
    public ApplicationResponse<?> getComments(@PathVariable(value = "post_id") Long postId) {
        Long userId = 1L; //수동 설정
        return ApplicationResponse.ok(commentService.getComments(postId, userId));
    }

    @Operation(summary = "댓글 삭제 API", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/{post_id}/{comment_id}")
    public ApplicationResponse<?> deleteComment(@PathVariable(value = "post_id") Long postId,
                                                         @PathVariable(value = "comment_id") Long commentId) {
        Long userId = 1L; //수동 설정
        commentService.delete(postId, commentId, userId);
        return ApplicationResponse.ok();
    }
}
