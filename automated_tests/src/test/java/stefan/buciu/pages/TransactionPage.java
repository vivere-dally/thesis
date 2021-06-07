package stefan.buciu.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import stefan.buciu.utils.WebElementUtils;

import java.util.concurrent.TimeUnit;

public class TransactionPage extends PageObject {

    @FindBy(id = "new_transaction-button")
    private WebElement newTransactionButton;

    @FindBy(xpath = "//ion-back-button[@text='Accounts']")
    private WebElement backButton;

    public void clickNewTransaction() {
        this.newTransactionButton.click();
    }

    public void clickGoBack() {
        this.backButton.click();
    }

    public boolean verticalScroll() {
        if (!WebElementUtils.exists(By.id("account_feed_page-ion_content"), getDriver(), null)) {
            return false;
        }

        getDriver().manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        String script = "(await document.querySelector('#account_feed_page-ion_content').getScrollElement()).scrollBy(0, 50);" +
                "window.setTimeout(arguments[arguments.length - 1], 500);";
        executor.executeAsyncScript(script);

        return true;
    }

    public boolean isTransactionVisible(String value) {
        String xpath = String.format("//ion-label/h3/span[text()[contains(.,'RON')] and text()[contains(.,'%s')]]", value);
        return WebElementUtils.exists(By.xpath(xpath), getDriver(), null);
    }
}
