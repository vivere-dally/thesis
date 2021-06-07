package stefan.buciu.pages;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import stefan.buciu.utils.WebElementUtils;

public class HeaderPage extends PageObject {

    public boolean isAuthenticationTitleVisible() {
        return WebElementUtils.exists(By.id("authentication-title"), getDriver(), null);
    }

    public boolean isSignupTitleVisible() {
        return WebElementUtils.exists(By.id("signup-title"), getDriver(), null);
    }

    public boolean isAccountsTitleVisible() {
        return WebElementUtils.exists(By.id("accounts-title"), getDriver(), null);
    }

    public boolean isNewAccountTitleVisible() {
        return WebElementUtils.exists(By.id("new_account-title"), getDriver(), null);
    }

    public boolean isAccountFeedTitleVisible() {
        return WebElementUtils.exists(By.id("account_feed-title"), getDriver(), null);
    }

    public boolean isNewTransactionTitleVisible() {
        return WebElementUtils.exists(By.id("new_transaction-title"), getDriver(), null);
    }
}
