package stefan.buciu.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class WebElementUtils {
    public static boolean exists(By by, WebDriver webDriver, Long implicitlyWaitInSeconds) {
        if (implicitlyWaitInSeconds == null) {
            implicitlyWaitInSeconds = 3L;
        }

        try {
            webDriver.manage().timeouts().implicitlyWait(implicitlyWaitInSeconds, TimeUnit.SECONDS);
            webDriver.findElement(by);
            return true;
        } catch (NoSuchElementException ignored) {
        }

        return false;
    }
}
