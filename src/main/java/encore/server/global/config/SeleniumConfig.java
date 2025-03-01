package encore.server.global.config;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    private WebDriver driver;

    @Value("${selenium.chrome-driver-path}")
    private String chromeDriverPath;

    /**
     * Selenium WebDriver 초기화
     */
    @PostConstruct
    public void initDriver() {
        try {
            // WebDriver 설정
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // 브라우저 창 없이 실행
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-popup-blocking"); // 팝업 안 띄움

            this.driver = new ChromeDriver(options);
        } catch (Exception e) {
            // 예외 처리
            System.err.println("Error initializing Selenium WebDriver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Bean
    public WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Please check the Selenium configuration.");
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}