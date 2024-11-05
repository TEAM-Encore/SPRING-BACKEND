package encore.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EncoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EncoreServerApplication.class, args);
        System.out.println("Encore server is running!");
    }
}
