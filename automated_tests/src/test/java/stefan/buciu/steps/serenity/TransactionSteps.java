package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.TransactionPage;

import static org.junit.Assert.assertEquals;

public class TransactionSteps {

    private TransactionPage transactionPage;

    @Step
    public void clickNewTransaction() {
        this.transactionPage.clickNewTransaction();
    }

    @Step
    public void goBack() {
        this.transactionPage.clickGoBack();
    }

    @Step
    public boolean isTransactionVisible(String value) {
        return this.transactionPage.verticalScroll() && this.transactionPage.isTransactionVisible(value);
    }

    @Step
    public void assertIsTransactionVisible(String value, boolean expected) {
        assertEquals(expected, this.isTransactionVisible(value));
    }
}
