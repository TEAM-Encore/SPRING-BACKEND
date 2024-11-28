package encore.server.domain.term.converter;

import encore.server.domain.term.dto.MusicalTermRes;
import encore.server.domain.term.entity.Term;

import java.util.List;

public class MusicalTermConverter {
    public static MusicalTermRes toMusicalTermRes(Term musicalTerm) {
        return MusicalTermRes.builder()
                .id(musicalTerm.getId())
                .term(musicalTerm.getName())
                .description(musicalTerm.getDescription())
                .build();
    }

    public static List<MusicalTermRes> toMusicalTermResList(List<Term> musicalTerms) {
        return musicalTerms.stream()
                .map(MusicalTermConverter::toMusicalTermRes)
                .toList();
    }
}
