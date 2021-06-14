package stefan.buciu.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import stefan.buciu.integration.utils.Authenticator;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerIT {
    @Value("${spring.rest.base-url}")
    private String baseUrl;
    @Value("${spring.rest.signup-path}")
    private String signupUrl;
    @Value("${spring.rest.login-path}")
    private String loginUrl;

    private final Authenticator authenticator = Authenticator.getInstance();

    @Test
    public void signup_ok() {
        this.authenticator.signup(baseUrl, signupUrl);
        assertTrue(true);
    }

    @Test
    public void login_ok() {
        this.authenticator.signup(baseUrl, signupUrl);
        MultiValueMap<String, String> headers = this.authenticator.login(baseUrl, loginUrl);
        assertTrue(headers.containsKey("Authorization"));
    }
}
