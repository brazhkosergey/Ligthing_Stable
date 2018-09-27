import org.apache.log4j.Logger;
import ui.main.MainFrame;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    private static Logger log = Logger.getLogger("file");

    public static void main(String[] args) {
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        log.info("Выделенная память для приложения - " + maxMemory);

        System.out.println("Выделенная память для приложения - " + maxMemory);

        if (false) {
//        if (maxMemory < 25000) {
            log.info("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти.");
            System.out.println("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти.");
            String javaBin = "java ";
            final File currentJar;
            try {
                currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                String string = javaBin +
                        "-jar " +
                        "-Xms5000m -Xmx29000m " +
                        currentJar.getPath();
                Runtime.getRuntime().exec(string);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        log.info("Выделенная память для приложения - " + maxMemory);
        System.out.println("Выделенная память для приложения - " + maxMemory);
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
