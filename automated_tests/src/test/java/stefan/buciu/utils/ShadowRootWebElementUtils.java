package stefan.buciu.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ShadowRootWebElementUtils {
    public static WebElement get(WebDriver webDriver, WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        return (WebElement) executor.executeScript("return arguments[0].shadowRoot", webElement);
    }
}
