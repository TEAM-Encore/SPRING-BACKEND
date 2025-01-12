package encore.server.domain.post.service;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.repository.PostRepository;
import encore.server.global.common.ExtractNouns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostRelatedSearchService {

    @Autowired
    @Qualifier("relatedSearchLogRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;
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
            List<Post> posts = postRepository.findByPostAutoCompleteSuggestions(userId, keyword);

            for (Post post : posts) {
                Set<String> extractedWords = extractNouns.extractNounsStartingWithPostField(
                        post, keyword
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
    public void updateAllSuggestions(Long userId, Post post) {
        Set<String> keys = redisTemplate.keys(userId + "_*");

        for (String key : keys) {
            Set<String> extractedWords = extractNouns.extractNounsStartingWithPostField(
                    post, key.split("_")[1]
            );
            for (String word : extractedWords) {
                updateSuggestions(userId, key.split("_")[1], word);
            }
        }
    }

}