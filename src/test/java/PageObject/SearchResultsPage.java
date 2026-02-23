package PageObject;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * SearchResultsPage
 * -----------------
 * This class represents Flipkart search results page.
 * It handles product listing actions.
 */

public class SearchResultsPage {

    private WebDriver ldriver;
    private static final Logger logger = LoggerFactory.getLogger(SearchResultsPage.class);

    // Constructor
    public SearchResultsPage(WebDriver rdriver) {
        ldriver = rdriver;
        PageFactory.initElements(rdriver, this);
    }

    // Stable locator for product titles (better than dynamic class)
    @FindBy(xpath = "//div[contains(@class,'_4rR01T') or contains(@class,'ZFwe0M') or //*[contains(@class,'_2kHMtA')]]")
    private List<WebElement> productTitles; 
    
    @FindBy(xpath = "(//a[contains(@href,'/p/')])[1]")
    private WebElement firstproduct; 

    /*
     * getTotalResults()
     * -----------------
     * Returns total number of products displayed
     */
    public int getTotalResults() {
        return productTitles == null ? 0 : productTitles.size();
    }


    /*
     * printAllProductTitles()
     * -----------------------
     * Prints all product names in console
     */
    public void printAllProductTitles() {
        if (productTitles == null) return;
        for (WebElement el : productTitles) {
            logger.info(el.getText());
        }
    }
    
    /*
     * clickFirstProduct()
     * -------------------
     * Clicks on first product from search results
     */
    public void clickFirstProduct() {
        try {
            if (productTitles != null && productTitles.size() > 0) {
                productTitles.get(0).click();
            } else if (firstproduct != null) {
                firstproduct.click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click first product", e);
        }
    }
}