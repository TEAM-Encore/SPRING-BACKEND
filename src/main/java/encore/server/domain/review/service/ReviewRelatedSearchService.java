package encore.server.domain.review.service;

import encore.server.domain.review.entity.Review;
import encore.server.domain.review.repository.ReviewRepository;
import encore.server.global.common.ExtractNouns;
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
        return suggestions.stream()
                .filter(word -> word != null && !word.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 새로운 리뷰 작성 시 Redis ZSET의 점수(score) 증가
     *
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @param newWord 새로운 단어
     */
    public void updateSuggestions(Long userId, String keyword, String newWord) {
        if (newWord == null) {
            return;
        }
        String redisKey = buildRedisKey(userId, keyword);
        redisTemplate.opsForZSet().incrementScore(redisKey, newWord, 1);
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

        Set<String> redisSuggestions = getSuggestionsFromRedis(userId, keyword);
        Set<String> finalResults = new HashSet<>();


        if (redisSuggestions != null && !redisSuggestions.isEmpty()) {
            finalResults.addAll(redisSuggestions);
        } else {
            List<Review> reviews = reviewRepository.findByReviewAutoCompleteSuggestions(userId, keyword);

            for (Review review : reviews) {
                Set<String> extractedWords = extractNouns.extractNounsStartingWithAnyField(
                        review, keyword
                );
                finalResults.addAll(extractedWords);

                for (String word : extractedWords) {
                    updateSuggestions(userId, keyword, word);
                }
            }
        }

        return finalResults.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 리뷰 작성 시 모든 추천 단어 삭제 및 업데이트
     *
     * @param userId 사용자 ID
     */
    public void updateAllSuggestions(Long userId, Review review) {
        Set<String> keys = redisTemplate.keys(userId + "_*");

        for (String key : keys) {
            Set<String> extractedWords = extractNouns.extractNounsStartingWithAnyField(
                    review, key.split("_")[1]
            );
            for (String word : extractedWords) {
                updateSuggestions(userId, key.split("_")[1], word);
            }
        }
    }

}