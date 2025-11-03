package encore.server.domain.review.service;

import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import encore.server.global.util.redis.SearchLogRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewRecentSearchService {

    private final UserRepository userRepository;

    @Qualifier("searchLogRedisTemplate")
    private final RedisTemplate<String, SearchLogRedis> redisTemplate;

    @Value("${redis.recent-keyword-size}")
    private int RECENT_KEYWORD_SIZE;

    public void saveRecentSearchLog(String keyword, Long userId) {
        // validation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        double score = System.currentTimeMillis();

        // 키와 값 설정
        String key = searchLogKey(user.getId());
        SearchLogRedis value = SearchLogRedis.builder()
                .name(keyword)
                .build();

        // Redis Sorted Set에 키워드와 score 저장
        redisTemplate.opsForZSet().add(key, value, score);

        // 최대 크기 제한 (RECENT_KEYWORD_SIZE를 넘지 않도록)
        Long size = redisTemplate.opsForZSet().size(key);
        if (size != null && size > RECENT_KEYWORD_SIZE) {
            redisTemplate.opsForZSet().removeRange(key, 0, 0);  // 가장 오래된 항목(점수가 가장 작은)을 제거
        }
    }

    public Set<SearchLogRedis> getRecentSearchLogs(Long userId) {
        String key = searchLogKey(userId);
        // 최근 키워드 목록 조회 (가장 최근 10개)
        return redisTemplate.opsForZSet().reverseRange(key, 0, RECENT_KEYWORD_SIZE - 1);
    }

    public Set<SearchLogRedis> deleteRecentSearchLog(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));

        String key = searchLogKey(userId);
        SearchLogRedis value = SearchLogRedis.builder()
                .name(name)
                .build();

        // 키워드가 존재하지 않을 경우 예외 처리
        if(redisTemplate.opsForZSet().rank(key, value) == null) {
            throw new ApplicationException(ErrorCode.SEARCH_LOG_NOT_FOUND_EXCEPTION);
        }

        // 키워드 삭제
        redisTemplate.opsForZSet().remove(key, value);
        return redisTemplate.opsForZSet().reverseRange(key, 0, RECENT_KEYWORD_SIZE - 1);
    }

    public void deleteAllRecentSearchLogs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION));
        String key = searchLogKey(userId);

        // Sorted Set의 모든 항목을 삭제
        redisTemplate.opsForZSet().removeRange(key, 0, -1);
    }

    private String searchLogKey(Long userId) {
        return "review_search_log_" + userId;
    }
}