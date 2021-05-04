package stefan.buciu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class ThesisApplication {
    public static void main(String[] args) {
        System.getenv().entrySet().forEach(System.out::println);
        SpringApplication.run(ThesisApplication.class, args);
    }
}
