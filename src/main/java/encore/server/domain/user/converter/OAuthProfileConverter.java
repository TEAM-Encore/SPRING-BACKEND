package encore.server.domain.user.converter;

import encore.server.domain.user.dto.profile.GoogleUserProfile;
import encore.server.domain.user.dto.request.UserLoginReq;
import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.profile.KakaoProfileResponse;
import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.enumerate.UserRole;

public class OAuthProfileConverter {

  public static UserLoginReq profileToLoginReq(KakaoProfileResponse profile) {
    return UserLoginReq.builder()
        .email(profile.getKakao_account().getEmail())
        .build();
  }

  public static UserLoginReq profileToLoginReq(GoogleUserProfile profile) {
    return UserLoginReq.builder()
        .email(profile.getEmail())
        .build();
  }

  public static UserSignupReq profileToSignupReq(KakaoProfileResponse profile) {
    return UserSignupReq.builder()
        .email(profile.getKakao_account().getEmail())
        .provider(AuthProvider.KAKAO)
        .role(UserRole.BASIC)
        .build();
  }

  public static UserSignupReq profileToSignupReq(GoogleUserProfile profile) {
    return UserSignupReq.builder()
        .email(profile.getEmail())
        .provider(AuthProvider.GOOGLE)
        .role(UserRole.BASIC)
        .build();
  }
}
