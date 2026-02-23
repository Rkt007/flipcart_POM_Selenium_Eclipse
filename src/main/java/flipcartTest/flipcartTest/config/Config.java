package flipcartTest.flipcartTest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

/**
 * Base configuration for spring
 */
@Configuration
public class Config
{
    Logger logger = LoggerFactory.getLogger(Config.class);
    @PostConstruct
    public void init(){
        logger.info("Config.class initialized!!!");
    }
}
