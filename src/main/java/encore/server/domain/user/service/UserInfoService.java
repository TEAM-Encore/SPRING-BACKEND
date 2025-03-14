package encore.server.domain.user.service;

import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.subscription.service.SubscriptionService;
import encore.server.domain.user.converter.UserConverter;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.dto.response.UserGetRes;
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
  private final SubscriptionService subscriptionService;

  public UserGetMeRes getMyInfo(Long userId) {
    User me = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    long numOfWritePost = postRepository.countByUserAndDeletedAtIsNull(me);
    long numOfSubscriber = subscriptionService.getSubscriberCount(userId);

    List<String> userPreferredKeywords = me.getUserPreferredKeywords().stream()
        .map(upk -> upk.getPreferredKeyword().getPreferredKeywordEnum().getKeywordText())
        .toList();

    return UserConverter.toUserGetMeRes(me, userPreferredKeywords, numOfWritePost, numOfSubscriber);
  }

  public UserGetRes getUserInfo(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    long numOfWritePost = postRepository.countByUserAndDeletedAtIsNull(user);
    long numOfSubscriber = subscriptionService.getSubscriberCount(userId);

    List<String> userPreferredKeywords = user.getUserPreferredKeywords().stream()
        .map(upk -> upk.getPreferredKeyword().getPreferredKeywordEnum().getKeywordText())
        .toList();

    return UserConverter.toUserGetRes(user, userPreferredKeywords, numOfWritePost, numOfSubscriber);
  }

}
