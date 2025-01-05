package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalConverter;
import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.dto.response.MusicalSeriesRes;
import encore.server.domain.musical.dto.response.MusicalSimpleRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.review.entity.Review;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public List<MusicalSeriesRes> getAllSeriesByMusicalId(Long musicalId) {
        Musical currentMusical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        List<Musical> musicals = musicalRepository.findByTitleAndDeletedAtIsNull(currentMusical.getTitle());

        return musicals.stream()
                .map(musical -> MusicalSeriesRes.builder()
                        .id(musical.getId())
                        .series(musical.getSeries())
                        .isCurrent(musical.getId().equals(musicalId)) // 현재 선택된 시리즈 표시
                        .build())
                .toList();
    }

    public List<MusicalSimpleRes> getFeaturedMusicals() {
        List<Musical> musicals = musicalRepository.findTop8ByIsFeaturedTrueOrderByStartDateAsc();
        return musicals.stream()
                .map(musical -> MusicalSimpleRes.builder()
                        .id(musical.getId())
                        .title(musical.getTitle())
                        .startDate(musical.getStartDate())
                        .endDate(musical.getEndDate())
                        .location(musical.getLocation())
                        .imageUrl(musical.getImageUrl())
                        .build())
                .toList();
    }

    public List<MusicalSimpleRes> getUpcomingMusicals() {
        LocalDateTime now = LocalDateTime.now();
        List<Musical> musicals = musicalRepository.findUpcomingMusicals(now);

        return musicals.stream()
                .limit(8)
                .map(musical -> MusicalSimpleRes.builder()
                        .id(musical.getId())
                        .title(musical.getTitle())
                        .startDate(musical.getStartDate())
                        .endDate(musical.getEndDate())
                        .location(musical.getLocation())
                        .imageUrl(musical.getImageUrl())
                        .build())
                .toList();
    }

}
