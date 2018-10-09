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
        JButton backButton = new JButton("<html>&#11178</html>");
        backButton.setFont(new Font(null, Font.BOLD, 30));
        backButton.addActionListener((hf) -> {
            if (date != null) {
                if (player) {
                    VideoPlayer currentPlayer = VideoFilesPanel.getCurrentPlayer();
                    MainFrame.setCentralPanel(currentPlayer);
                } else {
                    MainFrame.showVideoFilesPanel();
                }
            } else {
                System.out.println(" Ставим настрйоки ");
                MainFrame.setSettingPanel();
            }
        });

        JButton saveButton = new JButton("<html>&#128190</html>");
        saveButton.setFont(new Font(null, Font.BOLD, 30));
        saveButton.addActionListener((actionEvent) -> {

            if (date != null) {
                saveImage();
            } else {
                String testZoneName = hideZonePanel.getTestZoneName();
                if (testZoneName != null) {
                    infoLabel.setText("Creating test image - " + testZoneName);
                    Thread g = new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        HideZoneLightingSearcher.createTestImageForCameraThreeAndFour(testZoneName);
                        infoLabel.setText("Images was created - " + testZoneName);
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

        this.add(mainPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    private void saveImage() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        this.paint(g);
        String pathToSave = Storage.getPath() + "\\hideZoneImages\\" + dateFormat.format(date) + " - " + hideZoneName + ".jpeg";
        System.out.println(pathToSave);
        File fileImageToSave = new File(pathToSave);
        try {
            ImageIO.write(image, "jpeg", fileImageToSave);
        } catch (IOException ex) {
        }
//        BufferedImage imagebuf = null;
//        try {
////            imagebuf = new Robot().createScreenCapture(this.getBounds());
//            imagebuf = new Robot().createScreenCapture();
//        } catch (AWTException e) {
//            e.printStackTrace();
//        }
//        String pathToSave = Storage.getPath() + "\\hideZoneImages\\" + dateFormat.format(date) + " - " + hideZoneName + ".jpeg";
//        System.out.println(pathToSave);
//        File fileImageToSave = new File(pathToSave);
//        try {
//            boolean newFile = fileImageToSave.createNewFile();
//            if (newFile) {
//                ImageIO.write(imagebuf, "jpeg", fileImageToSave);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
