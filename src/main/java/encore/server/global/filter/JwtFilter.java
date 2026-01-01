package encore.server.global.filter;

import encore.server.global.auth.UserDetailsImpl;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import encore.server.global.exception.FilterExceptionHandler;
import encore.server.global.util.JwtUtils;
import io.jsonwebtoken.Claims;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final List<String> publicURLs = List.of("/api/mvp/users/signup",
      "/api/mvp/users/login");
  private final List<String> publicURLPrefixes = List.of(
      "/swagger-ui",
      "/v3/api-docs",
      "/api/v1/oauth2"
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();

    if (publicURLs.stream()
        .anyMatch(f -> f.equals(uri))) {
      filterChain.doFilter(request, response);
      return;
    }

    if (publicURLPrefixes.stream()
        .anyMatch(f -> uri.startsWith(f))
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String tokenValue = jwtUtils.getTokenFromRequest(request);

    try {
      jwtUtils.validateToken(tokenValue);
    } catch (ApplicationException e) {
      FilterExceptionHandler.handle(e, response);
      return;
    }

    log.info("tokenValue: " + tokenValue);

    Claims claim = jwtUtils.getUserInfoFromToken(tokenValue);
    setUserIdToSecurityContextHolder(Long.parseLong(String.valueOf(claim.get(jwtUtils.ID_KEY))),
        (String) claim.get(jwtUtils.EMAIL_KEY));

    filterChain.doFilter(request, response);
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
