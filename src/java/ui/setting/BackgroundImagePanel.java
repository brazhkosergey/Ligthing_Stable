package ui.setting;

import entity.Storage.Storage;
import ui.main.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BackgroundImagePanel extends JPanel {
    private BufferedImage bufferedImage;
    private int[][] sourcePoints;
    private double[][] pointsToDrawLine;
    private double[] onePointToDrawLine;
    private int groupNumber;

    private int[][] linePointsToSave;
    int lineSize = 3;
    boolean savePoints = false;

    private List<int[]> test;


    public BackgroundImagePanel(BufferedImage bufferedImage, int groupNumber) {
        this.bufferedImage = bufferedImage;
        this.groupNumber = groupNumber;

        sourcePoints = Storage.getLinePoints().get(groupNumber);
        if (sourcePoints == null) {
            sourcePoints = new int[5][];
            int heightPanel = bufferedImage.getHeight();
            int widthPanel = bufferedImage.getWidth();
            for (int i = 0; i < 5; i++) {
                sourcePoints[i] = new int[]{widthPanel / 6 * i, heightPanel / 2};
            }

        }
        pointsToDrawLine = new double[4][];
        for (int i = 1; i < 5; i++) {
            pointsToDrawLine[i - 1] = new double[2];
        }

        onePointToDrawLine = new double[2];
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(bufferedImage, 0, 0, null);
        g.setColor(Color.GREEN);

        if (savePoints) {
            linePointsToSave = new int[5][];
        }

        for (int i = 0; i < sourcePoints.length; i++) {
            if (sourcePoints.length > i + 3) {
                for (int j = 0; j < 4; j++) {
                    int pointNumber = i + j;
                    pointsToDrawLine[j][0] = sourcePoints[pointNumber][0];
                    pointsToDrawLine[j][1] = sourcePoints[pointNumber][1];
                }
                for (double t = 0; t < 1; t += 0.005) {
                    eval(onePointToDrawLine, pointsToDrawLine, t);
                    g.fillRect((int) onePointToDrawLine[0], (int) onePointToDrawLine[1], lineSize, lineSize);
                }
            }
        }

        g.setColor(Color.RED);
        for (int i = 0; i < sourcePoints.length; i++) {
            g.fillOval(sourcePoints[i][0], sourcePoints[i][1] - 4, 6, 6);

            if (savePoints) {
                int x = sourcePoints[i][0];
                int y = sourcePoints[i][1];
                int[] savePoint = new int[2];
                savePoint[0] = x;
                savePoint[1] = y;
                linePointsToSave[i] = savePoint;
            }
        }

        if (savePoints) {
            Storage.addLinePoint(groupNumber, linePointsToSave, true);
            savePoints = false;
        }

        if (test != null) {
            g.setColor(Color.blue);
            for (int i = 0; i < test.size(); i++) {
                g.fillOval(test.get(i)[0], test.get(i)[1], 2, 2);
            }
        }
    }

    public void setTest(List<int[]> test) {
        this.test = test;
        this.repaint();
    }

    public static void eval(double[] destX, double[][] coordinates, double tT) {
        double t = tT;
        double t2 = t * t;
        double t3 = t2 * t;

        for (int i = 0; i < destX.length; i++) {
            destX[i] = 0.5 * ((coordinates[3][i] - coordinates[0][i] + 3 * (coordinates[1][i] - coordinates[2][i])) * t3
                    + (2 * (coordinates[0][i] + 2 * coordinates[2][i]) - 5 * coordinates[1][i] - coordinates[3][i]) * t2
                    + (coordinates[2][i] - coordinates[0][i]) * t)
                    + coordinates[1][i];
        }
    }

    Dimension getDimension() {
        return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    int[][] getSourcePoints() {
        return sourcePoints;
    }

    public void setSavePoints(boolean savePoints) {
        this.savePoints = savePoints;
    }
}