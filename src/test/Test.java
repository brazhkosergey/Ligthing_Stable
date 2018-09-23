import entity.HideZoneLightingSearcher;
import ui.setting.SarcophagusSettingPanel;
import ui.setting.Setting;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Test {

    public static void main(String[] args) {
        File file = new File("C:\\LIGHTNING_STABLE\\bytes\\1537688884305");
        File fileSmall = new File("C:\\LIGHTNING_STABLE\\bytes\\1537561021388");

        HideZoneLightingSearcher.addHideZoneAreaName(file);
        HideZoneLightingSearcher.addHideZoneAreaName(fileSmall);
    }
}
