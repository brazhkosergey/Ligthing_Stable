package ui.video;

import entity.HideZoneArea;
import org.apache.log4j.Logger;
import ui.setting.BackgroundSettingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HideZonePanel extends JPanel {

    private Map<String, HideZoneArea> hideZoneAreaMap;
    private String hideZoneName;

    public HideZonePanel(String hideZoneName) {
        this.hideZoneName = hideZoneName;
        hideZoneAreaMap = new HashMap<>();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();
        if (hideZoneName != null && hideZoneName.length() > 1) {
            Graphics2D graphics2D = (Graphics2D) g;
            int hideZoneWidth = 160;
            int hideZoneHeight = 90;
            double proportions = (double) (panelHeight - 20) / hideZoneHeight;

            if ((int) (proportions * hideZoneWidth + proportions * 10 + 10) > panelWidth) {
                proportions = (double) (panelWidth - 20 - 5) / (hideZoneWidth + 10);
            }

            int circleDiameter = (int) (proportions * 10);
            int pictureHeight = (int) (proportions * hideZoneHeight);
            int pictureWidth = (int) (proportions * hideZoneWidth + circleDiameter);

            int x1OfPicture = (panelWidth - pictureWidth) / 2;
            int y1OfPicture = (panelHeight - pictureHeight) / 2;

            int x = 0;
            for (int i = 0; i <= 16; i++) {
                if (i == 0 || i == 16) {
                    graphics2D.setStroke(new BasicStroke(2.0f));
                } else {
                    graphics2D.setStroke(new BasicStroke(1.0f));
                }
                x = (i * (pictureWidth - circleDiameter - 10) / 16);
                graphics2D.drawLine(x1OfPicture + x, y1OfPicture, x1OfPicture + x, y1OfPicture + pictureHeight);
            }

            g.setFont(new Font(null, Font.BOLD, 15));
            char[] alphabet = new char[26];
            for (int i = 0; i < 26; i++) {
                alphabet[i] = (char) ('a' + i);
            }


            int y;
            int numberOfLine = 0;

            int yOfLastZone = 0;

            for (int i = 86; i >= 0; i--) {
                if ((i - 3) % 10 == 0 || i == 86 || i == 0) {
                    if (i == 0 || i == 86) {
                        graphics2D.setStroke(new BasicStroke(2.0f));
                    } else {
                        graphics2D.setStroke(new BasicStroke(1.0f));
                    }
                    y = i * pictureHeight / 86;
                    graphics2D.drawLine(x1OfPicture, y1OfPicture + y, x1OfPicture + x, y1OfPicture + y);

                    if (i == 86) {
                        yOfLastZone = y;
                    }

                    if (i != 0) {
                        graphics2D.setColor(new Color(184, 184, 184));

                        for (int k = 0; k < 16; k++) {
                            int x1 = (k * (pictureWidth - circleDiameter - 10) / 16);
                            String nameOfZone = String.valueOf(alphabet[numberOfLine]) + (k + 1);

                            HideZoneArea hideZoneArea = new HideZoneArea(nameOfZone);
                            hideZoneAreaMap.put(hideZoneArea.getNameOfArea(), hideZoneArea);

                            graphics2D.drawString(nameOfZone.toUpperCase(), x1OfPicture + x1 + (pictureWidth - circleDiameter - 10) / 32 - 5, y1OfPicture + y - 3);

                            if (i != 86) {
                                HideZoneArea hideZoneAreaLast = hideZoneAreaMap.get(String.valueOf(alphabet[numberOfLine - 1]) + (k + 1));
                                int heightOfZone = yOfLastZone - y;
                                int widthOfZone = (pictureWidth - circleDiameter - 10) / 16;
                                hideZoneAreaLast.setHeightOfZone(heightOfZone);
                                hideZoneAreaLast.setWidthOfZone(widthOfZone);
                                hideZoneAreaLast.setxOfZone(x1);
                                hideZoneAreaLast.setyOfZone(y);
                            }
                        }

                        yOfLastZone = y;

                        graphics2D.setColor(Color.BLACK);
                        numberOfLine++;
                    }
                }
            }

            graphics2D.drawOval(x1OfPicture + pictureWidth - circleDiameter,
                    y1OfPicture + pictureHeight / 2 - circleDiameter / 2,
                    circleDiameter, circleDiameter);

            int yOfProtectedZone = y1OfPicture + (34 * pictureHeight / 86);
            int heightOfProtectedZone = 28 * pictureHeight / 86;

            HideZoneArea hideZoneArea = hideZoneAreaMap.get(hideZoneName);
            if (hideZoneArea != null) {
                graphics2D.setColor(new Color(255, 211, 45, 200));

                int getYOfZone = hideZoneArea.getyOfZone();
                int heightOfZone = hideZoneArea.getHeightOfZone();

                graphics2D.fillRect(x1OfPicture + hideZoneArea.getxOfZone(), y1OfPicture + getYOfZone, hideZoneArea.getWidthOfZone(), heightOfZone);

                if (hideZoneName.contains("f") ||
                        hideZoneName.contains("e") ||
                        hideZoneName.contains("d")) {
                    graphics2D.fillOval(x1OfPicture + pictureWidth - circleDiameter,
                            y1OfPicture + pictureHeight / 2 - circleDiameter / 2,
                            circleDiameter, circleDiameter);
                }
            }

            graphics2D.setColor(Color.DARK_GRAY);

            graphics2D.drawRect(x1OfPicture, yOfProtectedZone, pictureWidth - circleDiameter - 10, heightOfProtectedZone);

            graphics2D.setColor(new Color(146, 154, 251, 200));

            graphics2D.fillRect(x1OfPicture, yOfProtectedZone, pictureWidth - circleDiameter - 10, heightOfProtectedZone);
        } else {
            g.setFont(new Font(null, Font.BOLD, 100));
            g.drawString("\u2300", panelWidth / 2, panelHeight / 2);
        }
    }
}
