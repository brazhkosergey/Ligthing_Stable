package ui.video;

import entity.Storage.Storage;
import ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class HideZoneMainPanel extends JPanel {

    HideZonePanel hideZonePanel;

    public HideZoneMainPanel(boolean player, String hideZoneName) {
        this.setLayout(new BorderLayout());
//        char[] alphabet = new char[26];
//        for (int i = 0; i < 26; i++) {
//            alphabet[i] = (char) ('a' + i);
//        }


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new HideZonePanel(hideZoneName));

        JPanel southPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("<html>&#11178</html>");
        backButton.setFont(new Font(null, Font.BOLD, 30));
        backButton.addActionListener((hf) -> {
            if (player) {
                VideoPlayer currentPlayer = VideoFilesPanel.getCurrentPlayer();
                MainFrame.setCentralPanel(currentPlayer);
            } else {
                MainFrame.getMainFrame().showVideoFilesPanel();
            }
        });

        JButton saveButton = new JButton("<html>&#128190</html>");
        saveButton.setFont(new Font(null, Font.BOLD, 30));
        JLabel infoLabel;
        if (hideZoneName != null && hideZoneName.length() > 1) {
            infoLabel = new JLabel(Storage.getBundle().getString("hidezonename") + hideZoneName);
        } else {
            infoLabel = new JLabel(Storage.getBundle().getString("nothidezonename"));
        }

        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(backButton, BorderLayout.WEST);
        southPanel.add(infoLabel, BorderLayout.CENTER);
        southPanel.add(saveButton, BorderLayout.EAST);

        this.add(mainPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }
}
