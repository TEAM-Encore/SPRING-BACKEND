package encore.server.domain.user.converter;

import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.enumerate.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User toEntity(UserSignupReq userSignupReq, String encodedPassword, String uniqueNickName) {

        return User.builder()
                .email(userSignupReq.email())
                .password(encodedPassword)
                .name(userSignupReq.name())
                .nickName(uniqueNickName)
                .authProvider(userSignupReq.provider())
                .role(UserRole.BASIC)
                .viewingFrequency(null)
                .point(0L)
                .profileImageUrl(null)
                .build();
    }
}
