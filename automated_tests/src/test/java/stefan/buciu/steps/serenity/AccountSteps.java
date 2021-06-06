package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.AccountPage;

import static org.junit.Assert.assertEquals;

public class AccountSteps {

    private AccountPage accountPage;

    @Step
    public void clickNewAccount() {
        this.accountPage.clickNewAccount();
    }

    public boolean isAccountVisible(String money) {
        return this.accountPage.isAccountVisible(money);
    }

    @Step
    public void clickLogout() {
        this.accountPage.clickLogout();
    }

    @Step
    public void assertIsAccountVisible(String money, boolean expected) {
        assertEquals(expected, this.isAccountVisible(money));
    }

    @Step
    public void clickAccount(String money) {
        this.accountPage.clickAccount(money);
    }
}
