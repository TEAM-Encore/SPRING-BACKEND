package encore.server.domain.musical.service;

import encore.server.domain.musical.entity.Musical;
import encore.server.domain.musical.entity.MusicalActor;
import encore.server.domain.musical.repository.MusicalRepository;
import encore.server.domain.ticket.entity.Actor;
import encore.server.domain.ticket.repository.ActorRepository;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicalInfoService {
    private final SeleniumConfig seleniumConfig;
    private final MusicalRepository musicalRepository;
    private final ActorRepository actorRepository;

    @Value("${jsoup.goods.url}")
    private String goodsUrl;

    @Value("${selenium.title-css-selector}")
    private String titleCssSelector;

    @Value("${selenium.img-css-selector}")
    private String imgCssSelector;

    @Value("${selenium.location-xpath}")
    private String locationXpath;

    @Value("${selenium.period-xpath}")
    private String periodXpath;

    @Value("${selenium.runningtime-xpath}")
    private String runningTimeXpath;

    @Value("${selenium.age-xpath}")
    private String ageXpath;

    /**
     * 뮤지컬 상세 정보를 가져오는 메서드
     * @param groupId
     */
    @Transactional
    public void fetchMusicalDetails(String groupId) {
        String url = goodsUrl + groupId;
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 페이지로 이동
            driver.get(url);

            // 페이지가 완전히 로드될 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(titleCssSelector)));

            // 뮤지컬 제목 가져오기
            WebElement titleElement = driver.findElement(By.cssSelector(titleCssSelector));
            String title = titleElement.getText().replace("뮤지컬", "").trim();

            // 뮤지컬 이미지 URL 가져오기
            WebElement imgElement = driver.findElement(By.cssSelector(imgCssSelector));
            String imageUrl = imgElement != null ? imgElement.getAttribute("src") : null;

            // 장소 가져오기
            WebElement locationElement = driver.findElement(By.xpath(locationXpath));
            String location = locationElement.getText().replaceAll("\\(.*?\\)", "").trim();

            // 공연 기간 가져오기
            WebElement periodElement = driver.findElement(By.xpath(periodXpath));
            String performancePeriod = periodElement.getText();
            String[] periodSplit = performancePeriod.split("~");
            String startDate = periodSplit[0].trim();
            String endDate = periodSplit[1].trim();

            // 공연 시간 가져오기
            WebElement runningTimeElement = driver.findElement(By.xpath(runningTimeXpath));
            Long runningTime = Long.parseLong(runningTimeElement.getText().replaceAll("\\(.*?\\)", "").replace("분", "").trim());

            // 관람 연령 가져오기
            WebElement ageElement = driver.findElement(By.xpath(ageXpath));
            String age = ageElement.getText();

            Musical musical = Musical.builder()
                    .interparkId(groupId)
                    .title(title)
                    .imageUrl(imageUrl)
                    .location(location)
                    .startDate(parseDate(startDate))
                    .endDate(parseDate(endDate))
                    .runningTime(runningTime)
                    .age(age)
                    .musicalActors(new ArrayList<>())
                    .showTimes(new ArrayList<>())
                    .build();

            musicalRepository.save(musical);

            /*
            // 뮤지컬 시간 가져오기
            WebElement showTimeElement = driver.findElement(By.xpath("//ul[@class='contentDetailList']/div"));
            String showTime = showTimeElement.getText();
            List<String> showTimes = parsingShowTime.parseShowTimes(showTime);
            Set<String> existingShowTimes = new HashSet<>();

            showTimes.forEach(show -> {
                String day = show.split(" ")[0];
                String time = show.split(" ")[1];
                String uniqueKey = day + " " + time;

                if (!existingShowTimes.contains(uniqueKey)) {
                    ShowTime st = ShowTime.builder()
                            .musical(musical)
                            .day(day)
                            .time(time)
                            .build();

                    musical.addShowTime(st);
                    existingShowTimes.add(uniqueKey); // 중복 방지용 키 추가
                }
            });
             */

            WebElement moreButton = driver.findElement(By.xpath("//a[@class='contentToggleBtn' and @aria-label='여닫기']"));
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("arguments[0].click();", moreButton);

            // 뮤지컬 캐스팅정보, 배우 정보 가져오기
            List<WebElement> castingItems = driver.findElements(By.xpath("//ul[@class='castingList']/li[@class='castingItem']"));

            for (WebElement item : castingItems) {
                String actorName = item.findElement(By.xpath(".//div[@class='castingInfo']/div[@class='castingName']")).getText();

                String roleName = item.findElement(By.xpath(".//div[@class='castingInfo']/div[@class='castingActor']")).getText();

                String actorPhotoUrl = item.findElement(By.xpath(".//div[@class='castingTop']//img[@class='castingImage']")).getAttribute("src");

                Actor actor = Actor.builder()
                        .name(actorName)
                        .actorImageUrl(actorPhotoUrl)
                        .musicalActors(new ArrayList<>())
                        .build();

                actorRepository.save(actor);

                MusicalActor musicalActor = MusicalActor.builder()
                        .actor(actor)
                        .roleName(roleName)
                        .isMainActor(true)
                        .musical(musical)
                        .build();

                musical.addMusicalActors(musicalActor);
            }

        } catch (Exception e) {
            log.error("Error fetching details for GroupId {}: {}", groupId, e.getMessage());
            throw e;
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