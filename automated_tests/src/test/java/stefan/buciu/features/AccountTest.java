package stefan.buciu.features;


import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import stefan.buciu.steps.serenity.*;
import stefan.buciu.utils.AccountTestState;
import stefan.buciu.utils.DataGenerator;

@RunWith(SerenityRunner.class)
public class AccountTest {

    @Managed
    private WebDriver webDriver;

    @Steps
    private LoginSteps loginSteps;
    @Steps
    private HeaderSteps headerSteps;
    @Steps
    private SignupSteps signupSteps;
    @Steps
    private AccountSteps accountSteps;
    @Steps
    private NewAccountSteps newAccountSteps;

    private final AccountTestState accountTestState = AccountTestState.getInstance();

    @Before
    public void before() {
        if (!accountTestState.credential.isCreated) {
            // Navigate to Signup Page
            loginSteps.openPage();
            loginSteps.clickSignup();
            headerSteps.assertIsSignupTitleVisible(true);

            // Signup
            signupSteps.signup(accountTestState.credential.username, accountTestState.credential.password);
            headerSteps.assertIsAuthenticationTitleVisible(true);

            accountTestState.credential.isCreated = true;
        }

        // Login
        loginSteps.openPage();
        loginSteps.login(accountTestState.credential.username, accountTestState.credential.password);
        headerSteps.assertIsAccountsTitleVisible(true);

        // Navigate to NewAccount Page
        accountSteps.clickNewAccount();
        headerSteps.assertIsNewAccountTitleVisible(true);
    }

    @Test
    public void createAccount_success() {
        String money = DataGenerator.money(true);
        String monthlyIncome = DataGenerator.money(true);

        // Create Account
        newAccountSteps.createAccount(money, monthlyIncome);
        headerSteps.assertIsAccountsTitleVisible(true);

        // Check if Account was created
        accountSteps.assertIsAccountVisible(money, true);

        // Logout
        accountSteps.clickLogout();
        headerSteps.isAuthenticationTitleVisible();
    }

    @Test
    public void createAccount_noInput() {
        newAccountSteps.clickCreate();
        headerSteps.assertIsNewAccountTitleVisible(true);
    }

    @Test
    public void createAccount_badInput() {
        String money = DataGenerator.money(false);
        String monthlyIncome = DataGenerator.money(false);

        // Create Account
        newAccountSteps.createAccount(money, monthlyIncome);
        headerSteps.assertIsNewAccountTitleVisible(true);
    }
}
