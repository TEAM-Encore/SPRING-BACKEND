package encore.server.global.util;

import encore.server.domain.user.enumerate.UserRole;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
public class JwtUtils {

  @Value("${jwt.header}")
  public String AUTHORIZATION_HEADER;

  @Value("${jwt.key}")
  public String AUTHORIZATION_KEY;

  @Value("${jwt.id-key}")
  public String ID_KEY;

  @Value("${jwt.email-key}")
  public String EMAIL_KEY;

  @Value("${jwt.bearer-prefix}")
  public String BEARER_PREFIX;

  @Value("${jwt.token-time}")
  public long TOKEN_TIME;


  @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
  private String secretKey;
  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


  @PostConstruct
  public void init() {
    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  // 토큰 생성

  public String createToken(String email, Long userId, UserRole role) {
    Date date = new Date();

    return BEARER_PREFIX +
        Jwts.builder()
            .setSubject(email) // 사용자 식별자값(ID)
            .claim(AUTHORIZATION_KEY, role) // 사용자 권한
            .claim(ID_KEY, userId)
            .claim(EMAIL_KEY, email)
            .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
            .setIssuedAt(date) // 발급일
            .signWith(key, signatureAlgorithm) // 암호화 알고리즘
            .compact();
  }


  // JWT Cookie 에 저장
  public void addJwtToCookie(String token, HttpServletResponse res) {
    try {
      token = URLEncoder.encode(token, "utf-8")
          .replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
      cookie.setPath("/");

      // Response 객체에 Cookie 추가
      res.addCookie(cookie);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }
  }

  // JWT 토큰 substring
  public String substringToken(String tokenValue) {
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
      return tokenValue.substring(7);
    }
    log.error("Not Found Token");
    throw new ApplicationException(ErrorCode.JWT_NOT_FOUND);
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException | MalformedJwtException |
             io.jsonwebtoken.security.SignatureException e) {
      throw new ApplicationException(ErrorCode.INVALID_JWT_SIGNATURE);
    } catch (ExpiredJwtException e) {
      throw new ApplicationException(ErrorCode.EXPIRED_JWT_TOKEN);
    } catch (UnsupportedJwtException e) {
      throw new ApplicationException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
    } catch (IllegalArgumentException e) {
      throw new ApplicationException(ErrorCode.JWT_CLAIMS_IS_EMPTY);
    }
  }

  public String getTokenFromRequest(HttpServletRequest request) {

    String header = request.getHeader(AUTHORIZATION_HEADER);

    return substringToken(header);

  }

  // 토큰에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
