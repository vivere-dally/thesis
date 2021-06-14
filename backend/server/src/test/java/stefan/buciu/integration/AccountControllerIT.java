package stefan.buciu.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stefan.buciu.domain.model.CurrencyType;
import stefan.buciu.domain.model.dto.AccountDTO;
import stefan.buciu.integration.utils.Authenticator;

import java.math.BigDecimal;
import java.net.URI;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountControllerIT {
    @Value("${spring.rest.base-url}")
    private String baseUrl;
    @Value("${spring.rest.signup-path}")
    private String signupUrl;
    @Value("${spring.rest.login-path}")
    private String loginUrl;
    @Value("${spring.rest.account-path}")
    private String accountUrl;

    private RestTemplate restTemplate;

    private final Authenticator authenticator = Authenticator.getInstance();
    private MultiValueMap<String, String> headers;

    @Before
    public void before() {
        restTemplate = new RestTemplate();

        authenticator.signup(baseUrl, signupUrl);
        headers = authenticator.login(baseUrl, loginUrl);
    }

    @Test
    public void save_ok() {
        AccountDTO accountDTO = new AccountDTO(0, BigDecimal.valueOf(1234.56), BigDecimal.valueOf(1234.56), CurrencyType.RON);
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("user")
                .pathSegment(String.valueOf(authenticator.getUserId()))
                .pathSegment(accountUrl)
                .build()
                .toUri();

        HttpEntity<AccountDTO> requestEntity = new HttpEntity<>(accountDTO, headers);
        ResponseEntity<AccountDTO> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, AccountDTO.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(accountDTO.getMoney(), responseEntity.getBody().getMoney());
        assertEquals(accountDTO.getMonthlyIncome(), responseEntity.getBody().getMonthlyIncome());
        assertEquals(accountDTO.getCurrency(), responseEntity.getBody().getCurrency());
    }
}
