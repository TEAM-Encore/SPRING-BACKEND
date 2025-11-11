package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalResponseConverter;
import encore.server.domain.musical.dto.response.MusicalDetailRes;
import encore.server.domain.musical.dto.response.MusicalRes;
import encore.server.domain.musical.dto.response.MusicalSimpleRes;
import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.ticket.repository.ActorRepository;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicalService {
    private final MusicalRepository musicalRepository;

    public Page<MusicalRes> searchMusicalsByTitle(String keyword, Pageable pageable) {
        Page<Musical> musicalPage = musicalRepository.findByTitleStartingWithAndDeletedAtIsNull(keyword, pageable);
        return musicalPage.map(MusicalResponseConverter::toResponse);
    }

    public MusicalDetailRes getMusicalDetail(Long musicalId) {
        //validation
        Musical musical = musicalRepository.findByIdAndDeletedAtIsNull(musicalId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MUSICAL_NOT_FOUND_EXCEPTION));

        return MusicalResponseConverter.toMusicalDetailRes(musical);
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
