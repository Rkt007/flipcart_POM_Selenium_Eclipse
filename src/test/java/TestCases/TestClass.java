package TestCases;

import java.time.Duration;
import java.util.Set;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import PageObject.CartPage;
import PageObject.HomePage;
import PageObject.LoginPage;
import PageObject.SearchResultsPage;
import PageObject.ProductDetailsPage;
import Utilities.TestListener;

@Listeners({ TestListener.class })
public class TestClass extends BaseClass {

    private static final Logger logger = LoggerFactory.getLogger(TestClass.class);

    @Test
    public void verifySearchAndSelectProduct() {

        // ===============================
        // 1️⃣ Close Login Popup (if present)
        // ===============================
        LoginPage login = new LoginPage(driver);
        login.closePopupIfPresent();

        // ===============================
        // 2️⃣ Search Product
        // ===============================
        HomePage home = new HomePage(driver);
        home.searchProduct("iphone");

        // ===============================
        // 3️⃣ Handle Search Results
        // ===============================
        SearchResultsPage searchResult = new SearchResultsPage(driver);

        int total = searchResult.getTotalResults();
        logger.info("Total products found: {}", total);

        searchResult.printAllProductTitles();
        searchResult.clickFirstProduct();

        // ===============================
        // 4️⃣ Switch to Product Tab
        // ===============================
        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();

        for (String window : allWindows) {
            if (!window.equals(parentWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // ===============================
        // 5️⃣ Wait for Product Page Load
        // ===============================
        new WebDriverWait(driver, Duration.ofSeconds(25))
                .until(d -> driver.getCurrentUrl().toLowerCase().contains("iphone"));

        // ===============================
        // 6️⃣ Product Page Actions (try multiple strategies)
        // ===============================
        ProductDetailsPage pdp = new ProductDetailsPage(driver);
        CartPage cart = new CartPage(driver);

        boolean actionSucceeded = false;

        // Strategy A: Try Buy Now first
        try {
            logger.info("Attempting strategy A: clickBuyNow on product page");
            cart.clickBuyNow();
            actionSucceeded = true;
            logger.info("Strategy A succeeded: clicked Buy Now");
        } catch (Exception e) {
            logger.warn("Strategy A (clickBuyNow) failed: {}", e.getMessage());
        }

        // Strategy B: try ProductDetailsPage.addToCart()
        if (!actionSucceeded) {
            try {
                logger.info("Attempting strategy B: ProductDetailsPage.addToCart()");
                pdp.addToCart();
                actionSucceeded = true;
                logger.info("Strategy B succeeded: ProductDetailsPage.addToCart()");
            } catch (Exception e) {
                logger.warn("Strategy B failed: {}", e.getMessage());
            }
        }

        // Strategy C: fallback to CartPage.addToCart()
        if (!actionSucceeded) {
            try {
                logger.info("Attempting strategy C: CartPage.addToCart() fallback");
                cart.addToCart();
                actionSucceeded = true;
                logger.info("Strategy C succeeded: CartPage.addToCart()");
            } catch (Exception e) {
                logger.error("Strategy C failed: {}", e.getMessage(), e);
                throw e; // all strategies failed — rethrow so listener captures artifacts
            }
        }

        // Close popup if present and proceed (if action succeeded)
        if (actionSucceeded) {
            cart.closeLoginPopupIfPresent();
            try {
                logger.info("Attempting to click Buy Now after add-to-cart action (if available)");
                cart.clickBuyNow();
            } catch (Exception e) {
                logger.warn("Clicking Buy Now after add failed or not present: {}", e.getMessage());
            }
        }

        logger.info("Current URL After Buy/Add actions: {}", driver.getCurrentUrl());
    }
}