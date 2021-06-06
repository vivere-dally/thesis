package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.NewAccountPage;

public class NewAccountSteps {

    private NewAccountPage newAccountPage;

    @Step
    public void inputMoney(String money) {
        this.newAccountPage.inputMoney(money);
    }

    @Step
    public void inputMonthlyIncomeInput(String monthlyIncome) {
        this.newAccountPage.inputMonthlyIncomeInput(monthlyIncome);
    }

    @Step
    public void clickCreate() {
        this.newAccountPage.clickCreate();
    }

    @Step
    public void createAccount(String money, String monthlyIncome) {
        this.inputMoney(money);
        this.inputMonthlyIncomeInput(monthlyIncome);
        this.clickCreate();
    }
}
