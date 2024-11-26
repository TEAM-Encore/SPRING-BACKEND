package encore.server.domain.term.repository;

import encore.server.domain.term.entity.Term;

import java.util.List;

public interface TermRepositoryCustom {
    List<Term> findByTermIn(String content);
    String highlightTermsInContent(String content);
}
