package encore.server.domain.user.converter;

import encore.server.domain.user.dto.request.UserSignupReq;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.dto.response.UserGetRes;
import encore.server.domain.user.entity.User;
import java.util.List;
import java.util.Objects;

public class UserConverter {

  public static User toEntity(UserSignupReq userSignupReq, String uniqueNickName) {
    return User.builder()
        .email(userSignupReq.email())
        .nickName(uniqueNickName)
        .authProvider(userSignupReq.provider())
        .role(userSignupReq.role())
        .point(0L)
        .profileImageUrl(null)
        .build();
  }

  public static UserGetMeRes toUserGetMeRes(User user, long numOfWritePost, String profileImageDownLoadPresignedUrl) {
    return UserGetMeRes.builder()
        .point(user.getPoint())
        .nickname(user.getNickName())
        .numOfWritePost(numOfWritePost)
        .email(user.getEmail())
        .profileImageUrl(profileImageDownLoadPresignedUrl)
        .build();
  }

  public static UserGetRes toUserGetRes(User user, long numOfWritePost) {
    return UserGetRes.builder()
        .userId(user.getId())
        .nickname(user.getNickName())
        .numOfWritePost(numOfWritePost)
        .build();
  }
}
