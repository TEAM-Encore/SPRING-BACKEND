package encore.server.global.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserLoginRes;
import encore.server.domain.user.service.UserAuthService;
import encore.server.global.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserAuthService userAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        //OAuth2UserService 에서 반환한 OAuth2User 의 Attributes
        Map<String, Object> oauthLoginUserInfoAttributes = ((OAuth2User) authentication.getPrincipal()).getAttributes();

        //이미 회원가입이 되어 있는지 확인
        try {
            userAuthService.signup(UserSignupReq.fromOauthAttributes(oauthLoginUserInfoAttributes));
        } catch (ApplicationException ignored){
            log.info("[OAuth2SuccessHandler]-[onAuthenticationSuccess] Already Sign upped User. Try Login");
        }

        //이후 login 시도 및 결과 반환
        UserLoginRes loginRes = userAuthService.login(UserLoginReq.fromOauthAttributes(oauthLoginUserInfoAttributes));

        //HttpResponse Header Mapping
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json; charset=UTF-8");

        //write response
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(loginRes));
    }


}
