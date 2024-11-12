package encore.server.domain.post.controller;

import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.dto.request.PostUpdateReq;
import encore.server.domain.post.dto.response.PostDetailsGetRes;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.service.PostService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.BadRequestException;
import encore.server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "Post", description = "게시글 API")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse<Long> createPost(@RequestBody @Valid PostCreateReq postCreateReq, BindingResult bindingResult) {

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
        return new ApplicationResponse(LocalDateTime.now(), ErrorCode.SUCCESS.getCode(), "Post created successfully", postId);
    }

    @PutMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<Long> updatePost(@PathVariable("post_id") Long postIdToUpdate, @RequestBody @Valid PostUpdateReq postUpdateReq, BindingResult bindingResult){

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

        return new ApplicationResponse<>(LocalDateTime.now(), ErrorCode.SUCCESS.getCode(), "Post updated successfully", postId);
    }


    @DeleteMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse deletePost(@PathVariable("post_id") Long postIdToDelete){

        log.info("[POST]-[PostController]-[postDelete] post delete API call");

        postService.deletePost(postIdToDelete);

        log.info("[POST]-[PostController]-[postDelete] post delete API terminated successfully");

        return ApplicationResponse.ok();
    }

    /*
    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<List<Post>> getAllPosts(){
        log.info("[POST]-[PostController]-[getAllPosts] /post/posts API call");


    }

     */

    @GetMapping("/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationResponse<PostDetailsGetRes> getPostDetails(@PathVariable("post_id") Long postId){

        log.info("[POST]-[PostController]-[getPostDetails] /post/{post_id} API call");

        PostDetailsGetRes postDetailsGetRes = postService.getPostDetails(postId);

        return ApplicationResponse.ok(postDetailsGetRes);

    }


    public Long mockUserIdProvide(){
        return 1L;
    }


}
