package encore.server.domain.musical.service;

import encore.server.global.common.ParsingShowTime;
import encore.server.global.config.SeleniumConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MusicalInfoService {
    private final SeleniumConfig seleniumConfig;
    private final ParsingShowTime parsingShowTime;

    @Value("${jsoup.goods.url}")
    private String goodsUrl;

    /**
     * 뮤지컬 상세 정보를 가져오는 메서드
     * @param groupId
     */
    public void fetchMusicalDetails(String groupId) {
        String url = goodsUrl + groupId;
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 페이지로 이동
            log.info("Navigating to the URL...");
            driver.get(url);

            // 페이지가 완전히 로드될 때까지 대기
            log.info("Waiting for the page to load...");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.prdTitle")));

            // 뮤지컬 제목 가져오기
            WebElement titleElement = driver.findElement(By.cssSelector("h2.prdTitle"));
            String title = titleElement.getText();
            log.info("뮤지컬 제목: {}", title);

            // 뮤지컬 이미지 URL 가져오기
            WebElement imgElement = driver.findElement(By.cssSelector("div.posterBoxTop img.posterBoxImage"));
            String imageUrl = imgElement != null ? imgElement.getAttribute("src") : null;
            log.info("뮤지컬 이미지 URL: {}", imageUrl);

            // 장소 가져오기
            WebElement locationElement = driver.findElement(By.xpath("//li[contains(@class, 'infoItem')]//strong[contains(text(), '장소')]/following-sibling::div//a"));
            String location = locationElement.getText().replaceAll("\\(.*?\\)", "").trim();
            log.info("장소: {}", location);

            // 공연 기간 가져오기
            WebElement periodElement = driver.findElement(By.xpath("//li[contains(@class, 'infoItem')]//strong[contains(text(), '공연기간')]/following-sibling::div//p[@class='infoText']"));
            String performancePeriod = periodElement.getText();
            String[] periodSplit = performancePeriod.split("~");
            String startDate = periodSplit[0].trim();
            String endDate = periodSplit[1].trim();
            log.info("공연 시작일: {}", parseDate(startDate));
            log.info("공연 종료일: {}", parseDate(endDate));

            // 공연 시간 가져오기
            WebElement runningTimeElement = driver.findElement(By.xpath("//li[contains(@class, 'infoItem')]//strong[contains(text(), '공연시간')]/following-sibling::div//p[@class='infoText']"));
            Long runningTime = Long.parseLong(runningTimeElement.getText().replaceAll("\\(.*?\\)", "").replace("분", "").trim());
            log.info("공연 시간 (분): {}", runningTime);

            // 관람 연령 가져오기
            WebElement ageElement = driver.findElement(By.xpath("//li[contains(@class, 'infoItem')]//strong[contains(text(), '관람연령')]/following-sibling::div//p[@class='infoText']"));
            Long age = Long.parseLong(ageElement.getText().replaceAll("\\(.*?\\)", "").replaceAll("[^0-9]", "").trim());
            log.info("관람 연령: {}", age);

            // 뮤지컬 시간 가져오기
            WebElement showTimeElement = driver.findElement(By.xpath("//ul[@class='contentDetailList']/div"));
            String showTime = showTimeElement.getText();
            log.info("뮤지컬 시간: {}", showTime);
            List<String> showTimes = parsingShowTime.parseShowTimes(showTime);
            showTimes.forEach(show -> log.info("공연 시간: {}", show));

            WebElement moreButton = driver.findElement(By.xpath("//a[@class='contentToggleBtn' and @aria-label='여닫기']"));
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("arguments[0].click();", moreButton);

            // 뮤지컬 캐스팅정보, 배우 정보 가져오기
            List<WebElement> castingItems = driver.findElements(By.xpath("//ul[@class='castingList']/li[@class='castingItem']"));

            for (WebElement item : castingItems) {
                String actorName = item.findElement(By.xpath(".//div[@class='castingInfo']/div[@class='castingName']")).getText();
                log.info("배우 이름: {}", actorName);

                String roleName = item.findElement(By.xpath(".//div[@class='castingInfo']/div[@class='castingActor']")).getText();
                log.info("역할 이름: {}", roleName);

                String actorPhotoUrl = item.findElement(By.xpath(".//div[@class='castingTop']//img[@class='castingImage']")).getAttribute("src");
                log.info("배우 사진 URL: {}", actorPhotoUrl);

                log.info("-----------");
            }
        } catch (Exception e) {
            log.error("Error fetching details for GroupId {}: {}", groupId, e.getMessage());
        } finally {
            // 드라이버 종료
            seleniumConfig.quitDriver();
        }
    }

    /**
     * 날짜 문자열을 파싱하여 LocalDateTime 객체로 변환하는 메서드
     * @param dateStr
     * @return
     */
    private LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        return date.atStartOfDay();
    }
}