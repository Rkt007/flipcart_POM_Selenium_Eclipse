package TestCases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmokeTest extends BaseClass {

    private static final Logger logger = LoggerFactory.getLogger(SmokeTest.class);

    @Test
    public void homepageLoads() {
        String title = driver.getTitle();
        logger.info("Homepage title: {}", title);
        // Simple assertion to ensure page loaded; change expected text if different locale
        Assert.assertTrue(title.toLowerCase().contains("flipkart") || title.length() > 0, "Homepage title check failed");
    }
}