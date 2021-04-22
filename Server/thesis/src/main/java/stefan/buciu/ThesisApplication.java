package stefan.buciu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class ThesisApplication {
    public static void main(String[] args) {
        Map<String, String> envVars  = System.getenv();
        envVars.entrySet().forEach(System.out::println);

        SpringApplication.run(ThesisApplication.class, args);
    }
}
