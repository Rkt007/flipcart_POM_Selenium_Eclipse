package TestCases;

import java.time.Duration;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import Utilities.ReadConfig;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

    private static final Logger logger = LoggerFactory.getLogger(BaseClass.class);

    ReadConfig readConfig = new ReadConfig();

    String url = readConfig.getBaseUrl();
    String browser = readConfig.getBrowser();

    public static WebDriver driver;

    @BeforeClass
    public void setup() {

        logger.info("Initializing WebDriver for browser: {}", browser);

        // Detect if running inside Jenkins/Docker
        boolean isCI = System.getenv("JENKINS_HOME") != null;

        switch (browser.toLowerCase()) {

        case "chrome":
            WebDriverManager.chromedriver().setup();

            ChromeOptions chromeOptions = new ChromeOptions();

            if (isCI) {
                // Required for Docker/Jenkins
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
            }

            driver = new ChromeDriver(chromeOptions);
            break;

        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions firefoxOptions = new FirefoxOptions();

            if (isCI) {
                firefoxOptions.addArguments("-headless");
            }

            driver = new FirefoxDriver(firefoxOptions);
            break;

        case "msedge":
            WebDriverManager.edgedriver().setup();
            EdgeOptions edgeOptions = new EdgeOptions();

            if (isCI) {
                edgeOptions.addArguments("--headless=new");
                edgeOptions.addArguments("--no-sandbox");
                edgeOptions.addArguments("--disable-dev-shm-usage");
            }

            driver = new EdgeDriver(edgeOptions);
            break;

        default:
            throw new RuntimeException("Browser not supported: " + browser);
        }

        // Set window size (works in both headless and normal mode)
        driver.manage().window().setSize(new Dimension(1920, 1080));

        // Implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Launch URL
        driver.get(url);

        logger.info("Navigated to URL: {}", url);
    }

    @AfterClass
    public void teardown() {

        logger.info("Tearing down WebDriver");

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}