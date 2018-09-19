package ui.setting;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class BackgroundSettingListener implements MouseMotionListener, MouseWheelListener, MouseListener {
    private JPanel panel;
    private int[][] sourcePoints;
    private int[] movedPoint;
    private int accuracy = 20;

    public BackgroundSettingListener(JPanel panel,int[][] sourcePoints) {
        this.panel = panel;
        this.sourcePoints = sourcePoints;
    }

    private int[] getNearestPoint(int x, int y) {
        int[] point = null;
        for (int[] p : sourcePoints) {
            if (Math.abs(p[0] - x) < accuracy && Math.abs(p[1] - y) < accuracy) {
                point = p;
                break;
            }
        }
        return point;
    }

//    private void eval(double[] destX, double[][] coordinates, double tT) {
//        double t = tT;
//        double t2 = t * t;
//        double t3 = t2 * t;
//
//        for (int i = 0; i < destX.length; i++) {
//            destX[i] = 0.5 * ((coordinates[3][i] - coordinates[0][i] + 3 * (coordinates[1][i] - coordinates[2][i])) * t3
//                    + (2 * (coordinates[0][i] + 2 * coordinates[2][i]) - 5 * coordinates[1][i] - coordinates[3][i]) * t2
//                    + (coordinates[2][i] - coordinates[0][i]) * t)
//                    + coordinates[1][i];
//        }
//    }

//    public void drawLine() {
//
//        for (int[] point : sourcePoints) {
//            panel.getGraphics().setColor(pointColor);
//            panel.getGraphics().fillOval(point[0], point[1], pointSize, pointSize);
//        }
//        panel.getGraphics().setColor(lineColor);
//        for (int i = 0; i < sourcePoints.size(); i++) {
//            if (sourcePoints.size() > i + 3) {
//                for (int j = 0; j < 4; j++) {
//                    int pointNumber = i + j;
//                    points[j][0] = sourcePoints.get(pointNumber)[0];
//                    points[j][1] = sourcePoints.get(pointNumber)[1];
//                }
//
//                for (double t = 0; t < 1; t += 0.001) {
//                    eval(linePoint, points, t);
//                    panel.getGraphics().drawRect((int) linePoint[0], (int) linePoint[1], lineSize, lineSize);
//                }
//            }
//        }
//    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        int x = e.getX();
//        int y = e.getY();
//
//        if (e.getClickCount() == 2) {
//            sourcePoints.add(new int[]{x, y});
//            drawLine();
//        }

//            Point point = getNearestPoint(x, y);
//            if (point == null) {
//                sourcePoints.add(new Point(x, y));
//                drawLine();
//            }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        movedPoint = getNearestPoint(x, y);
        if (movedPoint != null) {
            System.out.println(" Поймали точку - " + movedPoint[0] + " " + movedPoint[1]);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (movedPoint != null) {
            movedPoint[0] = x;
            movedPoint[1] = y;
            panel.repaint();
            movedPoint = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {}
}
