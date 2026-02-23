package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * ReadConfig Class
 * ----------------
 * This class is responsible for reading data from config.properties file.
 * It loads configuration values like browser name and base URL
 * and provides getter methods to access them.
 */

public class ReadConfig {

    // Properties class object to read key-value pairs from config file
    private Properties properties;

    // Dynamic path to config.properties file
    // user.dir gives current project directory path
    private String path = System.getProperty("user.dir") 
                          + "/Configuration/config.properties";

    /*
     * Constructor
     * -----------
     * Loads the config.properties file when object is created.
     */
    public ReadConfig() {

        properties = new Properties();

        try {
            // FileInputStream is used to read file data
            FileInputStream fis = new FileInputStream(path);

            // Load the properties file
            properties.load(fis);

        } catch (IOException e) {
            // If file is not found or cannot be loaded
            throw new RuntimeException("Failed to load config file: " + path);
        }
    }

    /*
     * getBaseUrl()
     * ------------
     * Returns the base URL from config.properties file.
     */
    public String getBaseUrl() {

        String value = properties.getProperty("baseurl");

        if (value != null)
            return value;
        else
            throw new RuntimeException("baseurl not found in config file");
    }

    /*
     * getBrowser()
     * ------------
     * Returns the browser name from config.properties file.
     */
    public String getBrowser() {

        String value = properties.getProperty("browser");

        if (value != null)
            return value;
        else
            throw new RuntimeException("browser not found in config file");
    }
}