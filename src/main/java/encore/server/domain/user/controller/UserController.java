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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mvp/users")
@Tag(name = "User (MVP)", description = "사용자 API")
public class UserController {

    private final UserAuthService userAuthService;
    private final UserSetupService userSetupService;
    private final UserInfoService userInfoService;
    private final UserHashtagService userHashtagService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(
        summary = "(MVP) 회원가입",
        description = """
        주어진 정보로 새로운 사용자를 등록합니다.
        - OAuth 로그인시 사용됩니다.
        - Test 용도로 사용 가능합니다.
        """
    )
    public ApplicationResponse<UserSignupRes> signup(@RequestBody @Valid UserSignupReq userSignupReq) {
        return ApplicationResponse.ok(userAuthService.signup(userSignupReq));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(
        summary = "(MVP) 로그인",
        description = """
        주어진 이메일과 비밀번호로 로그인합니다.
        - OAuth 로그인시 사용됩니다.
        - Test 용도로 사용 가능합니다.
        - 로그인 성공 시 JWT 액세스 토큰을 반환합니다.
        - 토큰은 이후 인증이 필요한 요청의 `Authorization` 헤더에 사용됩니다.
        """
    )
    public ApplicationResponse<UserLoginRes> login(@RequestBody @Valid UserLoginReq userLoginReq) {
        return ApplicationResponse.ok(userAuthService.login(userLoginReq));
    }

    @GetMapping("/nickname-validation")
    @Operation(
        summary = "(MVP) 유저 닉네임 검증",
        description = """
        주어진 닉네임이 사용 가능한지 검증합니다.
        - 중복된 닉네임이 있거나, 제약 조건을 위반할 경우 사용 불가로 표시됩니다.
        
         **검증 실패 시 발생 예외**
            - `USER_NICKNAME_INVALID_FORMAT_EXCEPTION`: 형식이 올바르지 않음 \s
            - `USER_NICKNAME_CONTAINS_WHITESPACE_EXCEPTION`: 공백 포함 \s
            - `USER_NICKNAME_TOO_SHORT_EXCEPTION`: 3자 미만 \s
            - `USER_NICKNAME_TOO_LONG_EXCEPTION`: 6자 초과 \s
            - `USER_NICKNAME_ALREADY_EXIST_EXCEPTION`: 중복 닉네임\s
        """
    )
    public ApplicationResponse<UserNicknameValidationRes> validateUserNickname(@RequestParam String nickname) {
        return ApplicationResponse.ok(userSetupService.validateUserNickname(nickname));
    }

    @PatchMapping("/me")
    @Operation(
        summary = "(MVP) 유저 정보 변경",
        description = """
        로그인된 사용자의 정보를 수정합니다.
        - 전달된 필드만 부분 업데이트가 가능합니다.
        """
    )
    public ApplicationResponse<Void> patchUserInfo(
        @RequestBody @Valid UserPatchReq userPatchReq,
        @Parameter(hidden = true) @LoginUserId Long loginUserId
    ){
        userSetupService.patchUserInfo(userPatchReq, loginUserId);
        return ApplicationResponse.ok();
    }

    @GetMapping("/me")
    @Operation(
        summary = "(MVP) 내 정보 조회",
        description = """
        현재 로그인된 사용자의 정보를 조회합니다.
        - MVP 에서 사용되지 않는 정보도 조회될 수 있습니다.
        """
    )
    public ApplicationResponse<UserGetMeRes> getMyInfo(
        @Parameter(hidden = true) @LoginUserId Long loginUserId) {
        return ApplicationResponse.ok(userInfoService.getMyInfo(loginUserId));
    }

    @DeleteMapping("/me")
    @Operation(
        summary = "(MVP) 회원 탈퇴",
        description = """
        현재 로그인된 회원에 대한 탈퇴를 진행합니다.
        """
    )
    public ApplicationResponse<Void> deleteMyAccount(
        @Parameter(hidden = true) @LoginUserId Long loginUserId
    ) {
        userInfoService.deleteMyAccount(loginUserId);
        return ApplicationResponse.ok();
    }

    @Hidden
    @GetMapping("/{userId}")
    @Operation(summary = "(MVP) 유저 정보 조회", description = "주어진 아이디로 유저 정보를 조회합니다.")
    public ApplicationResponse<UserGetRes> getUserInfo(@PathVariable @Valid Long userId) {
        return ApplicationResponse.ok(userInfoService.getUserInfo(userId));
    }

    @Hidden
    @PostMapping("/penalties")
    @Operation(summary = "유저 계정 정지", description = "설정한 값 만큼 계정 정지를 진행합니다.")
    public ApplicationResponse<Void> grantPenalty(@RequestBody @Valid UserImposePenaltyReq request) {
        userAuthService.imposePenalty(request);
        return ApplicationResponse.ok();
    }

    @Hidden
    @PostMapping("/hashtag/{name}")
    public ApplicationResponse<UserHashtagRes> addHashtag(
            @PathVariable("name") String name
    ){
        Long userId = 1L;
        return ApplicationResponse.ok(userHashtagService.addHashtag(userId, name));
    }

    @Hidden
    @DeleteMapping("/hashtag/{id}")
    public ApplicationResponse<Void> deleteHashtag(
            @PathVariable("id") Long id
    ){
        Long userId = 1L;
        userHashtagService.deleteHashtag(userId, id);
        return ApplicationResponse.ok();
    }

    @Hidden
    @GetMapping("/hashtags")
    public ApplicationResponse<List<UserHashtagRes>> getHashtags(){
        Long userId = 1L;
        return ApplicationResponse.ok(userHashtagService.getHashtags(userId));
    }
}
