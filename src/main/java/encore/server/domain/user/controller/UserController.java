package encore.server.domain.user.controller;

import encore.server.domain.user.dto.request.UserImposePenaltyReq;
import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserPatchReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.*;
import encore.server.domain.user.service.UserAuthService;
import encore.server.domain.user.service.UserHashtagService;
import encore.server.domain.user.service.UserInfoService;
import encore.server.domain.user.service.UserSetupService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserAuthService userAuthService;
    private final UserSetupService userSetupService;
    private final UserInfoService userInfoService;
    private final UserHashtagService userHashtagService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "(MVP) 회원가입", description = "주어진 정보로 회원가입을 진행합니다.")
    public ApplicationResponse<UserSignupRes> signup(@RequestBody @Valid UserSignupReq userSignupReq) {
        return ApplicationResponse.ok(userAuthService.signup(userSignupReq));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "(MVP) 로그인", description = "주어진 정보로 로그인을 진행합니다.")
    public ApplicationResponse<UserLoginRes> login(@RequestBody @Valid UserLoginReq userLoginReq) {
        return ApplicationResponse.ok(userAuthService.login(userLoginReq));
    }

    @GetMapping("/nickname-validation/{nickname}")
    @Operation(summary = "(MVP) 유저 닉네임 검증", description = "주어진 파라미터로 중복확인과 제약조건 검증을 진행합니다.")
    public ApplicationResponse<UserNicknameValidationRes> validateUserNickname(@PathVariable String nickname) {
        return ApplicationResponse.ok(userSetupService.validateUserNickname(nickname));
    }

    @PatchMapping("/me")
    @Operation(summary = "(MVP) 유저 정보 변경", description = "주어진 정보로 현재 로그인한 유저 정보를 변경합니다. 정보를 받은 요소만 변경합니다.")
    public ApplicationResponse<Void> patchUserInfo(
        @RequestBody @Valid UserPatchReq userPatchReq,
        @LoginUserId Long loginUserId
    ){
        userSetupService.patchUserInfo(userPatchReq, loginUserId);
        return ApplicationResponse.ok();
    }

    @GetMapping("/me")
    @Operation(summary = "(MVP) 내 정보 조회", description = "현재 로그인한 유저 정보를 조회합니다.")
    public ApplicationResponse<UserGetMeRes> getMyInfo(@LoginUserId Long loginUserId) {
        return ApplicationResponse.ok(userInfoService.getMyInfo(loginUserId));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "(MVP) 유저 정보 조회", description = "주어진 아이디로 유저 정보를 조회합니다.")
    public ApplicationResponse<UserGetRes> getUserInfo(@PathVariable @Valid Long userId) {
        return ApplicationResponse.ok(userInfoService.getUserInfo(userId));
    }

    @PostMapping("/penalties")
    @Operation(summary = "유저 계정 정지", description = "설정한 값 만큼 계정 정지를 진행합니다.")
    public ApplicationResponse<Void> grantPenalty(@RequestBody @Valid UserImposePenaltyReq request) {
        userAuthService.imposePenalty(request);
        return ApplicationResponse.ok();
    }

    @PostMapping("/hashtag/{name}")
    public ApplicationResponse<UserHashtagRes> addHashtag(
            @PathVariable("name") String name
    ){
        Long userId = 1L;
        return ApplicationResponse.ok(userHashtagService.addHashtag(userId, name));
    }

    @DeleteMapping("/hashtag/{id}")
    public ApplicationResponse<Void> deleteHashtag(
            @PathVariable("id") Long id
    ){
        Long userId = 1L;
        userHashtagService.deleteHashtag(userId, id);
        return ApplicationResponse.ok();
    }

    @GetMapping("/hashtags")
    public ApplicationResponse<List<UserHashtagRes>> getHashtags(){
        Long userId = 1L;
        return ApplicationResponse.ok(userHashtagService.getHashtags(userId));
    }
}
