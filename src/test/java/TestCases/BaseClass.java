package TestCases;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import Utilities.ReadConfig;
import io.github.bonigarcia.wdm.WebDriverManager;

/*
 * BaseClass
 * ---------
 * This class is responsible for:
 * 1. Reading configuration values
 * 2. Initializing WebDriver based on browser
 * 3. Launching application URL
 * 4. Closing browser after execution
 * 
 * All Test Classes extend this BaseClass.
 */

public class BaseClass {

    private static final Logger logger = LoggerFactory.getLogger(BaseClass.class);

    // Object of ReadConfig class to read config.properties file
    ReadConfig readConfig = new ReadConfig();

    // Fetch base URL and browser from config file
    String url = readConfig.getBaseUrl();
    String browser = readConfig.getBrowser();

    // WebDriver reference (shared across test classes)
    public static WebDriver driver;

    /*
     * setup()
     * -------
     * This method runs before test execution.
     * It initializes browser based on config file.
     */
    @BeforeClass
    public void setup() {

        logger.info("Initializing WebDriver for browser: {}", browser);
        System.out.println("[BaseClass] Initializing WebDriver for browser: " + browser);

        switch (browser.toLowerCase()) {

        case "chrome":
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();
            driver = new ChromeDriver(chromeOptions);
            break;

        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            driver = new FirefoxDriver(firefoxOptions);
            break;

        case "msedge":
            WebDriverManager.edgedriver().setup();
            EdgeOptions edgeOptions = new EdgeOptions();
            driver = new EdgeDriver(edgeOptions);
            break;

        default:
            throw new RuntimeException("Browser not supported: " + browser);
        }

        // Maximize browser window
        driver.manage().window().maximize();

        // Implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Launch application URL
        driver.get(url);

        logger.info("Navigated to URL: {}", url);
        System.out.println("[BaseClass] Navigated to URL: " + url);
    }

    /*
     * teardown()
     * ----------
     * This method runs after test execution.
     * It closes and quits browser.
     */
 /*   @AfterClass
    public void teardown() {

        logger.info("Tearing down WebDriver");
        System.out.println("[BaseClass] Tearing down WebDriver");

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    } */
}