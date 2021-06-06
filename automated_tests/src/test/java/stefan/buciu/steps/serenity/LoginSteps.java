package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.LoginPage;

public class LoginSteps {

    private LoginPage loginPage;

    @Step
    public void openPage() {
        this.loginPage.open();
    }

    @Step
    public void inputUsername(String username) {
        this.loginPage.inputUsername(username);
    }

    @Step
    public void inputPassword(String password) {
        this.loginPage.inputPassword(password);
    }

    @Step
    public void clickLogin() {
        this.loginPage.clickLogin();
    }

    @Step
    public void clickSignup() {
        this.loginPage.clickSignup();
    }

    @Step
    public void login(String username, String password) {
        this.inputUsername(username);
        this.inputPassword(password);
        this.clickLogin();
    }
}
