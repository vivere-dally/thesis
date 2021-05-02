package stefan.buciu.environment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/details.properties")
@Getter
public class AppDetails {
    @Value("${PROJECT_ARTIFACT_ID}")
    private String projectArtifactId;

    @Value("${PROJECT_VERSION}")
    private String projectVersion;

    @Value("${PROJECT_DESCRIPTION}")
    private String projectDescription;

    @Value("${PROJECT_OWNER}")
    private String projectOwner;

    @Value("${PROJECT_OWNER_EMAIL}")
    private String projectOwnerEmail;

    @Value("${PROJECT_SCM_URL}")
    private String projectUrl;
}
