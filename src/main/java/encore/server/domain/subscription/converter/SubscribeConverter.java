package encore.server.domain.subscription.converter;

import encore.server.domain.subscription.dto.response.SubscriptionGetResponse;
import encore.server.domain.subscription.entity.Subscription;

public class SubscribeConverter {

  public static SubscriptionGetResponse toSubscriptionGetResponse(Subscription subscription) {
    return SubscriptionGetResponse.builder()
        .userId(subscription.getFollowing().getId())
        .nickname(subscription.getFollowing().getNickName())
        .profileImageUrl(subscription.getFollowing().getProfileImageUrl())
        .createdAt(subscription.getFollowing().getCreatedAt())
        .build();
  }

}
