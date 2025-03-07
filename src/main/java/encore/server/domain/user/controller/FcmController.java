package encore.server.domain.user.controller;

import encore.server.domain.user.service.UserFcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/fcm")
@Tag(name = "FCM", description = "FCM 알람 관련 API")
public class FcmController {

    private final UserFcmService userFcmService;

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    @GetMapping(value = "/{user_id}",headers = "FCM-TOKEN")
    public void pushAlarmMyDevice(@PathVariable("user_id") Long userId, @RequestHeader("FCM-TOKEN") String token){
        userFcmService.addFCMToken(userId,token);
    }
}
