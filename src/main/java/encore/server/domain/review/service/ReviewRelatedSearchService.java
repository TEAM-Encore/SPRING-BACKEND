package encore.server.domain.review.service;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.global.common.ExtractNouns;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewRelatedSearchService {

    @Autowired
    @Qualifier("relatedSearchLogRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private final ReviewRepository reviewRepository;
    private final ExtractNouns extractNouns;

    /**
     * Redis ZSET에서 키워드에 대한 추천 단어 가져오기
     *
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return 추천 단어 세트
     */
    public Set<String> getSuggestionsFromRedis(Long userId, String keyword) {
        String redisKey = buildRedisKey(userId, keyword);
        Set<String> suggestions = redisTemplate.opsForZSet().reverseRange(redisKey, 0, -1);
        log.info("Redis에서 가져온 추천어 (key: '{}'): {}", redisKey, suggestions);
        return suggestions;
    }

    /**
     * 새로운 리뷰 작성 시 Redis ZSET의 점수(score) 증가
     *
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @param newWord 새로운 단어
     */
    public void updateSuggestions(Long userId, String keyword, String newWord) {
        String redisKey = buildRedisKey(userId, keyword);
        redisTemplate.opsForZSet().incrementScore(redisKey, newWord, 1);
        log.info("Redis 업데이트 (key: '{}', word: '{}')", redisKey, newWord);
    }

    /**
     * Redis 키 생성
     *
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return Redis 키
     */
    private String buildRedisKey(Long userId, String keyword) {
        return userId + "_" + keyword;
    }

    /**
     * 자동 완성 추천 단어 가져오기
     *
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return 추천 단어 목록
     */
    public List<String> getAutoCompleteSuggestions(Long userId, String keyword) {
        log.info("자동 완성 추천어 가져오기: keyword = '{}'", keyword);

        Set<String> redisSuggestions = getSuggestionsFromRedis(userId, keyword);
        Set<String> finalResults = new HashSet<>();

        if (redisSuggestions != null && !redisSuggestions.isEmpty()) {
            log.info("Redis에서 추천어를 찾았습니다: {}", redisSuggestions);
            finalResults.addAll(redisSuggestions);
        } else {
            List<Review> reviews = reviewRepository.findByUserIdAndTitleContaining(userId, keyword);
            log.info("DB에서 {}개의 리뷰를 찾았습니다. 키워드 '{}', userId {}", reviews.size(), keyword, userId);

            for (Review review : reviews) {
                if (review.getTitle().startsWith(keyword)) {
                    Set<String> extractedWords = extractNouns.extractNounsStartingWith(review.getTitle(), keyword);
                    finalResults.addAll(extractedWords);

                    for (String word : extractedWords) {
                        updateSuggestions(userId, keyword, word);
                    }
                }
            }
        }

        log.info("최종 자동 완성 추천어: {}", finalResults);
        return finalResults.stream().sorted().collect(Collectors.toList());
    }
}