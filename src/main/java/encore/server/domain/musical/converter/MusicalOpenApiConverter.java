package encore.server.domain.musical.converter;

import encore.server.domain.musical.dto.external.MusicalOpenApiDto;
import encore.server.domain.musical.entity.Musical;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MusicalOpenApiConverter {

    private static final DateTimeFormatter DOT_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * XML -> OpenApi DTO
     */
    public static MusicalOpenApiDto toDto(Element element) {
        // 1. 원본 제목(prfnm)을 가져옵니다.
        String rawTitle = getTagValue("prfnm", element);

        // 2. 헬퍼 메서드를 이용해 제목을 정제합니다.
        String cleanedTitle = cleanMusicalTitle(rawTitle);

        return MusicalOpenApiDto.builder()
                .mt20id(getTagValue("mt20id", element))
                .prfnm(cleanedTitle) // 3. 정제된 제목을 DTO에 저장합니다.
                .prfpdfrom(getTagValue("prfpdfrom", element))
                .prfpdto(getTagValue("prfpdto", element))
                .fcltynm(getTagValue("fcltynm", element))
                .poster(getTagValue("poster", element))
                .build();
    }

    /**
     * OpenApi DTO -> Musical Entity
     */
    public static Musical toEntity(MusicalOpenApiDto dto) {
        return Musical.builder()
                .openApiId(dto.mt20id())
                .title(dto.prfnm())
                .startDate(LocalDate.parse(dto.prfpdfrom(), DOT_FORMATTER))
                .endDate(LocalDate.parse(dto.prfpdto(), DOT_FORMATTER))
                .location(dto.fcltynm())
                .imageUrl(dto.poster())
                .build();
    }

    public static void updateEntity(Musical target, MusicalOpenApiDto dto) {
        target.updateTitle(dto.prfnm());
        target.updateStartDate(LocalDate.parse(dto.prfpdfrom(), DOT_FORMATTER));
        target.updateEndDate(LocalDate.parse(dto.prfpdto(), DOT_FORMATTER));
        target.updateLocation(dto.fcltynm());
        target.updateImageUrl(dto.poster());
    }


    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return null;
    }

    /**
     * 뮤지컬 제목에서 "[지역명]" 또는 "[기타 정보]" 부분을 제거합니다.
     * @param title 원본 제목
     * @return 정제된 제목
     */
    private static String cleanMusicalTitle(String title) {
        if (title == null || title.isBlank()) {
            return title;
        }

        // 정규식을 사용하여 문자열 끝에 오는 " [어쩌고]" 부분을 제거합니다.
        // 예: "레미제라블 [서울]" -> "레미제라블"
        // 예: "오페라의 유령 [부산 드림씨어터]" -> "오페라의 유령"
        // \\s* : 0개 이상의 공백 (예: "제목[정보]")
        // \\[[^]]+\\]$ : 문자열 끝($)에 있는, [...] 형태이며 내부에 ]가 없는(+) 문자열
        return title.replaceAll("\\s*\\[[^]]+\\]$", "").trim();
    }
}
