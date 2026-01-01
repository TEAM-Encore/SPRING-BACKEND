package encore.server.domain.post.controller;

import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.request.PostLikeReq;
import encore.server.domain.post.dto.request.PostUpdateReq;
import encore.server.domain.post.dto.response.PostCreateRes;
import encore.server.domain.post.dto.response.PostDetailsGetRes;
import encore.server.domain.post.dto.response.SimplePostRes;
import encore.server.domain.post.service.PostLikeService;
import encore.server.domain.post.service.PostRelatedSearchService;
import encore.server.domain.post.service.PostService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.BadRequestException;
import encore.server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "게시판 API", description = "게시판 관련 기능 API입니다.")
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final PostRelatedSearchService postSearchService;

    @PostMapping("/likes")
    @Operation(summary = "게시글 좋아요 API", description = "게시글 좋아요를 누릅니다.")
    public ApplicationResponse<Void> toggleLike(@RequestBody @Valid PostLikeReq postLikeReq) throws Exception {
        postLikeService.toggleLike(postLikeReq);
        return  ApplicationResponse.ok();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "게시글 작성 API", description = "게시글을 작성합니다.")
    public ApplicationResponse<PostCreateRes> createPost(@RequestBody @Valid PostCreateReq postCreateReq, BindingResult bindingResult) {

        log.info("[POST]-[PostController]-[post] post API call");

        //Request Validation
        if(bindingResult.hasErrors()) {

            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append(",");
            });

            log.info("[POST]-[PostController]-[post] post API validation error: {}", errorMessages.toString());

            throw new BadRequestException(errorMessages.toString());
        }

        //Business logic
        Long postId = postService.createPost(postCreateReq, mockUserIdProvide());

        log.info("[POST]-[PostController]-[post] post API terminated successfully");

        //Response Create
        return new ApplicationResponse(LocalDateTime.now(), ErrorCode.SUCCESS.getCode(), "Post created successfully", new PostCreateRes(postId));
    }

    @PutMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "게시글 수정 API", description = "게시글을 수정합니다.")
    public ApplicationResponse<PostCreateRes> updatePost(@PathVariable("post_id") Long postIdToUpdate, @RequestBody @Valid PostUpdateReq postUpdateReq, BindingResult bindingResult){

        log.info("[POST]-[PostController]-[postUpdate] update API call");

        //Request Validation
        if(bindingResult.hasErrors()) {

            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append(",");
            });

            log.info("[POST]-[PostController]-[postUpdate] update API validation error: {}", errorMessages.toString());

            throw new BadRequestException(errorMessages.toString());
        }

        //Business logic
        Long postId = postService.updatePost(postIdToUpdate, postUpdateReq);

        return new ApplicationResponse<>(LocalDateTime.now(), ErrorCode.SUCCESS.getCode(), "Post updated successfully", new PostCreateRes(postId));
    }


    @DeleteMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제합니다.")
    public ApplicationResponse deletePost(@PathVariable("post_id") Long postIdToDelete){

        log.info("[POST]-[PostController]-[postDelete] post delete API call");

        postService.deletePost(postIdToDelete);

        log.info("[POST]-[PostController]-[postDelete] post delete API terminated successfully");

        return ApplicationResponse.ok();
    }


    @GetMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "게시글 상세 조회 API", description = "게시글 상세 정보를 조회합니다.")
    public ApplicationResponse<PostDetailsGetRes> getPostDetails(@PathVariable("post_id") Long postId){

        log.info("[POST]-[PostController]-[getPostDetails] /post/{post_id} API call");

        PostDetailsGetRes postDetailsGetRes = postService.getPostDetails(postId, mockUserIdProvide());

        return ApplicationResponse.ok(postDetailsGetRes);

    }

    @GetMapping("/list")
    @Operation(summary = "게시글 페이징 조회 API", description = "게시글을 커서 기반 페이징으로 조회합니다.")
    public ApplicationResponse<Slice<SimplePostRes>> getPostPagination(@RequestParam(name = "cursor", required = false) Long cursor,
                                                                       @RequestParam(name = "category", required = false) String category,
                                                                       @RequestParam(name = "type", required = false) String type,
                                                                       @RequestParam(name = "search_word", required = false) String searchWord,
        @ParameterObject @PageableDefault(size = 3, sort = "createdAt") Pageable pageable) {
        return ApplicationResponse.ok(postService.getPostPagination(cursor, category, type, searchWord, pageable, mockUserIdProvide()));
    }

    @GetMapping("/hashtag-list")
    @Operation(summary = "해시태그별 게시글 페이징 조회 API", description = "해시태그별 게시글을 커서 기반 페이징으로 조회합니다.")
    public ApplicationResponse<Slice<SimplePostRes>> getPostPaginationByHashtag(@RequestParam(name = "cursor", required = false) Long cursor,
                                                                                @RequestParam(name = "hashtag", required = false) String hashtag,
        @ParameterObject @PageableDefault(size = 3, sort = "createdAt") Pageable pageable) {
        return ApplicationResponse.ok(postService.getPostPaginationByHashtag(cursor, hashtag, pageable, mockUserIdProvide()));
    }

    @GetMapping("/search-suggestions")
    @Operation(summary = "게시판 검색어 자동완성", description = "게시판 검색어 자동완성을 제공합니다.")
    public ApplicationResponse<List<String>> getSearchSuggestions(@RequestParam("keyword") String keyword) {
        Long userId = mockUserIdProvide();
        return ApplicationResponse.ok(postSearchService.getAutoCompleteSuggestions(userId, keyword));
    }

    @GetMapping("/me")
    @Operation(summary = "내가 작성한 게시글 페이징 조회 API", description = "내가 작성한 게시글을 커서 기반 페이징으로 조회합니다.")
    public ApplicationResponse<Slice<SimplePostRes>> getMyPostPagination(
        @RequestParam(required = false) LocalDateTime cursor,
        @RequestParam(defaultValue = "30") int size,
        @LoginUserId Long userId) {
        return ApplicationResponse.ok(postService.getMyPostPagination(userId, cursor, size));
    }


    public Long mockUserIdProvide(){
        return 1L;
    }


}