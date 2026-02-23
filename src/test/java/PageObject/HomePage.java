package PageObject;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    private WebDriver ldriver;
    private WebDriverWait wait;

    public HomePage(WebDriver rdriver) {
        ldriver = rdriver;
        wait = new WebDriverWait(ldriver, Duration.ofSeconds(10));
        PageFactory.initElements(rdriver, this);
    }

    // Search input box
    @FindBy(name = "q")
    private WebElement searchBox;

    /*
     * searchProduct()
     * ----------------
     * Enters product name in search field
     * and submits the search.
     */
    public void searchProduct(String itemName) {

        wait.until(ExpectedConditions.visibilityOf(searchBox));

        searchBox.clear();
        searchBox.sendKeys(itemName);
        searchBox.submit();
    }
}