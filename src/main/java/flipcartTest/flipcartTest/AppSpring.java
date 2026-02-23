package flipcartTest.flipcartTest;

import me.xethh.utils.dateUtils.formatBuilder.DateFormatBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Date;

/**
 * Application spring
 */
@SpringBootApplication
public class AppSpring {
    private final static Logger logger;
    static {
        App.systemInit();
        logger = LoggerFactory.getLogger(AppSpring.class);
        logger.info("App start up: "+ DateFormatBuilderFactory.ISO8601().format(new Date()));
    }

    public static void main(String[] args) {
        SpringApplication.run(AppSpring.class, args);
    }
}