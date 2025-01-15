package encore.server.domain.user.converter;

import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.entity.User;
import java.util.List;
import java.util.Objects;

public class UserConverter {

  public static User toEntity(UserSignupReq userSignupReq, String encodedPassword, String uniqueNickName) {

    return User.builder()
        .email(userSignupReq.email())
        .password(encodedPassword)
        .name(userSignupReq.name())
        .nickName(uniqueNickName)
        .authProvider(userSignupReq.provider())
        .role(userSignupReq.role())
        .viewingFrequency(null)
        .point(0L)
        .profileImageUrl(null)
        .build();
  }

  public static UserGetMeRes toUserGetMeRes(User user, List<String> userPreferredKeywords, int numOfWritePost) {

    String viewingFrequency = "";
    if (!Objects.isNull(user.getViewingFrequency())) {
      viewingFrequency = user.getViewingFrequency().getText();
    }

    return UserGetMeRes.builder()
        .point(user.getPoint())
        .nickname(user.getNickName())
        .numOfSubscriber(0)
        .numOfWritePost(numOfWritePost)
        .preferredKeywords(userPreferredKeywords)
        .viewingFrequency(viewingFrequency)
        .email(user.getEmail())
        .build();
  }
}
