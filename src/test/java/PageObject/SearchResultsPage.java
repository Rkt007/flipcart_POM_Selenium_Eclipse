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
     * Prints a limited number of product names at DEBUG and a concise INFO summary
     */
    public void printAllProductTitles() {
        if (productTitles == null) return;

        int total = productTitles.size();
        int limit = Math.min(total, 10); // limit number printed to avoid huge logs

        for (int i = 0; i < limit; i++) {
            try {
                String text = productTitles.get(i).getText();
                logger.debug("Product[{}]: {}", i, text);
            } catch (Exception e) {
                logger.debug("Failed to read product title at index {}: {}", i, e.getMessage());
            }
        }

        if (total > limit) {
            logger.info("Displayed {} of {} product titles (see debug logs for details)", limit, total);
        } else {
            logger.info("Displayed all {} product titles", total);
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