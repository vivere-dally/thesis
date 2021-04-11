package stefan.buciu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:/myconfig.yml", factory = YamlPropertySourceFactory.class)
@Configuration
public class TestPropSource {
}
