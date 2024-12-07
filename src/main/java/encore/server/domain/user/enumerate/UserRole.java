package encore.server.domain.user.enumerate;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    BASIC("일반사용자"),
    MANAGER("운영자");

    private final String roleText;
}
