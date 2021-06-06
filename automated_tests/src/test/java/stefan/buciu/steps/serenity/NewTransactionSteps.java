package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.NewTransactionPage;

public class NewTransactionSteps {

    private NewTransactionPage newTransactionPage;

    @Step
    public void inputMessage(String message) {
        this.newTransactionPage.inputMessage(message);
    }

    @Step
    public void inputValue(String value) {
        this.newTransactionPage.inputValue(value);
    }

    @Step
    public void clickCreate() {
        this.newTransactionPage.clickCreate();
    }

    @Step
    public void newTransaction(String message, String value) {
        this.inputMessage(message);
        this.inputValue(value);
        this.clickCreate();
    }
}
