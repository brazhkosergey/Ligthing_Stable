package ui.video;

import entity.HideZoneArea;
import org.apache.log4j.Logger;
import ui.setting.BackgroundSettingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class HideZonePanel extends JPanel {
    private Map<String, HideZoneArea> hideZoneAreaMap;
    private String[] hideZoneDetectedNames;
    private boolean testMode;
    private int x1OfPicture;
    private int y1OfPicture;
    private String testZoneName;

    public HideZonePanel(String hideZoneName, boolean testMode) {
        this.testMode = testMode;
        if (testMode) {
            this.addMouseListener(new MyListener(this));
        }
        if (hideZoneName != null) {
            hideZoneDetectedNames = hideZoneName.split(",");
        }
        hideZoneAreaMap = new TreeMap<>(new MyComparator());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        if (hideZoneDetectedNames != null) {
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

            x1OfPicture = (panelWidth - pictureWidth) / 2;
            y1OfPicture = (panelHeight - pictureHeight) / 2;

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

            g.setFont(new Font(null, Font.BOLD, this.getHeight() / 30));
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

                    graphics2D.setColor(new Color(184, 184, 184));
                    graphics2D.setColor(Color.BLACK);
                    for (int k = 0; k < 16; k++) {
                        int x1 = (k * (pictureWidth - circleDiameter - 10) / 16);
                        String nameOfZone = String.valueOf(alphabet[numberOfLine]) + (k + 1);
                        HideZoneArea hideZoneArea = new HideZoneArea(nameOfZone);
                        hideZoneAreaMap.put(hideZoneArea.getNameOfArea(), hideZoneArea);
                        if (i != 0) {
                            graphics2D.drawString(nameOfZone.toUpperCase(), x1OfPicture + x1 + (pictureWidth - circleDiameter - 10) / 32 - 5, y1OfPicture + y - 3);
                        }
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

            graphics2D.drawOval(x1OfPicture + pictureWidth - circleDiameter,
                    y1OfPicture + pictureHeight / 2 - circleDiameter / 2,
                    circleDiameter, circleDiameter);

            int yOfProtectedZone = y1OfPicture + (29 * pictureHeight / 86);
            int heightOfProtectedZone = 28 * pictureHeight / 86;

            if (!testMode) {
                for (String hideZoneName : hideZoneDetectedNames) {
                    HideZoneArea hideZoneArea = hideZoneAreaMap.get(hideZoneName);
                    if (hideZoneArea != null) {
                        graphics2D.setColor(new Color(255, 211, 45, 200));
                        int getYOfZone = hideZoneArea.getyOfZone();
                        int heightOfZone = hideZoneArea.getHeightOfZone();
                        graphics2D.fillRect(x1OfPicture + hideZoneArea.getxOfZone(), y1OfPicture + getYOfZone, hideZoneArea.getWidthOfZone(), heightOfZone);
                        if (hideZoneName.contains("f") ||
                                hideZoneName.contains("e") ||
                                hideZoneName.contains("d") ||
                                hideZoneName.contains("g")) {
                            graphics2D.fillOval(x1OfPicture + pictureWidth - circleDiameter,
                                    y1OfPicture + pictureHeight / 2 - circleDiameter / 2,
                                    circleDiameter, circleDiameter);
                        }
                    }
                }
            } else {
                if (testZoneName != null) {
                    HideZoneArea hideZoneArea = hideZoneAreaMap.get(testZoneName);
                    if (hideZoneArea != null) {
                        graphics2D.setColor(new Color(255, 211, 45, 200));
                        int getYOfZone = hideZoneArea.getyOfZone();
                        int heightOfZone = hideZoneArea.getHeightOfZone();
                        graphics2D.fillRect(x1OfPicture + hideZoneArea.getxOfZone(), y1OfPicture + getYOfZone, hideZoneArea.getWidthOfZone(), heightOfZone);
                    }
                }
            }
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.drawRect(x1OfPicture, yOfProtectedZone, pictureWidth - circleDiameter - 10, heightOfProtectedZone);
            graphics2D.setColor(new Color(146, 154, 251, 200));
            graphics2D.fillRect(x1OfPicture, yOfProtectedZone, pictureWidth - circleDiameter - 10, heightOfProtectedZone);

            BasicStroke pen1 = new BasicStroke(4);
            graphics2D.setStroke(pen1);
            graphics2D.setColor(new Color(255, 211, 45));
            graphics2D.drawLine(x1OfPicture - 5, y1OfPicture + pictureHeight / 2, x1OfPicture + pictureWidth + 5 - circleDiameter - 10, y1OfPicture + pictureHeight / 2);
        } else {
            g.setFont(new Font(null, Font.BOLD, 100));
            g.drawString("\u2300", panelWidth / 2, panelHeight / 2);
        }
    }

    class MyListener implements MouseListener {

        private HideZonePanel hideZonePanel;

        private MyListener(HideZonePanel hideZonePanel) {
            this.hideZonePanel = hideZonePanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                int x = e.getX() - x1OfPicture;
                int y = e.getY() - y1OfPicture;

                for (String zoneName : hideZoneAreaMap.keySet()) {
                    HideZoneArea hideZoneArea = hideZoneAreaMap.get(zoneName);
                    if (x > hideZoneArea.getxOfZone() &&
                            hideZoneArea.getxOfZone() + hideZoneArea.getWidthOfZone() > x &&
                            hideZoneArea.getyOfZone() < y) {
                        testZoneName = zoneName;
                        hideZonePanel.repaint();
                        break;
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public String getTestZoneName() {
        return testZoneName;
    }


    private class MyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int comp = o1.substring(0, 1).compareTo(o2.substring(0, 1));
            if (comp == 0) {
                comp = Integer.valueOf(o1.substring(1, o1.length())).compareTo(Integer.valueOf(o2.substring(1, o2.length())));
            }
            return comp;
        }
    }
}


