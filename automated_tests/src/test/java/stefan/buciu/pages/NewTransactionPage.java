package stefan.buciu.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebElement;

public class NewTransactionPage extends PageObject {

    @FindBy(xpath = "//ion-input[@id='message-text-input']/input")
    private WebElement messageInput;

    @FindBy(xpath = "//ion-input[@id='value-number-input']/input")
    private WebElement valueInput;

    @FindBy(id = "create-submit-button")
    private WebElement createButton;

    public void inputMessage(String message) {
        this.messageInput.sendKeys(message);
    }

    public void inputValue(String value) {
        this.valueInput.sendKeys(value);
    }

    public void clickCreate() {
        this.createButton.click();
    }
}
