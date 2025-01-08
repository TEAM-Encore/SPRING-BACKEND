package encore.server.global.common;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ExtractNouns {
    private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    public Set<String> extractNounsStartingWith(String text, String keyword) {
        List<Token> tokens = komoran.analyze(text).getTokenList();
        Set<String> results = new HashSet<>();

        if (text.startsWith(keyword)) {
            results.add(text);
        }

        for (Token token : tokens) {
            String word = token.getMorph();
            if (word.startsWith(keyword) && token.getPos().startsWith("N")) {
                results.add(word);
            }
        }
        return results;
    }
}