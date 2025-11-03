package encore.server.domain.musical.dto.external;

import lombok.Builder;

@Builder
public record MusicalOpenApiDto(
        String mt20id,     // 공연 ID (외부 PK)
        String prfnm,      // 공연명
        String prfpdfrom,  // 공연 시작일 (yyyy.MM.dd)
        String prfpdto,    // 공연 종료일 (yyyy.MM.dd)
        String fcltynm,    // 공연시설명
        String poster     // 포스터 URL
) {
}
