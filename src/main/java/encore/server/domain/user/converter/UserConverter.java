package encore.server.domain.user.converter;

import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.dto.response.UserGetRes;
import encore.server.domain.user.entity.User;
import java.util.List;
import java.util.Objects;

public class UserConverter {

  public static User toEntity(UserSignupReq userSignupReq, String encodedPassword,
      String uniqueNickName) {

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

  public static UserGetMeRes toUserGetMeRes(User user, List<String> userPreferredKeywords,
      long numOfWritePost, long numOfSubscriber) {

    String viewingFrequency = "";
    if (!Objects.isNull(user.getViewingFrequency())) {
      viewingFrequency = user.getViewingFrequency().getText();
    }

    return UserGetMeRes.builder()
        .point(user.getPoint())
        .nickname(user.getNickName())
        .numOfSubscriber(numOfSubscriber)
        .numOfWritePost(numOfWritePost)
        .preferredKeywords(userPreferredKeywords)
        .viewingFrequency(viewingFrequency)
        .email(user.getEmail())
        .build();
  }

  public static UserGetRes toUserGetRes(User user, List<String> userPreferredKeywords,
      long numOfWritePost, long numOfSubscriber) {
    String viewingFrequency = "";
    if (!Objects.isNull(user.getViewingFrequency())) {
      viewingFrequency = user.getViewingFrequency().getText();
    }

    return UserGetRes.builder()
        .userId(user.getId())
        .nickname(user.getNickName())
        .numOfSubscriber(numOfSubscriber)
        .numOfWritePost(numOfWritePost)
        .preferredKeywords(userPreferredKeywords)
        .viewingFrequency(viewingFrequency)
        .build();
  }
}
