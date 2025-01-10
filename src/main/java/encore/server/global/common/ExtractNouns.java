package encore.server.global.common;

import encore.server.domain.review.entity.Review;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ExtractNouns {
    private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    public Set<String> extractNounsStartingWithAnyField(Review review, String keyword) {
        Set<String> words = new HashSet<>();

        // 필드 리스트를 배열로 선언
        String[] fields = {
                review.getTitle(),  // 제목 필드
                review.getReviewData().getRating().getRatingReview(),  // 평점 리뷰 필드
                review.getReviewData().getFacility().getFacilityReview(),  // 시설 리뷰 필드
                review.getReviewData().getSound().getSoundReview(),  // 소리 리뷰 필드
                review.getReviewData().getView().getViewReview()  // 뷰 리뷰 필드
        };

        // Stream API를 사용하여 키워드와 시작하는 명사를 추출
        Arrays.stream(fields)
                .filter(field -> field != null && field.contains(keyword))
                .forEach(field -> words.addAll(extractNounsStartingWith(field, keyword)));  // 명사 추가

        return words;
    }

    /**
     * 주어진 텍스트에서 특정 키워드로 시작하는 명사만 추출하는 메서드
     *
     * @param text    분석할 텍스트
     * @param keyword 명사로 시작하는 키워드
     * @return 특정 키워드로 시작하는 명사들을 포함한 Set
     */
    public Set<String> extractNounsStartingWith(String text, String keyword) {
        // 텍스트 분석 결과 토큰 리스트 가져오기
        List<Token> tokens = komoran.analyze(text).getTokenList();
        Set<String> results = new HashSet<>();

        boolean allNouns = true;
        boolean isKeywordIn = false;
        StringBuilder result = new StringBuilder();

        // 토큰 리스트를 순회하며 명사인지 확인하고, 명사일 경우 결과 문자열에 추가
        for (Token token : tokens) {
            if (token.getPos().startsWith("N") && !isKeywordIn && token.getMorph().startsWith(keyword)) {
                result.append(token.getMorph()).append(" ");
                isKeywordIn = true;
            } else if (token.getPos().startsWith("N") && isKeywordIn) {
                result.append(token.getMorph()).append(" ");
            } else {
                allNouns = false;
                break;
            }
        }

        log.info("11111Extracted nouns: {}", result);

        // 모든 토큰이 명사일 경우, 마지막 문자 한 개를 제외한 명사 부분만 추가
        if (allNouns) {
            int endIndex = result.length() > 0 ? result.length() - 1 : result.length();
            results.add(result.toString().substring(0, endIndex).trim());
        } else {
            // 명사가 아닌 부분을 제외한 부분을 결과로 추가
            int endIndex = Math.max(0, result.length() - 1);
            results.add(result.toString().substring(0, endIndex).trim());
        }

        log.info("22222Extracted nouns: {}", results);

        // 키워드로 시작하는 명사를 필터링하여 추가
        for (Token token : tokens) {
            String word = token.getMorph();
            if (word.startsWith(keyword) && token.getPos().startsWith("N")) {
                results.add(word);
            }
        }

        log.info("33333Extracted nouns: {}", results);

        // 결과에서 빈 문자열 제거
        results.remove("");

        return results;
    }
}