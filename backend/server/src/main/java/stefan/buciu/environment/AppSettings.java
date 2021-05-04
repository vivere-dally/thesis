package stefan.buciu.environment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:/dev.appSettings.properties")
@Getter
@Slf4j
public class AppSettings {
    private final String securityKey;

    private final String securityRequiredAuthorizationHeader;

    private final String securityAccessTokenTypeHeaderName;
    private final String securityAccessTokenTypeHeaderValue;

    private final String securityAccessTokenHeaderName;
    private final Long securityAccessTokenTtlMillis;

    private final String securityRefreshTokenHeaderName;
    private final Long securityRefreshTokenTtlMillis;

    @Getter(AccessLevel.NONE)
    private final Environment environment;

    public AppSettings(Environment environment) {
        this.environment = environment;

        this.securityKey = getAppSetting("SECURITY_KEY");

        this.securityRequiredAuthorizationHeader = getAppSetting("SECURITY_REQUIRED_AUTHORIZATION_HEADER");

        this.securityAccessTokenTypeHeaderName = getAppSetting("SECURITY_ACCESS_TOKEN_TYPE_HEADER_NAME");
        this.securityAccessTokenTypeHeaderValue = getAppSetting("SECURITY_ACCESS_TOKEN_TYPE_HEADER_VALUE");

        this.securityAccessTokenHeaderName = getAppSetting("SECURITY_ACCESS_TOKEN_HEADER_NAME");
        this.securityAccessTokenTtlMillis = Long.parseLong(getAppSetting("SECURITY_ACCESS_TOKEN_TTL_MILLIS"));

        this.securityRefreshTokenHeaderName = getAppSetting("SECURITY_REFRESH_TOKEN_HEADER_NAME");
        this.securityRefreshTokenTtlMillis = Long.parseLong(getAppSetting("SECURITY_REFRESH_TOKEN_TTL_MILLIS"));
    }

    private String getAppSetting(String s) {
        if (System.getenv().containsKey("APPSETTING_" + s)) {
            log.info("Loading prod app setting {}", s);
            return System.getenv("APPSETTING_" + s);
        }

        log.info("Loading dev app setting {}", s);
        return environment.getProperty(s);
    }
}
