package Utilities;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

    // Default wait time in seconds
    private static final int DEFAULT_TIMEOUT = 15;

    public static WebElement waitForVisibility(WebDriver driver, WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return waitForVisibility(driver, element, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return waitForClickable(driver, element, DEFAULT_TIMEOUT);
    }

    public static boolean waitForUrlContains(WebDriver driver, String fraction, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.urlContains(fraction));
    }

    public static boolean waitForUrlContains(WebDriver driver, String fraction) {
        return waitForUrlContains(driver, fraction, DEFAULT_TIMEOUT);
    }
}
