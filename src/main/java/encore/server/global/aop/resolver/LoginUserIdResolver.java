package encore.server.global.aop.resolver;


import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.auth.UserDetailsImpl;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 컨트롤러 메서드에서 @LoginUserId 어노테이션이 붙은 파라미터에
 * 현재 로그인된 사용자의 userId(Long)를 주입하는 리졸버입니다.
 */
@Component
@Slf4j
public class LoginUserIdResolver implements HandlerMethodArgumentResolver {

  /**
   * 해당 파라미터를 리졸버가 지원하는지 여부를 반환합니다.
   * @param parameter 메서드 파라미터 정보
   * @return true일 경우 resolveArgument 실행
   */
  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    // @LoginUserId 애너테이션이 붙어 있고, 타입이 Long일 때만 처리
    return parameter.hasParameterAnnotation(LoginUserId.class)
        && parameter.getParameterType().equals(Long.class);
  }

  /**
   * 실제 파라미터 값을 리졸빙해서 반환합니다.
   * 현재 SecurityContextHolder에 저장된 인증 정보를 가져와서 userId 반환
   * @return 로그인된 사용자의 userId
   */
  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    // SecurityContextHolder에서 인증 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 인증 정보가 없거나 principal이 UserDetailsImpl 타입이 아닌 경우 예외 처리
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
      throw new ApplicationException(ErrorCode.UNAUTHORIZED_EXCEPTION);
    }

    // userId 반환 (컨트롤러 메서드 파라미터에 주입됨)
    return userDetails.getUserId();
  }
}
