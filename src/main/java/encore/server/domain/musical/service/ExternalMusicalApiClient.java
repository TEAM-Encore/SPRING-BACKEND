package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalOpenApiConverter;
import encore.server.domain.musical.dto.external.MusicalOpenApiDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExternalMusicalApiClient {

    private static final String BASE_URL = "http://www.kopis.or.kr/openApi/restful/pblprfr";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<MusicalOpenApiDto> fetchMusicals(
            String serviceKey,
            String startDate,
            String endDate,
            int page,
            int rows,
            String afterDate
    ) {
        try {
            int safeRows = Math.max(1, Math.min(100, rows));
            int safePage = Math.max(1, page);

            UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("service", serviceKey)
                    .queryParam("stdate", startDate)
                    .queryParam("eddate", endDate)
                    .queryParam("cpage", safePage)
                    .queryParam("rows", safeRows)
                    .queryParam("shcate", "GGGA"); // 뮤지컬 고정

            if (afterDate != null && !afterDate.isBlank()) {
                ub.queryParam("afterdate", afterDate);
            }

            String url = ub.toUriString();
            log.debug("[ExternalMusicalApiClient] 요청 URL={}", url);

            String response = restTemplate.getForObject(url, String.class);

            return parseXmlToDto(response);
        } catch (Exception e) {
            log.error("KOPIS API 호출 실패", e);
            return List.of();
        }
    }

    private List<MusicalOpenApiDto> parseXmlToDto(String xml) throws Exception {
        // ISO-8859-1로 들어온 문자열을 UTF-8로 보정
        String utf8Xml = new String(xml.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        List<MusicalOpenApiDto> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(utf8Xml.getBytes(StandardCharsets.UTF_8)));

        NodeList dbList = doc.getElementsByTagName("db");
        log.info("[KOPIS] parsed db nodes={}", dbList.getLength());

        for (int i = 0; i < dbList.getLength(); i++) {
            Element e = (Element) dbList.item(i);
            list.add(MusicalOpenApiConverter.toDto(e));
        }

        list.stream().limit(2).forEach(m ->
                log.debug("[KOPIS] sample: id={} title={} {}~{}",
                        m.mt20id(), m.prfnm(), m.prfpdfrom(), m.prfpdto())
        );
        return list;
    }

    private String maskServiceKey(String url) {
        return url.replaceAll("(service=)([^&]*)", "$1****");
    }

    private String headSample(String s, int max) {
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() <= max ? t : t.substring(0, max) + "...";
    }
}
