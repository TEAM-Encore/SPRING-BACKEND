package encore.server.domain.term.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.term.entity.Term;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static encore.server.domain.term.entity.QTerm.term;


@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    // content 내에서 term을 찾는 메서드
    public List<Term> findByTermIn(String content) {
        List<Term> matchingTerms = new ArrayList<>();
        for (Term term : queryFactory.selectFrom(term).fetch()) {
            if (content.contains(term.getName())) { // term이 content에 포함되어 있는지 확인
                matchingTerms.add(term);
            }
        }
        return matchingTerms;
    }

    // term을 content에서 <span> 태그로 감싸는 메서드
    public String highlightTermsInContent(String content) {
        List<Term> matchingTerms = findByTermIn(content);

        // 각 MusicalTerm에 대해 content를 수정
        for (Term term : matchingTerms) {
            // term이 이미 <span> 태그로 감싸지지 않았을 때만 <span> 태그로 감싸기
            content = content.replaceAll("(?<!<span[^>]>)" + Pattern.quote(term.getName()) + "(?!</span>)",
                    "<span class='term' title='" + term.getDescription() + "'>" + term.getName() + "</span>");
        }

        return content; // 수정된 content 반환
    }
}