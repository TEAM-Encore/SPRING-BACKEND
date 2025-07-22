package encore.server.domain.user.dto.profile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoProfileResponse {

  private Long id;
  private String connected_at;
  private KakaoAccount kakao_account;
  private KakaoProperties properties;

  @Data
  @NoArgsConstructor
  public static class KakaoProperties {
    private String nickname;
  }

  @Data
  @NoArgsConstructor
  public static class KakaoAccount {
    private String email;
  }
}
