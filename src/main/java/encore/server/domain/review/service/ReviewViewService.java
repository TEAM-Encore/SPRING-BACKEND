package encore.server.domain.review.service;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewViewService {
    @Value("${redis.visited-posts-key-prefix}")
    private String VISITED_POSTS_KEY_PREFIX;

    @Value("${redis.review-views-key}")
    private String REVIEW_VIEWS_KEY;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ReviewRepository reviewRepository;

    // 사용자 방문한 게시물에 대한 조회수 관리
    public void addVisitedRedis(User user, Review review) {
        String viewedKey = VISITED_POSTS_KEY_PREFIX + user.getId();
        Boolean viewed = redisTemplate.opsForSet().isMember(viewedKey, review.getId());

        // 해당 사용자가 조회한 적 없다면, 조회수 증가 처리
        if (viewed == null || !viewed) {
            redisTemplate.opsForSet().add(viewedKey, review.getId());
            redisTemplate.expire(viewedKey, 24, TimeUnit.HOURS);  // 24시간 후 캐시 만료

            incrementViewCount(review.getId()); // Redis에서 조회수 증가
        }
    }

    public void incrementViewCount(Long reviewId) {
        redisTemplate.opsForHash().increment(REVIEW_VIEWS_KEY, reviewId, 1);
    }

    // 일정 주기로 Redis에 저장된 조회수를 DB에 반영하고 Redis에서 삭제
    @Transactional
    @Scheduled(fixedRate = 10000)
    public void batchUpdateViews() {
        Map<Object, Object> views = redisTemplate.opsForHash().entries(REVIEW_VIEWS_KEY);

        for (Map.Entry<Object, Object> entry : views.entrySet()) {
            Long reviewId = (Long) entry.getKey();
            Long increment = (long) ((Number) entry.getValue()).intValue();

            // DB에 반영
            reviewRepository.updateViewCount(reviewId, increment);

            // Redis에서 해당 게시물 조회수 삭제
            redisTemplate.opsForHash().delete(REVIEW_VIEWS_KEY, reviewId);
        }
    }
}
