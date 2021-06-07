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
public class SignupTest {

    @Managed
    private WebDriver webDriver;

    @Steps
    private LoginSteps loginSteps;
    @Steps
    private HeaderSteps headerSteps;
    @Steps
    private SignupSteps signupSteps;

    @Test
    public void signup_fail_noInput() {
        loginSteps.openPage();
        loginSteps.clickSignup();
        headerSteps.assertIsSignupTitleVisible(true);

        signupSteps.clickSignup();
        headerSteps.assertIsAuthenticationTitleVisible(false);
    }

    @Test
    public void signup_fail_badUsername() {
        String username = DataGenerator.username(false);
        String password = DataGenerator.password(true);

        loginSteps.openPage();
        loginSteps.clickSignup();
        headerSteps.assertIsSignupTitleVisible(true);

        signupSteps.signup(username, password);
        headerSteps.assertIsAuthenticationTitleVisible(false);
    }

    @Test
    public void signup_fail_badPassword() {
        String username = DataGenerator.username(true);
        String password = DataGenerator.password(false);

        loginSteps.openPage();
        loginSteps.clickSignup();
        headerSteps.assertIsSignupTitleVisible(true);
        
        signupSteps.signup(username, password);
        headerSteps.assertIsAuthenticationTitleVisible(false);
    }
}
