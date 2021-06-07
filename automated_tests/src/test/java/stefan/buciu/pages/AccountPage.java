package stefan.buciu.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import stefan.buciu.utils.WebElementUtils;

public class AccountPage extends PageObject {

    @FindBy(id = "new_account-button")
    private WebElement newAccountButton;

    @FindBy(id = "logout-button")
    private WebElement logoutButton;

    private final String accountListItemXPathFormat = "//ion-label[text()[contains(.,'RON')] and text()[contains(.,'%s')]]";

    public void clickNewAccount() {
        this.newAccountButton.click();
    }

    public boolean isAccountVisible(String money) {
        String xpath = String.format(accountListItemXPathFormat, money);
        return WebElementUtils.exists(By.xpath(xpath), getDriver(), null);
    }

    public void clickAccount(String money) {
        String xpath = String.format(accountListItemXPathFormat, money);
        getDriver().findElement(By.xpath(xpath)).click();
    }
    
    public void clickLogout() {
        this.logoutButton.click();
    }
}
