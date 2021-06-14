package stefan.buciu.integration.utils;

import com.github.javafaker.Faker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stefan.buciu.domain.model.dto.UserAuthenticatedDTO;
import stefan.buciu.domain.model.dto.UserLoginDTO;
import stefan.buciu.domain.model.dto.UserSignupDTO;

import java.net.URI;
import java.util.regex.Pattern;

public class Authenticator {
    private static Authenticator instance = null;

    private final Faker faker = Faker.instance();
    private final Pattern USR_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$", Pattern.CASE_INSENSITIVE);
    private final Pattern PSW_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})");
    private final RestTemplate restTemplate = new RestTemplate();

    private UserAuthenticatedDTO userAuthenticated;

    private boolean isRegistered;
    private final String usr, psw;

    public Authenticator() {
        this.isRegistered = false;
        this.usr = this.username();
        this.psw = this.password();
    }

    public static Authenticator getInstance() {
        if (instance == null) {
            instance = new Authenticator();
        }

        return instance;
    }

    public void signup(String baseUrl, String signupUrl) {
        if (this.isRegistered) {
            return;
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(signupUrl)
                .build()
                .toUri();

        this.restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(new UserSignupDTO(usr, psw)),
                String.class);
        this.isRegistered = true;
    }

    public MultiValueMap<String, String> login(String baseUrl, String loginUrl) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(loginUrl)
                .build()
                .toUri();

        HttpEntity<UserAuthenticatedDTO> response = this.restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(new UserLoginDTO(usr, psw)),
                UserAuthenticatedDTO.class);
        userAuthenticated = response.getBody();

        String accessToken = response.getHeaders().getFirst("accessToken");
        String tokenType = response.getHeaders().getFirst("tokenType");

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", String.format("%s %s", tokenType, accessToken));
        return headers;
    }

    public long getUserId() {
        return this.userAuthenticated.getId();
    }

    private String password() {
        String psw;
        do {
            psw = faker.internet().password(8, 50, true, true, true);
        } while (!PSW_PATTERN.matcher(psw).find());

        return psw;
    }

    private String username() {
        String usr;
        do {
            usr = faker.internet().password(3, 16);
        } while (!USR_PATTERN.matcher(usr).find());

        return usr;
    }
}
