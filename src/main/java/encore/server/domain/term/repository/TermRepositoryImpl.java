package encore.server.domain.term.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import encore.server.domain.term.entity.Term;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static encore.server.domain.term.entity.QTerm.term;

@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // content 내에서 term을 찾는 메서드
    public List<Term> findByTermIn(String content) {
        Set<String> seenTermNames = new HashSet<>();
        return queryFactory.selectFrom(term)
                .fetch().stream()
                .filter(t -> content.contains(t.getName()) && seenTermNames.add(t.getName()))
                .collect(Collectors.toList());
    }

    // term을 content에서 <span> 태그로 감싸는 메서드
    public String highlightTermsInContent(String content) {
        List<Term> matchingTerms = findByTermIn(content);

        // 각 MusicalTerm에 대해 content를 수정
        for (Term term : matchingTerms) {
            // term이 이미 <span> 태그로 감싸지지 않았을 때만 <span> 태그로 감싸기
            content = content.replaceFirst("(?<!<span[^>]>)" + Pattern.quote(term.getName()) + "(?!</span>)",
                    "<span class='term' title='" + term.getDescription() + "'>" + term.getName() + "</span>");
        }

        return content; // 수정된 content 반환
    }
}