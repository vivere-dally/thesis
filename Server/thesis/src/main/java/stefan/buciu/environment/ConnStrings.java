package stefan.buciu.environment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:/dev.connStrings.properties")
@Getter
@Slf4j
public class ConnStrings {

    @Getter(AccessLevel.NONE)
    private final Environment environment;

    public ConnStrings(Environment environment) {
        this.environment = environment;
    }

    private String getConnString(String s, ConnStringType connStringType) {
        if (System.getenv().containsKey(connStringType.toString() + "_" + s)) {
            log.info("Loading prod conn str {}", s);
            return System.getenv(connStringType.toString() + "_" + s);
        }

        log.info("Loading dev conn str {}", s);
        return environment.getProperty(s);
    }
}
