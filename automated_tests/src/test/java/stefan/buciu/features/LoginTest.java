package stefan.buciu.features;

import net.serenitybdd.junit.runners.SerenityParameterizedRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(SerenityParameterizedRunner.class)
@UseTestDataFrom("src/test/resources/loginData.csv")
public class LoginTest {

    @Managed
    private WebDriver webDriver;

    private String username, password;
    private Boolean valid;

    @Test
    public void login_success() {

    }
}
