package encore.server.domain.user.controller;

import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserPatchReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.dto.response.UserNicknameValidationRes;
import encore.server.domain.user.dto.response.UserSignupRes;
import encore.server.domain.user.service.UserAuthService;
import encore.server.domain.user.service.UserSetupService;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserAuthService userAuthService;
    private final UserSetupService userSetupService;
    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "주어진 정보로 회원가입을 진행합니다.")
    public ApplicationResponse<UserSignupRes> signup(@RequestBody @Valid UserSignupReq userSignupReq) {
        return ApplicationResponse.ok(userAuthService.signup(userSignupReq));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "주어진 정보로 로그인을 진행합니다.")
    public ApplicationResponse<UserLoginRes> login(@RequestBody @Valid UserLoginReq userLoginReq) {
        return ApplicationResponse.ok(userAuthService.login(userLoginReq));
    }

    @GetMapping("/nickname-validation/{nickname}")
    @Operation(summary = "유저 닉네임 검증", description = "주어진 파라미터로 중복확인과 제약조건 검증을 진행합니다.")
    public ApplicationResponse<UserNicknameValidationRes> validateUserNickname(@PathVariable String nickname) {
        return ApplicationResponse.ok(userSetupService.validateUserNickname(nickname));
    }

    @PatchMapping("")
    @Operation(summary = "유저 정보 변경", description = "주어진 정보로 현재 로그인한 유저 정보를 변경합니다. 정보를 받은 요소만 변경합니다.")
    public ApplicationResponse<Void> patchUserInfo(@RequestBody @Valid UserPatchReq userPatchReq){
        Long userId = 1L;
        userSetupService.patchUserInfo(userPatchReq, userId);
        return ApplicationResponse.ok();
    }

}
