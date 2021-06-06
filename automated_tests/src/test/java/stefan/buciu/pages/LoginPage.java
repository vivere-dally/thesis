package stefan.buciu.pages;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebElement;

@DefaultUrl("http://localhost:5002/login")
public class LoginPage extends PageObject {

    @FindBy(xpath = "//ion-input[@id='username-text-input']/input")
    private WebElement usernameInput;

    @FindBy(xpath = "//ion-input[@id='password-password-input']/input")
    private WebElement passwordInput;

    @FindBy(id = "login-submit-button")
    private WebElement loginButton;

    @FindBy(id = "signup-button")
    private WebElement signupButton;

    public void inputUsername(String username) {
        this.usernameInput.sendKeys(username);
    }

    public void inputPassword(String password) {
        this.passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        this.loginButton.click();
    }

    public void clickSignup() {
        this.signupButton.click();
    }
}
