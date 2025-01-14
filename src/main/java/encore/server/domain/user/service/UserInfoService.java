package encore.server.domain.user.service;

import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.converter.UserConverter;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  
  /*
      TODO: 구독 기능 개발 후 구독자 조회 추가
   */
  public UserGetMeRes getMyInfo(Long userId) {
    User me = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    int numOfWritePost = postRepository.countByUserAndDeletedAtIsNull(me);

    List<String> userPreferredKeywords = me.getUserPreferredKeywords().stream()
        .map(upk -> upk.getPreferredKeyword().getPreferredKeywordEnum().getKeywordText())
        .toList();

    return UserConverter.toUserGetMeRes(me, userPreferredKeywords, numOfWritePost);
  }
}
