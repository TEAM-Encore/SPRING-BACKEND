package encore.server.domain.term.dto;

import lombok.Builder;

@Builder
public record MusicalTermRes(
        Long id,
        String term,
        String description
) {
}
