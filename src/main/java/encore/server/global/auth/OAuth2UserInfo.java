package encore.server.global.auth;

import encore.server.domain.user.enumerate.AuthProvider;
import encore.server.domain.user.enumerate.UserRole;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Builder
@Getter
public class OAuth2UserInfo implements OAuth2User {

    private Long userId;
    private String email;
    private String name;
    private AuthProvider provider;
    private UserRole role;

    public static final String ID_KEY = "id";
    public static final String EMAIL_KEY = "email";
    public static final String NAME_KEY = "name";
    public static final String PROVIDER_KEY = "provider";
    public static final String ROLE_KEY = "role";


    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(ID_KEY, userId, EMAIL_KEY, email, NAME_KEY, name, ROLE_KEY, role, PROVIDER_KEY, provider);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = UserRole.BASIC.name();

        if (!Objects.isNull(role)) {
           authority = role.name();
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }
}
