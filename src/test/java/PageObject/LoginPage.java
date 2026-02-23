package PageObject;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private WebDriver ldriver;
    private WebDriverWait wait;

    public LoginPage(WebDriver rdriver) {
        ldriver = rdriver;
        wait = new WebDriverWait(ldriver, Duration.ofSeconds(10));
        PageFactory.initElements(rdriver, this);
    }

    // Username field - relaxed locator to handle variations across pages
    @FindBy(xpath = "//input[@type='text' or @type='email' or contains(@placeholder,'Phone') or contains(@class,'_2IX')]")
    private WebElement username;

    // Password field
    @FindBy(xpath = "//input[@type='password']")
    private WebElement password;

    // Login button - match common text variants
    @FindBy(xpath = "//button[contains(.,'Login') or contains(.,'Log in') or contains(.,'LOGIN')]")
    private WebElement loginBtn;

    // Popup close button (optional)
    @FindBy(xpath = "//button[contains(text(),'✕') or contains(@class,'_2doB4z') or contains(.,'✕')]")
    private List<WebElement> popupCloseBtn;

    // ============================
    // Action Methods
    // ============================

    public void closePopupIfPresent() {
        if (popupCloseBtn != null && popupCloseBtn.size() > 0) {
            try {
                popupCloseBtn.get(0).click();
            } catch (Exception e) {
                // swallow; popup might disappear between check and click
            }
        }
    }

    public void login(String user, String pass) {

        // Defensive wait: some pages may show different input fields; guard against timeouts
        try {
            wait.until(ExpectedConditions.visibilityOf(username));

            username.clear();
            username.sendKeys(user);

            password.clear();
            password.sendKeys(pass);

            loginBtn.click();
        } catch (Exception e) {
            // If login elements are not present, surface a clear runtime exception for easier debugging
            throw new RuntimeException("Login failed - login form elements not found or not interactable", e);
        }
    }
}