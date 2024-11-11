package encore.server.domain.post.controller;

import encore.server.domain.post.converter.PostConverter;
import encore.server.domain.post.dto.request.PostCreateReq;
import encore.server.domain.post.entity.Post;
import encore.server.domain.post.service.PostService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.BadRequestException;
import encore.server.global.exception.ErrorCode;
import encore.server.global.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
@Tag(name = "Post", description = "게시글 API")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse<Long> post(@RequestBody @Valid PostCreateReq postCreateReq, BindingResult bindingResult) {

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

        //비즈니스 로직
        Long postId = postService.createPost(postCreateReq, mockUserIdProvide());

        log.info("[POST]-[PostController]-[post] post API terminated successfully");

        //Response Create
        return new ApplicationResponse(LocalDateTime.now(), ErrorCode.SUCCESS.getCode(), "Post created successfully", postId);
    }

    public Long mockUserIdProvide(){
        return 1L;
    }


}
