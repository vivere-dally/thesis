package stefan.buciu.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebElement;

public class NewAccountPage extends PageObject {

    @FindBy(xpath = "//ion-input[@id='money-number-input']/input")
    private WebElement moneyInput;

    @FindBy(xpath = "//ion-input[@id='monthly_income-number-input']/input")
    private WebElement monthlyIncomeInput;

    @FindBy(id = "create-submit-button")
    private WebElement createButton;

    public void inputMoney(String money) {
        this.moneyInput.sendKeys(money);
    }

    public void inputMonthlyIncomeInput(String monthlyIncome) {
        this.monthlyIncomeInput.sendKeys(monthlyIncome);
    }
    
    public void clickCreate() {
        this.createButton.click();
    }
}
