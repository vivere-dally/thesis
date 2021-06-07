package stefan.buciu.steps.serenity;

import net.thucydides.core.annotations.Step;
import stefan.buciu.pages.SignupPage;

public class SignupSteps {

    private SignupPage signupPage;

    @Step
    public void inputUsername(String username) {
        this.signupPage.inputUsername(username);
    }

    @Step
    public void inputPassword(String password) {
        this.signupPage.inputPassword(password);
    }

    @Step
    public void clickSignup() {
        this.signupPage.clickSignup();
    }

    @Step
    public void signup(String username, String password) {
        this.inputUsername(username);
        this.inputPassword(password);
        this.clickSignup();
    }
}
