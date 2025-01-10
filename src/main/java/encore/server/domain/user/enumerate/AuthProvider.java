package encore.server.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    KAKAO("KAKAO"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    private final String providerText;

}
