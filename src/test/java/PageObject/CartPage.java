package PageObject;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Utilities.WaitUtils;

public class CartPage {

    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);

    WebDriver ldriver;
    WebDriverWait wait;

    // Runtime By locators (more flexible than PageFactory for dynamic pages)
    private static final By BY_LOGIN_CLOSE = By.xpath("//button[contains(@class,'_2doB4z') or contains(.,'✕')]");
    // Broadened add-to-cart locator: button, anchor, or role=button, or input with value
    private static final By BY_ADD_TO_CART = By.xpath("//*[ (local-name() = 'button' or local-name() = 'a' or @role='button' or contains(@class,'_2KpZ6l')) and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart') ] | //input[contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')]");
    private static final By BY_BUY_NOW = By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'buy now') or contains(.,'Buy Now')]");

    // Cart view/management locators (simplified and fixed XPaths)
    private static final By BY_CART_ICON = By.xpath("//a[contains(@href,'/viewcart') or contains(@href,'/cart') or contains(.,'Cart')]");
    private static final By BY_CART_ITEMS = By.xpath("//div[contains(@class,'_2utdA-') or contains(@class,'_1AtVbE')]//div[contains(@class,'_3wU53n') or contains(@class,'_1AtVbE') or contains(@class,'_2kHMtA')]");
    private static final By BY_REMOVE_BTN = By.xpath("//button[contains(.,'Remove') or contains(.,'REMOVE') or (contains(@class,'_2KpZ6l') and contains(.,'Remove'))]");
    private static final By BY_PROCEED_TO_CHECKOUT = By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'place order') or contains(.,'Place Order') or contains(.,'Proceed to Checkout') or contains(.,'Checkout')]");

    public CartPage(WebDriver rdriver) {
        ldriver = rdriver;
        wait = new WebDriverWait(ldriver, Duration.ofSeconds(15));
        PageFactory.initElements(rdriver, this);
    }

    /*
     * closeLoginPopupIfPresent()
     */
    public void closeLoginPopupIfPresent() {

        try {
            List<WebElement> popupButtons = ldriver.findElements(BY_LOGIN_CLOSE);
            if (popupButtons != null && popupButtons.size() > 0) {
                try {
                    popupButtons.get(0).click();
                    return;
                } catch (Exception e) {
                    // try to click via JS as fallback
                    try {
                        ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", popupButtons.get(0));
                        return;
                    } catch (Exception ex) {
                        // ignore
                    }
                }
            }
        } catch (Exception e) {
            // ignore - popup simply not present
        }
    }

    /*
     * addToCart()
     */
    public void addToCart() {

        // Try to locate candidate buttons at runtime
        List<WebElement> candidates = ldriver.findElements(BY_ADD_TO_CART);

        // Fallback: broader search for elements containing both 'add' and 'cart'
        if (candidates == null || candidates.size() == 0) {
            logger.debug("No primary add-to-cart candidates found, trying broader fallback selector");
            candidates = ldriver.findElements(By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add') and contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cart')]"));
        }

        if (candidates == null || candidates.size() == 0) {
            // Log page snapshot to help debugging
            try {
                String html = (String) ((JavascriptExecutor) ldriver).executeScript("return document.documentElement.outerHTML;");
                logger.debug("Current page HTML snapshot (truncated): {}", html.length() > 2000 ? html.substring(0, 2000) : html);
            } catch (Exception ex) {
                logger.debug("Could not retrieve page HTML for debugging: {}", ex.getMessage());
            }

            throw new RuntimeException("Add to Cart button not found on product page");
        }

        logger.info("Found {} add-to-cart candidate(s)", candidates.size());

        // Try clicking the first interactable candidate with a few retries
        RuntimeException lastException = null;
        for (WebElement candidate : candidates) {
            try {
                // Log candidate outerHTML for debugging
                try {
                    Object outer = ((JavascriptExecutor) ldriver).executeScript("return arguments[0].outerHTML;", candidate);
                    logger.debug("Trying candidate outerHTML: {}", outer == null ? "null" : outer.toString());
                } catch (Exception e) {
                    logger.debug("Could not get outerHTML for candidate: {}", e.getMessage());
                }

                // Wait for visibility & clickability using WaitUtils
                WaitUtils.waitForVisibility(ldriver, candidate, 10);
                WaitUtils.waitForClickable(ldriver, candidate, 10);

                candidate.click();
                logger.info("Clicked add-to-cart candidate successfully");
                return; // success
            } catch (Exception e) {
                lastException = new RuntimeException("Failed to click candidate Add to Cart button", e);
                // try JS click as fallback
                try {
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].scrollIntoView(true);", candidate);
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", candidate);
                    logger.info("Clicked add-to-cart candidate via JS fallback");
                    return;
                } catch (Exception ex) {
                    lastException = new RuntimeException("JS click fallback failed for Add to Cart", ex);
                    logger.debug("JS fallback also failed for this candidate: {}", ex.getMessage());
                }
            }
        }

        // If we reach here, no candidate worked
        if (lastException != null)
            throw lastException;
        else
            throw new RuntimeException("Unable to click Add to Cart - no interactable candidates");
    }

    /*
     * clickBuyNow()
     */
    public void clickBuyNow() {

        List<WebElement> candidates = ldriver.findElements(BY_BUY_NOW);

        if (candidates == null || candidates.size() == 0) {
            throw new RuntimeException("Buy Now button not found on product page");
        }

        RuntimeException lastException = null;
        for (WebElement candidate : candidates) {
            try {
                // scroll to candidate, then wait and click
                try {
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].scrollIntoView(true);", candidate);
                } catch (Exception ignore) {
                }

                WaitUtils.waitForVisibility(ldriver, candidate, 10);
                WaitUtils.waitForClickable(ldriver, candidate, 10);

                candidate.click();
                return;
            } catch (Exception e) {
                lastException = new RuntimeException("Failed to click candidate Buy Now button", e);
                // try JS click as fallback
                try {
                    ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", candidate);
                    return;
                } catch (Exception ex) {
                    lastException = new RuntimeException("JS click fallback failed for Buy Now", ex);
                }
            }
        }

        if (lastException != null)
            throw lastException;
        else
            throw new RuntimeException("Unable to click Buy Now - no interactable candidates");
    }

    /*
     * viewCart()
     */
    public void viewCart() {
        try {
            List<WebElement> icons = ldriver.findElements(BY_CART_ICON);
            if (icons != null && icons.size() > 0) {
                WebElement icon = icons.get(0);
                try {
                    WaitUtils.waitForVisibility(ldriver, icon, 10);
                    WaitUtils.waitForClickable(ldriver, icon, 10);
                    icon.click();
                    return;
                } catch (Exception e) {
                    try {
                        ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", icon);
                        return;
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to click cart icon", ex);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cart icon not found or clickable", e);
        }

        throw new RuntimeException("Cart icon not found on page");
    }

    /*
     * removeFirstItem()
     */
    public void removeFirstItem() {
        // Assume we are on cart page
        try {
            List<WebElement> items = ldriver.findElements(BY_CART_ITEMS);
            if (items == null || items.size() == 0) {
                throw new RuntimeException("No items found in cart to remove");
            }

            WebElement firstItem = items.get(0);
            // find a remove button within the item
            List<WebElement> removeButtons = firstItem.findElements(BY_REMOVE_BTN);
            if (removeButtons != null && removeButtons.size() > 0) {
                WebElement removeBtn = removeButtons.get(0);
                try {
                    WaitUtils.waitForVisibility(ldriver, removeBtn, 10);
                    WaitUtils.waitForClickable(ldriver, removeBtn, 10);
                    removeBtn.click();
                    // confirm removal if a confirmation dialog appears
                    // try clicking a confirm button
                    List<WebElement> confirms = ldriver.findElements(By.xpath("//button[contains(.,'Remove') or contains(.,'REMOVE') or contains(.,'Yes')]") );
                    if (confirms != null && confirms.size() > 0) {
                        try {
                            WaitUtils.waitForClickable(ldriver, confirms.get(0), 5);
                            confirms.get(0).click();
                        } catch (Exception ex) {
                            // ignore
                        }
                    }
                    return;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to click remove button for first cart item", e);
                }
            } else {
                // try global remove buttons
                List<WebElement> globalRemove = ldriver.findElements(BY_REMOVE_BTN);
                if (globalRemove != null && globalRemove.size() > 0) {
                    try {
                        WaitUtils.waitForClickable(ldriver, globalRemove.get(0), 10);
                        globalRemove.get(0).click();
                        return;
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to click global remove button", ex);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while removing first cart item", e);
        }

        throw new RuntimeException("Unable to remove any item from cart");
    }

    /*
     * proceedToCheckout()
     */
    public void proceedToCheckout() {
        try {
            List<WebElement> candidates = ldriver.findElements(BY_PROCEED_TO_CHECKOUT);
            if (candidates != null && candidates.size() > 0) {
                WebElement btn = candidates.get(0);
                try {
                    WaitUtils.waitForVisibility(ldriver, btn, 10);
                    WaitUtils.waitForClickable(ldriver, btn, 10);
                    btn.click();
                    return;
                } catch (Exception e) {
                    try {
                        ((JavascriptExecutor) ldriver).executeScript("arguments[0].click();", btn);
                        return;
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to click proceed to checkout button", ex);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Proceed to checkout button not found or not clickable", e);
        }

        throw new RuntimeException("Proceed to checkout button not found on cart page");
    }
}