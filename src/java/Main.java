import org.apache.log4j.Logger;
import ui.main.MainFrame;

import java.io.File;

public class Main {
    private static Logger log = Logger.getLogger("file");

    public static void main(String[] args) {
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        log.info("Выделенная память для приложения - " + maxMemory);
        if (false) {
//        if (maxMemory < 10000) {
//            log.info("Памяти не достаточно, перегружаем приложени, с указанием большего количества памяти.");
            String javaBin = "java ";
            final File currentJar;
            try {
                currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                String string = javaBin +
                        "-jar " +
                        "-Xms5000m -Xmx15000m " +
                        currentJar.getPath();
                Runtime.getRuntime().exec(string);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        log.info("Выделенная память для приложения - " + maxMemory);
        log.info("Памяти достаточно.");
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
