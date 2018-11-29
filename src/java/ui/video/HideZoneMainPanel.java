package ui.video;

import entity.HideZoneLightingSearcher;
import entity.Storage.Storage;
import ui.main.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HideZoneMainPanel extends JPanel {

    private String hideZoneName;
    private Date date;
    private SimpleDateFormat dateFormat;
    private HideZonePanel hideZonePanel;

    public HideZoneMainPanel(boolean player, String hideZoneName, Date date) {
        this.hideZoneName = hideZoneName;
        dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("yyyy.MM.dd HH.mm.ss");
        this.date = date;

        JLabel infoLabel;
        if (date != null) {
            List<String> namesOfZoneWasDetected = new ArrayList<>();
            if (hideZoneName.contains(",")) {
                String[] split = hideZoneName.split(",");
                for (String s : split) {
                    if (s.length() < 4) {
                        namesOfZoneWasDetected.add(s);
                    }
                }
            } else {
                if (hideZoneName.length() < 4) {
                    namesOfZoneWasDetected.add(hideZoneName);
                } else {
                    namesOfZoneWasDetected = null;
                }
            }

            if (namesOfZoneWasDetected != null && namesOfZoneWasDetected.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < namesOfZoneWasDetected.size(); i++) {
                    String s = namesOfZoneWasDetected.get(i);
                    stringBuilder.append(s);
                    if (i != namesOfZoneWasDetected.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                infoLabel = new JLabel(Storage.getBundle().getString("hidezonename") + stringBuilder.toString());
                hideZonePanel = new HideZonePanel(stringBuilder.toString(), false);
            } else {
                infoLabel = new JLabel(Storage.getBundle().getString("nothidezonename"));
                hideZonePanel = new HideZonePanel(null, false);
            }
        } else {
            hideZonePanel = new HideZonePanel("test", true);
            infoLabel = new JLabel("TEST MODE");
        }

        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(233, 233, 233));
        mainPanel.add(hideZonePanel);

        JPanel southPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton(Storage.getBundle().getString("backbutton"));
        backButton.setFont(new Font(null, Font.BOLD, 15));
        backButton.addActionListener((hf) -> {
            if (date != null) {
                if (player) {
                    VideoPlayer currentPlayer = VideoFilesPanel.getCurrentPlayer();
                    MainFrame.setCentralPanel(currentPlayer);
                } else {
                    MainFrame.showVideoFilesPanel();
                }
            } else {
                MainFrame.setSettingPanel();
            }
        });

        JButton saveButton = new JButton(Storage.getBundle().getString("savebutton"));
        saveButton.setFont(new Font(null, Font.BOLD, 15));
        saveButton.addActionListener((actionEvent) -> {
            if (date != null) {
                saveImage();
            } else {
                String testZoneName = hideZonePanel.getTestZoneName();
                if (testZoneName != null) {
                    infoLabel.setText("Creating test image - " + testZoneName);
                    Thread g = new Thread(() -> {
                        HideZoneLightingSearcher.createTestImageForCameraThreeAndFour(testZoneName);
                        infoLabel.setText("Images was created - " + testZoneName);
                        MainFrame.showInformMassage("Test " + testZoneName, Color.RED);
                    });
                    g.start();
                } else {
                    infoLabel.setText("Select hide zone");
                }
            }
        });

        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(backButton, BorderLayout.WEST);
        southPanel.add(infoLabel, BorderLayout.CENTER);
        southPanel.add(saveButton, BorderLayout.EAST);

        JPanel westPanel = new JPanel(new BorderLayout());

        JLabel l = new JLabel("\u2195");
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setFont(new Font(null, Font.BOLD, 100));

        JLabel northLabel = new JLabel(Storage.getBundle().getString("north"));
        northLabel.setFont(new Font(null, Font.BOLD, 20));
        northLabel.setAlignmentX(CENTER_ALIGNMENT);
        northLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel southLabel = new JLabel(Storage.getBundle().getString("south"));
        southLabel.setFont(new Font(null, Font.BOLD, 20));
        southLabel.setAlignmentX(CENTER_ALIGNMENT);
        southLabel.setHorizontalAlignment(SwingConstants.CENTER);

        westPanel.add(l, BorderLayout.CENTER);
        westPanel.add(northLabel, BorderLayout.NORTH);
        westPanel.add(southLabel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        this.add(westPanel, BorderLayout.WEST);
    }

    private void saveImage() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        this.paint(g);
        String pathToSave = Storage.getPath() + "\\hideZoneImages\\" + dateFormat.format(date) + " - " + hideZoneName + ".jpeg";
        File fileImageToSave = new File(pathToSave);
        if(!fileImageToSave.exists()){
            try {
                fileImageToSave.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        try {
            ImageIO.write(image, "jpeg", fileImageToSave);
            MainFrame.showInformMassage(Storage.getBundle().getString("savedbutton"), new Color(23, 114, 26));
        } catch (IOException ex) {
            MainFrame.showInformMassage(Storage.getBundle().getString("cannotsaveinform"), Color.RED);
        }
    }
}
