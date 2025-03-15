package encore.server.domain.subscription.service;

import encore.server.domain.subscription.converter.SubscribeConverter;
import encore.server.domain.subscription.dto.response.SubscriptionGetResponse;
import encore.server.domain.subscription.entity.Subscription;
import encore.server.domain.subscription.repository.SubscriptionRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final EntityManager entityManager;

  @Transactional
  @CacheEvict(value = "subscriberCount", key = "#followingUserId")
  public void subscribe(Long followerUserId, Long followingUserId) {
    // validation: 구독 대상 유저가 존재하는지 검증
    if (!userRepository.existsById(followingUserId)) {
      throw new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    // 프록시 객체로 팔로워와 팔로잉 유저 엔티티 조회 (지연 로딩)
    User follower = entityManager.getReference(User.class, followerUserId);
    User following = entityManager.getReference(User.class, followingUserId);

    // 이미 구독하고 있는지 중복 검증
    if (subscriptionRepository.existsByFollowerAndFollowing(follower, following)) {
      throw new ApplicationException(ErrorCode.ALREADY_SUBSCRIBE_EXCEPTION);
    }

    //business logic
    // 구독 관계 생성 및 저장
    Subscription subscription = Subscription.create(follower, following);
    subscriptionRepository.save(subscription);
  }

  @Transactional
  @CacheEvict(value = "subscriberCount", key = "#followingUserId")
  public void unsubscribe(Long followerUserId, Long followingUserId) {
    // validation: 구독 취소 대상 유저가 존재하는지 검증
    if (!userRepository.existsById(followingUserId)) {
      throw new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    // 프록시 객체로 팔로워와 팔로잉 유저 엔티티 조회 (지연 로딩)
    User follower = entityManager.getReference(User.class, followerUserId);
    User following = entityManager.getReference(User.class, followingUserId);

    // 구독한 상태인지 검증 후 구독 엔티티 조회
    Subscription subscription = subscriptionRepository.findByFollowerAndFollowing(follower,
            following)
        .orElseThrow(() -> new ApplicationException(ErrorCode.ALREADY_UNSUBSCRIBE_EXCEPTION));

    //business logic
    // 구독 관계 삭제
    subscriptionRepository.delete(subscription);
  }

  public Slice<SubscriptionGetResponse> getFollowing(Long loginUserId, LocalDateTime cursor,
      int size) {

    // 1. 페이징 및 정렬 정보 설정 (최신순 정렬)
    Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());

    // 2. userId에 해당하는 유저 조회 (없으면 예외 발생)
    User user = userRepository.findById(loginUserId)
        .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

    // 3. cursor가 null이면 최신 글부터 조회, 아니면 cursor 기준 이전 글 조회
    List<Subscription> subscriptions;
    if (cursor == null) {
      subscriptions = subscriptionRepository.findByFollowerOrderByCreatedAtDesc(user, pageable);
    } else {
      subscriptions = subscriptionRepository.findByFollowerAndCreatedAtBeforeOrderByCreatedAtDesc(
          user, cursor, pageable);
    }

    boolean hasNext = subscriptions.size() > pageable.getPageSize();

    List<SubscriptionGetResponse> response = subscriptions.stream()
        .limit(pageable.getPageSize())
        .map(SubscribeConverter::toSubscriptionGetResponse)
        .toList();

    return new SliceImpl<>(response, pageable, hasNext);
  }

  public Boolean isFollowing(Long loginUserId, Long targetUserId) {
    // validation: 대상 유저가 존재하는지 검증
    if (!userRepository.existsById(targetUserId)) {
      throw new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }

    // 프록시 객체로 팔로워와 팔로잉 유저 엔티티 조회
    User follower = entityManager.getReference(User.class, loginUserId);
    User following = entityManager.getReference(User.class, targetUserId);

    //business logic
    // 구독 여부 확인 후 결과 반환
    return subscriptionRepository.existsByFollowerAndFollowing(follower, following);
  }

  @Cacheable(value = "subscriberCount", key = "#userId")
  public long getSubscriberCount(Long userId) {
    // 해당 유저를 구독하고 있는 사용자 수 반환
    return subscriptionRepository.countByFollowingId(userId);
  }
}
