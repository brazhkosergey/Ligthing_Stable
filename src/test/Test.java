import ui.setting.SarcophagusSettingPanel;
import ui.setting.Setting;

import javax.swing.*;
import java.awt.*;

public class Test {

    public static void main(String[] args) {


        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(1120,540));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SarcophagusSettingPanel sarcophagusSettingPanel = new SarcophagusSettingPanel(null);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(sarcophagusSettingPanel,BorderLayout.CENTER);
        frame.pack();
    }
}
