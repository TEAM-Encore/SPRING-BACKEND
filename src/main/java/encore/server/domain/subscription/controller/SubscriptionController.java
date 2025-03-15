package encore.server.domain.subscription.controller;

import encore.server.domain.subscription.dto.request.SubscribeRequest;
import encore.server.domain.subscription.dto.response.SubscriptionGetResponse;
import encore.server.domain.subscription.service.SubscriptionService;
import encore.server.global.aop.annotation.LoginUserId;
import encore.server.global.common.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subscriptions")
@Tag(name = "Subscription", description = "구독 API")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @PostMapping
  @Operation(summary = "구독하기 API", description = "특정 사용자를 구독합니다.")
  public ApplicationResponse<Void> subscribe(@RequestBody @Valid SubscribeRequest request,
      @LoginUserId Long loginUserId) {
    subscriptionService.subscribe(loginUserId, request.followingUserId());
    return ApplicationResponse.created();
  }

  @DeleteMapping("/users/{targetUserId}")
  @Operation(summary = "구독취소 API", description = "특정 사용자를 구독 취소합니다.")
  public ApplicationResponse<Void> unsubscribe(@PathVariable Long targetUserId,
      @LoginUserId Long loginUserId) {
    subscriptionService.unsubscribe(loginUserId, targetUserId);
    return ApplicationResponse.ok();
  }

  @GetMapping
  @Operation(summary = "구독자 목록 페이징 조회 API", description = "구독자 목록을 커서 기반으로 페이징 조회합니다.")
  public ApplicationResponse<Slice<SubscriptionGetResponse>> getFollowing(
      @RequestParam(required = false) LocalDateTime cursor,
      @RequestParam(defaultValue = "30") int size,
      @LoginUserId Long loginUserId) {
    return ApplicationResponse.ok(subscriptionService.getFollowing(loginUserId, cursor, size));
  }

  @GetMapping("/users/{targetUserId}")
  @Operation(summary = "구독 여부 조회 API", description = "내가 특정 사용자를 구독중인지 확인합니다.")
  public ApplicationResponse<Boolean> isFollowing(@PathVariable Long targetUserId,
      @LoginUserId Long loginUserId) {
    return ApplicationResponse.ok(subscriptionService.isFollowing(loginUserId, targetUserId));
  }

}
