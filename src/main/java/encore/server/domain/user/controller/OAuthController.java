package encore.server.domain.user.controller;

import encore.server.domain.user.dto.response.OAuthURLRes;
import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.service.oauth.OAuthLoginService;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {

  private final Map<AuthProvider, OAuthLoginService> loginServices;

  @GetMapping("/{provider}/login")
  public ResponseEntity<OAuthURLRes> getLoginUrl(@PathVariable AuthProvider provider) {
    OAuthURLRes response = loginServices.get(provider).getLoginUrl();
    return ResponseEntity.ok(response);
  }

  @RequestMapping(value = "/{provider}/callback", method = {RequestMethod.GET, RequestMethod.POST})
  public ResponseEntity<Void> loginCallback(@PathVariable AuthProvider provider,
      @RequestParam String code) {
    String redirectUrl = loginServices.get(provider).loginWithCode(code);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
  }

}
