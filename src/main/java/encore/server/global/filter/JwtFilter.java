package encore.server.global.filter;

import encore.server.global.auth.UserDetailsImpl;
import encore.server.global.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final List<String> publicURLs = List.of("/api/v1/users/signup", "/api/v1/users/login");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();
    log.info(uri);
  /*
    if (publicURLs.stream()
        .anyMatch(f -> f.equals(uri))) {
      filterChain.doFilter(request, response);
      return;
    }

  */
    //이후 삭제
    if (true) {
      setUserIdToSecurityContextHolder(1L, "encore@naver.com");
      filterChain.doFilter(request, response);
      return;
    }
  /*
    String tokenValue = jwtUtils.getTokenFromRequest(request);

    if (!(StringUtils.hasText(tokenValue) && jwtUtils.validateToken(tokenValue))) {
      throw new ApplicationException(ErrorCode.FORBIDDEN_EXCEPTION);
    }

    Claims claim = jwtUtils.getUserInfoFromToken(tokenValue);

    setUserIdToSecurityContextHolder((Long) claim.get(jwtUtils.ID_KEY),
        (String) claim.get(JwtUtil.EMAIL_KEY));

    filterChain.doFilter(request, response);

   */
  }

  public void setUserIdToSecurityContextHolder(Long userId, String email) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    UserDetailsImpl userDetails = new UserDetailsImpl(userId, email);
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities()));
    SecurityContextHolder.setContext(context);
  }
}
