package stefan.buciu.features;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import stefan.buciu.steps.serenity.*;
import stefan.buciu.utils.DataGenerator;
import stefan.buciu.utils.TransactionTestState;

@RunWith(SerenityRunner.class)
public class TransactionTest {

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
    @Steps
    private TransactionSteps transactionSteps;
    @Steps
    private NewTransactionSteps newTransactionSteps;

    private final TransactionTestState transactionTestState = TransactionTestState.getInstance();

    @Before
    public void before() {
        if (!transactionTestState.credential.isCreated) {
            // Navigate to Signup Page
            loginSteps.openPage();
            loginSteps.clickSignup();
            headerSteps.assertIsSignupTitleVisible(true);

            // Signup
            signupSteps.signup(transactionTestState.credential.username, transactionTestState.credential.password);
            headerSteps.assertIsAuthenticationTitleVisible(true);
        }

        // Login
        loginSteps.openPage();
        loginSteps.login(transactionTestState.credential.username, transactionTestState.credential.password);
        headerSteps.assertIsAccountsTitleVisible(true);

        if (!transactionTestState.credential.isCreated) {
            // Navigate to NewAccount Page
            accountSteps.clickNewAccount();
            headerSteps.assertIsNewAccountTitleVisible(true);

            // Create Account
            newAccountSteps.createAccount(transactionTestState.accountMoney, transactionTestState.accountMonthlyIncome);
            headerSteps.assertIsAccountsTitleVisible(true);

            // Check if Account was created
            accountSteps.assertIsAccountVisible(transactionTestState.accountMoney, true);

            transactionTestState.credential.isCreated = true;
        }

        // Navigate to Transaction Page
        accountSteps.clickAccount(transactionTestState.accountMoney);
        headerSteps.assertIsAccountFeedTitleVisible(true);

        // Navigate to New Transaction Page
        transactionSteps.clickNewTransaction();
        headerSteps.assertIsNewTransactionTitleVisible(true);
    }

    @Test
    public void createTransaction_success() {
        String message = DataGenerator.message();
        String value = DataGenerator.money(true);

        // Create New Transaction
        newTransactionSteps.newTransaction(message, value);
        headerSteps.assertIsAccountFeedTitleVisible(true);

        // Check if the new Transaction is visible
        transactionSteps.assertIsTransactionVisible(value, true);

        // Go back to Accounts page
        transactionSteps.goBack();

        // Logout
        accountSteps.clickLogout();
        headerSteps.isAuthenticationTitleVisible();
    }
}
