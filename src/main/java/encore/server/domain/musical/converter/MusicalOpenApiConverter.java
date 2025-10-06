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
        return MusicalOpenApiDto.builder()
                .mt20id(getTagValue("mt20id", element))
                .prfnm(getTagValue("prfnm", element))
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
}
