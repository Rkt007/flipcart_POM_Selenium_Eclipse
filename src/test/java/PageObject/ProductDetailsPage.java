package PageObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utilities.WaitUtils;

public class ProductDetailsPage {

    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsPage.class);

    WebDriver ldriver;
    WebDriverWait wait;

    private static final By BY_PRODUCT_NAME = By.xpath("//span[@class='B_NuCI'] | //h1 | //div[contains(@class,'_2-f4')]//h1");
    private static final By BY_PRICE = By.xpath("//div[contains(@class,'_30jeq3')][1] | //span[contains(.,'₹')][1] | //div[contains(@class,'_1vC4OE')]");
    private static final By BY_RATINGS = By.xpath("//div[contains(@class,'_3LWZlK') or contains(@class,'_2_R_DZ') or //*[contains(@class,'_3UAT2v')]]");
    // Broadened add-to-cart locator to include button, anchor, role=button, input/value etc
    private static final By BY_ADD_TO_CART = By.xpath("//*[ (local-name() = 'button' or local-name() = 'a' or @role='button' or contains(@class,'_2KpZ6l') or @aria-label) and (contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to bag') or (contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart'))) ] | //input[contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')]");
    private static final By BY_BUY_NOW = By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy now') or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy')]");

    // Extra CSS fallbacks to catch common class/id patterns
    private static final By BY_CSS_FALLBACK_1 = By.cssSelector("button[class*='add'][class*='cart'], button[id*='add'][id*='cart'], .addToCart, .add-to-cart");
    private static final By BY_CSS_FALLBACK_2 = By.cssSelector("button[class*='add'], a[class*='add'], [id*='add-to-cart']");

    public ProductDetailsPage(WebDriver driver) {
        this.ldriver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public String getProductName() {
        List<WebElement> els = ldriver.findElements(BY_PRODUCT_NAME);
        if (els != null && els.size() > 0) {
            return els.get(0).getText().trim();
        }
        return "";
    }

    public String getPrice() {
        List<WebElement> els = ldriver.findElements(BY_PRICE);
        if (els != null && els.size() > 0) {
            return els.get(0).getText().trim();
        }
        return "";
    }

    public String getRatings() {
        try {
            List<WebElement> els = ldriver.findElements(BY_RATINGS);
            if (els != null && els.size() > 0) {
                return els.get(0).getText().trim();
            }
        } catch (Exception e) {
            // ignore
        }
        return "N/A";
    }

    public void addToCart() {
        // First wait for either add-to-cart or buy-now to be present (some pages show buy-now only)
        try {
            logger.debug("Waiting up to 15s for Add/Buy button to appear on product details page");
            WebDriverWait localWait = new WebDriverWait(ldriver, Duration.ofSeconds(15));
            localWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart')] | //button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')] | //button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy now')] | //a[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy now')]")));
        } catch (Exception e) {
            logger.debug("Timeout waiting for Add/Buy presence: {}", e.getMessage());
            // continue to try finding candidates anyway
        }

        List<WebElement> candidates = ldriver.findElements(BY_ADD_TO_CART);

        // Fallback broader selector
        if (candidates == null || candidates.size() == 0) {
            logger.debug("No product-details add-to-cart candidates found with primary selector, trying BUY_NOW selector");
            candidates = ldriver.findElements(BY_BUY_NOW);
        }

        if (candidates == null || candidates.size() == 0) {
            logger.debug("Trying very broad fallback selector for add/buy");
            candidates = ldriver.findElements(By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart')] | //*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy now')] | //*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy')]") );
        }

        // Try CSS fallbacks
        if (candidates == null || candidates.size() == 0) {
            try {
                List<WebElement> css1 = ldriver.findElements(BY_CSS_FALLBACK_1);
                if (css1 != null && css1.size() > 0) {
                    logger.info("ProductDetailsPage: matched BY_CSS_FALLBACK_1 with {} elements", css1.size());
                    candidates = css1;
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        if (candidates == null || candidates.size() == 0) {
            try {
                List<WebElement> css2 = ldriver.findElements(BY_CSS_FALLBACK_2);
                if (css2 != null && css2.size() > 0) {
                    logger.info("ProductDetailsPage: matched BY_CSS_FALLBACK_2 with {} elements", css2.size());
                    candidates = css2;
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        if (candidates == null || candidates.size() == 0) {
            // New proximity fallback: locate price element and look for action buttons near it
            try {
                List<WebElement> priceEls = ldriver.findElements(BY_PRICE);
                if (priceEls != null && priceEls.size() > 0) {
                    WebElement priceEl = priceEls.get(0);
                    // search ancestors for buttons/anchors/role=button
                    WebElement ancestor = priceEl.findElement(By.xpath("ancestor::div[1]"));
                    List<WebElement> nearbyButtons = ancestor.findElements(By.xpath(".//button | .//a | .//*[@role='button']"));
                    if (nearbyButtons != null && nearbyButtons.size() > 0) {
                        logger.info("ProductDetailsPage: found {} nearby buttons near price as fallback", nearbyButtons.size());
                        List<WebElement> filtered = new ArrayList<>();
                        for (WebElement nb : nearbyButtons) {
                            String txt = "";
                            try { txt = nb.getText(); } catch (Exception ex) { /* ignore */ }
                            if (txt != null && (txt.toLowerCase().contains("add") || txt.toLowerCase().contains("buy") || txt.toLowerCase().contains("cart"))) {
                                filtered.add(nb);
                            }
                        }
                        if (!filtered.isEmpty()) {
                            candidates = filtered;
                        }
                    }
                }
            } catch (Exception ex) {
                logger.debug("Proximity fallback failed: {}", ex.getMessage());
            }
        }

        if (candidates == null || candidates.size() == 0) {
            // Log page snapshot to help debugging at INFO level so it's visible in your logs
            try {
                String html = (String) ((JavascriptExecutor) ldriver).executeScript("return document.documentElement.outerHTML;");
                logger.info("ProductDetailsPage HTML snapshot (truncated to 2000 chars): {}", html.length() > 2000 ? html.substring(0, 2000) : html);
            } catch (Exception e) {
                logger.info("Could not read page HTML: {}", e.getMessage());
            }
            throw new RuntimeException("Add to Cart button not found on product details page");
        }

        logger.info("ProductDetailsPage: found {} add/buy candidate(s)", candidates.size());

        RuntimeException lastException = null;
        for (WebElement candidate : candidates) {
            try {
                try {
                    Object outer = ((JavascriptExecutor) ldriver).executeScript("return arguments[0].outerHTML;", candidate);
                    logger.info("Trying product-details candidate outerHTML (truncated to 500 chars): {}", outer == null ? "null" : (outer.toString().length() > 500 ? outer.toString().substring(0,500) : outer.toString()));
                } catch (Exception e) {
                    logger.info("Could not get outerHTML for candidate: {}", e.getMessage());
                }

                WaitUtils.waitForVisibility(ldriver, candidate, 10);
                WaitUtils.waitForClickable(ldriver, candidate, 10);
                candidate.click();
                logger.info("Clicked Add/Buy candidate on product details page");
                return;
            } catch (Exception e) {
                lastException = new RuntimeException("Failed to click candidate Add/Buy on product details page", e);
                try {
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].scrollIntoView(true);", candidate);
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", candidate);
                    logger.info("Clicked Add/Buy via JS fallback on product details page");
                    return;
                } catch (Exception ex) {
                    lastException = new RuntimeException("JS click fallback failed on product details page", ex);
                    logger.info("JS fallback failed: {}", ex.getMessage());
                }
            }
        }

        // After trying candidates and JS fallback per candidate, if still not clicked, try DOM-wide JS search
        try {
            logger.info("Attempting DOM-wide JS search for Add/Buy elements as a last resort");
            String script = "var nodes = document.querySelectorAll('*');\n" +
                    "for (var i=0;i<nodes.length;i++) {\n" +
                    "  var el = nodes[i];\n" +
                    "  var txt = (el.innerText || el.textContent || '').toLowerCase();\n" +
                    "  if (txt.indexOf('add to cart')!==-1 || txt.indexOf('add to bag')!==-1 || (txt.indexOf('add')!==-1 && txt.indexOf('cart')!==-1) || txt.indexOf('buy now')!==-1 || txt.indexOf('buy')!==-1) {\n" +
                    "    try { el.scrollIntoView(true); el.click(); return el.outerHTML; } catch(e) { try { el.click(); return el.outerHTML; } catch(e2) {} }\n" +
                    "  }\n" +
                    "}\n return null;";

            Object result = ((JavascriptExecutor) ldriver).executeScript(script);
            if (result != null) {
                String outer = result.toString();
                logger.info("DOM-wide JS search clicked element outerHTML (truncated): {}", outer.length() > 500 ? outer.substring(0, 500) : outer);
                return;
            }
        } catch (Exception jsEx) {
            logger.info("DOM-wide JS fallback failed: {}", jsEx.getMessage());
        }

        // If still not found, throw as before
        throw new RuntimeException("Add to Cart button not found on product details page");
    }
}
