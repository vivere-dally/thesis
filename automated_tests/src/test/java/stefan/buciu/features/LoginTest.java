package stefan.buciu.features;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import stefan.buciu.steps.serenity.HeaderSteps;
import stefan.buciu.steps.serenity.LoginSteps;
import stefan.buciu.steps.serenity.SignupSteps;
import stefan.buciu.utils.DataGenerator;

@RunWith(SerenityRunner.class)
public class LoginTest {

    @Managed
    private WebDriver webDriver;

    @Steps
    private LoginSteps loginSteps;
    @Steps
    private HeaderSteps headerSteps;
    @Steps
    private SignupSteps signupSteps;

    @Test
    public void login_success() {
        String username = DataGenerator.username(true);
        String password = DataGenerator.password(true);

        loginSteps.openPage();
        loginSteps.clickSignup();
        headerSteps.assertIsSignupTitleVisible(true);

        signupSteps.signup(username, password);
        headerSteps.assertIsAuthenticationTitleVisible(true);

        loginSteps.login(username, password);
        headerSteps.assertIsAccountsTitleVisible(true);
    }

    @Test
    public void login_failed() {
        String username = DataGenerator.username(false);
        String password = DataGenerator.password(false);

        loginSteps.openPage();
        loginSteps.login(username, password);
        headerSteps.assertIsAccountsTitleVisible(false);
        headerSteps.assertIsAuthenticationTitleVisible(true);
    }
}
