package Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import TestCases.BaseClass;

public class TestListener implements ITestListener {

    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "Logs";
    private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + File.separator + "Screenshots";

    private String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = BaseClass.driver;
        String name = result.getMethod().getMethodName();
        String time = timestamp();

        // ensure directories exist
        new File(LOG_DIR).mkdirs();
        new File(SCREENSHOT_DIR).mkdirs();

        if (driver != null) {
            // take screenshot
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File dest = new File(SCREENSHOT_DIR + File.separator + name + "-" + time + ".png");
                java.nio.file.Files.copy(src.toPath(), dest.toPath());
            } catch (Exception e) {
                // ignore
            }

            // save page source
            try {
                String pageSource = driver.getPageSource();
                File out = new File(LOG_DIR + File.separator + name + "-" + time + ".html");
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(pageSource.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) { }

    @Override
    public void onTestSuccess(ITestResult result) { }

    @Override
    public void onTestSkipped(ITestResult result) { }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }

    @Override
    public void onStart(ITestContext context) { }

    @Override
    public void onFinish(ITestContext context) { }
}
