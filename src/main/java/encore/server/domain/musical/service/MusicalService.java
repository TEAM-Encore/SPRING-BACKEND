package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalConverter;
import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
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
                .map(MusicalConverter::toResponse) // Converter를 이용해 변환으로 수정!
                .toList();
    }

    public MusicalDetailRes getMusicalDetail(Long musicalId) {
        //validation
        Musical musical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        return MusicalConverter.toMusicalDetailRes(musical);
    }
}
