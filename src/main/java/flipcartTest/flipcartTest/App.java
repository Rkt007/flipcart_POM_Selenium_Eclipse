package flipcartTest.flipcartTest;

import me.xethh.utils.dateUtils.timezone.BaseTimeZone;

import java.io.File;
import java.util.TimeZone;

public class App
{
    public static void systemInit(){
        if(System.getProperty("base.log.path")==null || "".equals(System.getProperty("base.log.path"))){
            File file = new File("./target/test-app/logs/");
            file.mkdirs();
            System.setProperty("base.log.path",file.toString());
        }
        TimeZone.setDefault(BaseTimeZone.Hongkong.timeZone());
    }
    public static void main( String[] args )
    {
        systemInit();
        System.out.println( "Hello World!" );
    }
}
