package encore.server.domain.user.service;

import encore.server.domain.image.service.ImageService;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.subscription.service.SubscriptionService;
import encore.server.domain.user.converter.UserConverter;
import encore.server.domain.user.dto.response.UserGetMeRes;
import encore.server.domain.user.dto.response.UserGetRes;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.domain.user.repository.WithdrawalQuerydslRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
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
  private final ImageService imageService;
  private final WithdrawalQuerydslRepository withdrawalQuerydslRepository;
  private final EntityManager em;

  public UserGetMeRes getMyInfo(Long userId) {
    User me = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    long numOfWritePost = postRepository.countByUserAndDeletedAtIsNull(me);
    String profileImageDownLoadPresignedUrl = imageService.generateGetPresignedUrl(me.getProfileImageUrl());
    return UserConverter.toUserGetMeRes(me, numOfWritePost, profileImageDownLoadPresignedUrl);
  }

  public UserGetRes getUserInfo(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    long numOfWritePost = postRepository.countByUserAndDeletedAtIsNull(user);

    return UserConverter.toUserGetRes(user, numOfWritePost);
  }

  @Transactional
  public void deleteMyAccount(Long loginUserId) {
    LocalDateTime now = LocalDateTime.now();

    withdrawalQuerydslRepository.withdrawBoard(loginUserId, now);
    withdrawalQuerydslRepository.withdrawReviewTicket(loginUserId, now);
    withdrawalQuerydslRepository.withdrawMisc(loginUserId, now);
    withdrawalQuerydslRepository.anonymizeAndDeleteUser(loginUserId, now);
    em.clear();
  }

  @Transactional
  public void setupComplete(Long loginUserId) {
    User me = userRepository.findById(loginUserId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
    me.setupSuccess();
  }
}
