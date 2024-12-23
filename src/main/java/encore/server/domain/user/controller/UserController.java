package encore.server.domain.user.controller;

import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.dto.response.UserSignupRes;
import encore.server.domain.user.service.UserService;
import encore.server.global.common.ApplicationResponse;
import encore.server.global.exception.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "주어진 정보로 회원가입을 진행합니다.")
    public ApplicationResponse<UserSignupRes> signup(@RequestBody @Valid UserSignupReq userSignupReq) {
        return ApplicationResponse.ok(userService.signup(userSignupReq));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "주어진 정보로 로그인을 진행합니다.")
    public ApplicationResponse<UserLoginRes> login(@RequestBody @Valid UserLoginReq userLoginReq) {
        return ApplicationResponse.ok(userService.login(userLoginReq));
    }

}
