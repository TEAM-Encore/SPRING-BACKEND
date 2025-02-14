package encore.server.domain.musical.service;

import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.global.config.JsoupConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicalListService {
    private final MusicalRepository musicalRepository;
    private final JsoupConfig jsoupConfig;
    private final MusicalInfoService musicalInfoService;

    @Value("${jsoup.goods.list-url}")
    private String url;

    /**
     * Interpark 뮤지컬 페이지에서 뮤지컬 ID(GroupCode)를 추출하여 저장하는 메서드
     * @return List<String> musicalIds (저장할 뮤지컬 ID 목록)
     */
    public List<String> crawlingMusicalInfo() {
        List<String> musicalIds = new ArrayList<>();
        try {
            // 1. Jsoup 연결 및 Document 가져오기
            Connection connection = jsoupConfig.jsoupConnection(url);
            Document document = connection.get();

            // 2. 뮤지컬 리스트에서 각 ID(GroupCode) 추출
            Elements rows = document.select("tbody > tr");

            // 3. 각 행에서 GroupCode 추출
            for (Element row : rows) {
                Element linkElement = row.selectFirst("a[href*='GoodsInfo.asp?GroupCode=']");
                if (linkElement != null) { // GroupCode가 포함된 링크가 존재하는 경우
                    String href = linkElement.attr("href");
                    String groupId = extractGroupId(href); // GroupCode 추출
                    if(musicalRepository.existsByInterparkId(groupId)) { // 이미 저장했던 뮤지컬인 경우 패스
                        continue;
                    }
                    if (groupId != null) {
                        musicalIds.add(groupId);
                        log.info("GroupCode: " + groupId);
                        musicalInfoService.fetchMusicalDetails(groupId); // 뮤지컬 상세 정보 가져오기
                    }
                }
            }
            log.info(("총 " + musicalIds.size() + "개의 뮤지컬 ID 추출 완료"));
        } catch (Exception e) {
            e.getStackTrace();
        }
        return musicalIds;
    }

    /**
     * href에서 GroupCode 추출하는 메서드
     * @param href
     * @return GroupCode
     */
    private String extractGroupId(String href) {
        String prefix = "GroupCode=";
        int startIndex = href.indexOf(prefix);
        if (startIndex != -1) {
            return href.substring(startIndex + prefix.length());
        }
        return null;
    }
}
