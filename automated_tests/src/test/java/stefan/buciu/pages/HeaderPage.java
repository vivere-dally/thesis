package stefan.buciu.pages;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import stefan.buciu.utils.WebElementUtils;

public class HeaderPage extends PageObject {

    public boolean isAuthenticationTitleVisible() {
        return WebElementUtils.exists(By.id("authentication-title"), getDriver(), null);
    }

    public boolean isAccountsTitleVisible() {
        return WebElementUtils.exists(By.id("accounts-title"), getDriver(), null);
    }
}
