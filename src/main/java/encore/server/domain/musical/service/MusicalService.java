package encore.server.domain.musical.service;

import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicalService {
    private final MusicalRepository musicalRepository;

    public List<MusicalRes> searchMusicalsByTitle(String keyword) {
        List<Musical> musicals = musicalRepository.findByTitleContaining(keyword);
        return musicals.stream()
                .map(musical -> MusicalRes.builder()
                        .musicalId(musical.getId())
                        .title(musical.getTitle())
                        .series(musical.getSeries())
                        .build())
                .toList();
    }
}
