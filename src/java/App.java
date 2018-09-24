import org.apache.log4j.Logger;
import ui.main.MainFrame;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class App {
    private static Logger log = Logger.getLogger("file");

    public static void main(String[] args) {
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        log.info("Выделенная память для приложения - " + maxMemory);

        System.out.println("Выделенная память для приложения - " + maxMemory);
//        if (maxMemory < 25000) {
//            log.info("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти.");
//            System.out.println("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти.");
//            String currentPath = null;
//            try {
//                currentPath = App.class
//                        .getProtectionDomain()
//                        .getCodeSource().getLocation()
//                        .toURI().getPath();
////                        .toURI().getPath()
////                        .replace('/', File.separator.charAt(0)).substring(1);
////                        .replace('\\', File.separator.charAt(0)).substring(1);
//                System.out.println("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти. "+currentPath);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                Runtime.getRuntime().exec("java -jar -Xms5000m -Xmx29000m " + currentPath + " restart");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return;
//        }

        log.info("Памяти достаточно.");
        System.out.println("Памяти достаточно.");
        try {
            MainFrame.getMainFrame();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        } catch (Error error) {
            log.error(error.getLocalizedMessage());
        }

    }

    public static Logger getLog() {
        return log;
    }
}
