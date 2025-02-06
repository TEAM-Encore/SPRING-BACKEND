package encore.server.global.config;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsoupConfig {
    // Jsoup 연결 메서드
    public Connection jsoupConnection(String url) throws Exception {
        return Jsoup.connect(url);
    }
}