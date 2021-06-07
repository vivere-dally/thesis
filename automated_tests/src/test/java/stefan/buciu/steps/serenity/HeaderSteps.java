package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.HeaderPage;

import static org.junit.Assert.assertEquals;

public class HeaderSteps {

    private HeaderPage headerPage;

    @Step
    public boolean isAuthenticationTitleVisible() {
        return this.headerPage.isAuthenticationTitleVisible();
    }

    @Step
    public boolean isSignupTitleVisible() {
        return this.headerPage.isSignupTitleVisible();
    }

    @Step
    public boolean isAccountsTitleVisible() {
        return this.headerPage.isAccountsTitleVisible();
    }

    @Step
    public boolean isNewAccountTitleVisible() {
        return this.headerPage.isNewAccountTitleVisible();
    }

    @Step
    public boolean isAccountFeedTitleVisible() {
        return this.headerPage.isAccountFeedTitleVisible();
    }

    @Step
    public boolean isNewTransactionTitleVisible() {
        return this.headerPage.isNewTransactionTitleVisible();
    }

    @Step
    public void assertIsAuthenticationTitleVisible(boolean expected) {
        assertEquals(expected, this.isAuthenticationTitleVisible());
    }

    @Step
    public void assertIsSignupTitleVisible(boolean expected) {
        assertEquals(expected, this.isSignupTitleVisible());
    }

    @Step
    public void assertIsAccountsTitleVisible(boolean expected) {
        assertEquals(expected, this.isAccountsTitleVisible());
    }

    @Step
    public void assertIsNewAccountTitleVisible(boolean expected) {
        assertEquals(expected, this.isNewAccountTitleVisible());
    }

    @Step
    public void assertIsAccountFeedTitleVisible(boolean expected) {
        assertEquals(expected, this.isAccountFeedTitleVisible());
    }

    @Step
    public void assertIsNewTransactionTitleVisible(boolean expected) {
        assertEquals(expected, this.isNewTransactionTitleVisible());
    }
}
