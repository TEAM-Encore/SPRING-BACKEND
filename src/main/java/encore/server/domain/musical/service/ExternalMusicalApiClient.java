package encore.server.domain.musical.service;

import encore.server.domain.musical.converter.MusicalOpenApiConverter;
import encore.server.domain.musical.dto.external.MusicalOpenApiDto;
import encore.server.global.exception.ApplicationException;
import encore.server.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
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
        log.debug("[KOPIS] 요청 URL={}", maskServiceKey(url));

        final String response;
        try {
            response = restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            log.error("[KOPIS] HTTP 호출 실패: {}", e.getMessage(), e);
            throw new ApplicationException(ErrorCode.KOPIS_SERVICE_ERROR);
        }

        if (response == null || response.isBlank()) {
            log.error("[KOPIS] 응답 본문이 비어있습니다.");
            throw new ApplicationException(ErrorCode.KOPIS_SERVICE_ERROR);
        }

        return parseXmlToDtoOrThrow(response);
    }

    /** 오류 응답이면 ApplicationException 즉시 throw, 정상일 때만 DTO 목록 반환 */
    private List<MusicalOpenApiDto> parseXmlToDtoOrThrow(String xml) {
        try {
            // 인코딩 보정
            String utf8Xml = new String(xml.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(utf8Xml.getBytes(StandardCharsets.UTF_8)));

            // ✅ 에러 응답 체크
            NodeList returnCodeList = doc.getElementsByTagName("returncode");
            if (returnCodeList.getLength() > 0) {
                String code = text(returnCodeList.item(0));

                String msg = "";
                NodeList msgList = doc.getElementsByTagName("errmsg");
                if (msgList.getLength() > 0) {
                    msg = text(msgList.item(0));
                }

                // 코드 매핑 (필요 시 확장)
                if (!"00".equals(code)) {
                    // 02: SERVICE KEY IS NOT REGISTERED ERROR → 인증 이슈로 간주
                    if ("02".equals(code)) {
                        log.error("[KOPIS] 인증 오류(code=02): {}", msg);
                        throw new ApplicationException(ErrorCode.KOPIS_SERVICE_KEY_INVALID);
                    }
                    // 그 외: 잘못된 요청/시스템 오류 등
                    log.error("[KOPIS] 오류 응답(code={}): {}", code, msg);
                    throw new ApplicationException(ErrorCode.KOPIS_SERVICE_ERROR);
                }
            }

            // 정상: <db> 파싱
            NodeList dbList = doc.getElementsByTagName("db");
            log.info("[KOPIS] parsed db nodes={}", dbList.getLength());

            List<MusicalOpenApiDto> list = new ArrayList<>(dbList.getLength());
            for (int i = 0; i < dbList.getLength(); i++) {
                Element e = (Element) dbList.item(i);
                list.add(MusicalOpenApiConverter.toDto(e));
            }
            return list;

        } catch (ApplicationException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("[KOPIS] XML 파싱 실패: {}", e.getMessage(), e);
            throw new ApplicationException(ErrorCode.KOPIS_SERVICE_ERROR);
        }
    }

    private static String text(Node node) {
        return (node == null || node.getTextContent() == null) ? "" : node.getTextContent().trim();
    }

    private String maskServiceKey(String url) {
        return url.replaceAll("(service=)([^&]*)", "$1****");
    }
}
