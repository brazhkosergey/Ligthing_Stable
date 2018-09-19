package entity;

import ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class NewVideoInformFrame extends JFrame {

    public NewVideoInformFrame() {
        super();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeWidth = 250;
        int sizeHeight = 150;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        this.setBounds(locationX, locationY, sizeWidth, sizeHeight);

        setPreferredSize(new Dimension(250, 150));
        setLayout(new BorderLayout());
        this.setResizable(false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(90, 30));
        okButton.addActionListener((e) -> {
            this.dispose();
            MainVideoCreator.setInformFrameNewVideo(false);
        });

        JLabel label = new JLabel("Test");
        label.setText(MainFrame.getBundle().getString("endofsaving"));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setFont(new Font(null, Font.BOLD, 16));
        label.setPreferredSize(new Dimension(240, 80));

        JPanel outPanel = new JPanel(new FlowLayout());
        outPanel.setBackground(new Color(0x24D846));
        outPanel.add(label);
        outPanel.add(okButton);

        this.add(outPanel);
        this.setVisible(true);
        this.pack();
    }
}
