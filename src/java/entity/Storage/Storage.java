package entity.Storage;

import entity.AddressSaver;
import entity.Camera.Camera;
import entity.Camera.CameraGroup;
import entity.sound.SoundSaver;
import org.apache.log4j.Logger;
import ui.main.MainFrame;
import ui.setting.BackgroundImagePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Storage {

    private static Logger log = Logger.getLogger("file");

    private static String password;

    private static Map<Integer, int[][]> linePoints;
    private static Map<Integer, List<int[]>> linesForHideZoneParsing;
    private static Map<Integer, double[]> pixelsSizesForHideZoneParsingMap;
    private static Map<Integer, Double> lengthOfViewArcMap;


    private static AddressSaver addressSaver;

    private static int opacitySetting = 30;

    private static int secondsToSave = 30;

    private static int percentDiffWhite = 5;

    private static int colorLightNumber = 200;

    private static Set<Integer> colorRGBNumberSet;

    private static boolean programLightCatchEnable;

    private static int port;

    private static String path = "C:\\LIGHTNING_STABLE\\";

    private static String defaultPath = "C:\\LIGHTNING_STABLE\\";

    private static SoundSaver soundSaver;

    private static ResourceBundle bundle;

    private static int showFramesPercent = 15;

    private static CameraGroup[] cameraGroups;

    static {
        bundle = ResourceBundle.getBundle("Labels");
        addressSaver = AddressSaver.restorePasswords();
        cameraGroups = new CameraGroup[4];

        for (int groupNumber = 0; groupNumber < 4; groupNumber++) {
            CameraGroup cameraGroup = new CameraGroup(groupNumber + 1);
            Camera[] cameraArray = new Camera[2];
            for (int cameraNumber = 1; cameraNumber < 3; cameraNumber++) {
                cameraArray[cameraNumber - 1] = new Camera(groupNumber * 2 + cameraNumber, cameraGroup);
            }
            cameraGroup.setCameras(cameraArray);

            File imageFile = new File("C:\\LIGHTNING_STABLE\\buff\\" + (groupNumber + 1) + ".jpg");
            if (imageFile.exists()) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(imageFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                cameraGroup.setBackGroundImage(bufferedImage);
            }

            cameraGroups[groupNumber] = cameraGroup;
        }
        colorRGBNumberSet = new HashSet<>();
        linePoints = new HashMap<>();
        linesForHideZoneParsing = new HashMap<>();
        pixelsSizesForHideZoneParsingMap = new TreeMap<>();
        lengthOfViewArcMap = new HashMap<>();
    }

    public static void startAllCameras() {

        for (int groupNumber = 0; groupNumber < 4; groupNumber++) {
            for (int cameraNumber = 0; cameraNumber < 2; cameraNumber++) {
                Camera camera = cameraGroups[groupNumber].getCameras()[cameraNumber];
                String[] strings = addressSaver.getCamerasIdentificationInformation()[groupNumber * 2 + cameraNumber];
                camera.startReceiveVideoThread(strings[0], strings[1], strings[2]);
            }
        }
        if (soundSaver == null) {
            Thread gh = new Thread(() -> {
                String audioAddress = Storage.getAddressSaver().getAudioAddress();
                if (audioAddress != null && audioAddress.length() > 5) {
                    soundSaver = new SoundSaver(audioAddress);
                    soundSaver.SETUP();
                    soundSaver.PLAY();
                }
            });
            gh.start();
        } else {
            soundSaver.TEARDOWN();
            soundSaver = null;
        }
    }

    public static void addLinePoint(int groupNumber, int[][] list, boolean editOrStartApp) {
        linePoints.put(groupNumber, list);
        if (editOrStartApp) {
            addressSaver.saveLinePoints(groupNumber, list);
        }

        if (list != null) {
            linesForHideZoneParsing.put(groupNumber, getLineForParsing(list, groupNumber));
        }
    }

    private static double[] getPixelsSizesForHideZoneParsing(int groupNumber, List<int[]> linesForHideZoneParsing) {
        double[] groupNumberViewAnglesTangences = Storage.getAddressSaver().getCamerasViewAnglesTangens()[groupNumber - 1];
        double angleMin = Math.atan(groupNumberViewAnglesTangences[0]);//Math.atan(camerasViewAnglesTangens[i][0]))
        double angleMax = Math.atan(groupNumberViewAnglesTangences[1]);
        double angleIncrement = angleMax - angleMin;

        double distanceToSarcophagusRadius = Storage.getAddressSaver().getCamerasPosition()[groupNumber - 1][0] / Math.sin(angleMin);
        double lengthOfViewArc = distanceToSarcophagusRadius * Math.sqrt(2 - 2 * Math.cos(angleIncrement));

        lengthOfViewArcMap.put(groupNumber, lengthOfViewArc);
        double[] distances = new double[linesForHideZoneParsing.size()];
        double totalLineLength = 0.0;


        for (int i = 1; i < linesForHideZoneParsing.size(); i++) {
            int[] previousPoint = linesForHideZoneParsing.get(i - 1);
            int[] currentPoint = linesForHideZoneParsing.get(i);
            int horizontal = previousPoint[0] - currentPoint[0];
            int vertical = previousPoint[1] - currentPoint[1];
            double distanceBetweenPoints = Math.sqrt((Math.pow(horizontal, 2.0) +
                    Math.pow(vertical, 2.0)));
            distances[i - 1] = distanceBetweenPoints;
            totalLineLength += distanceBetweenPoints;
        }

        int x = linesForHideZoneParsing.get(0)[0] - linesForHideZoneParsing.get(linesForHideZoneParsing.size() - 1)[0];
        int y = linesForHideZoneParsing.get(0)[1] - linesForHideZoneParsing.get(linesForHideZoneParsing.size() - 1)[1];

        double ratio = lengthOfViewArc / totalLineLength;
        double distanceBetweenPoints = Math.sqrt((Math.pow(x, 2.0) +
                Math.pow(y, 2.0)));
        double d;

        for (int i = 0; i < distances.length; i++) {
            d = distances[i];
            distances[i] = d * ratio;
        }
        return distances;
    }

    public static Map<Integer, Double> getLengthOfViewArcMap() {
        return lengthOfViewArcMap;
    }

    private static List<int[]> getLineForParsing(int[][] linePoints, int groupNumber) {
        List<int[]> listToReturn = new LinkedList<>();
        double[][] pointsToDrawLine;
        double[] onePointToDrawLine = new double[2];

        pointsToDrawLine = new double[4][];
        for (int i = 1; i < 5; i++) {
            pointsToDrawLine[i - 1] = new double[2];
        }

        for (int i = 0; i < linePoints.length; i++) {
            if (linePoints.length > i + 3) {
                for (int j = 0; j < 4; j++) {
                    int pointNumber = i + j;
                    pointsToDrawLine[j][0] = linePoints[pointNumber][0];
                    pointsToDrawLine[j][1] = linePoints[pointNumber][1];
                }

//                for (double t = 0; t < 1; t += 0.005) {
//                for (double t = 0; t < 1; t += 0.01) {
                for (double t = 0; t < 1; t += 0.005) {
                    BackgroundImagePanel.eval(onePointToDrawLine, pointsToDrawLine, t);
                    listToReturn.add(new int[]{(int) onePointToDrawLine[0], (int) onePointToDrawLine[1]});
                }
            }
        }
        pixelsSizesForHideZoneParsingMap.put(groupNumber, getPixelsSizesForHideZoneParsing(groupNumber, listToReturn));
        return listToReturn;
    }

    public static CameraGroup[] getCameraGroups() {
        return cameraGroups;
    }

    public static Map<Integer, int[][]> getLinePoints() {
        return linePoints;
    }

    public static void setLinePoints(Map<Integer, int[][]> linePoints) {
        Storage.linePoints = linePoints;
    }

    public static Map<Integer, List<int[]>> getLinesForHideZoneParsing() {
        return linesForHideZoneParsing;
    }

    public static void setLinesForHideZoneParsing(Map<Integer, List<int[]>> linesForHideZoneParsing) {
        Storage.linesForHideZoneParsing = linesForHideZoneParsing;
    }

    public static AddressSaver getAddressSaver() {
        return addressSaver;
    }

    public static void setAddressSaver(AddressSaver addressSaver) {
        Storage.addressSaver = addressSaver;
    }

    public static float getOpacitySetting() {
        return (float) opacitySetting / 100;
    }

    public static void setOpacitySetting(int opacitySetting) {
        Storage.opacitySetting = opacitySetting;
        MainFrame.setOpacitySettingToFrame(opacitySetting);
    }

    public static int getSecondsToSave() {
        return secondsToSave;
    }

    public static void setSecondsToSave(int secondsToSave) {
        Storage.secondsToSave = secondsToSave;
        MainFrame.setSecondsToSaveToFrame(secondsToSave);
    }

    public static int getPercentDiffWhite() {
        return percentDiffWhite;
    }

    public static void setPercentDiffWhite(int percentDiffWhite) {
        Storage.percentDiffWhite = percentDiffWhite;
        MainFrame.setPercentDiffWhiteToFrame(percentDiffWhite);
    }

    public static int getColorLightNumber() {
        return colorLightNumber;
    }

    public static void setColorLightNumber(int colorLightNumber) {
        Storage.colorLightNumber = colorLightNumber;
        MainFrame.setColorLightNumberToFrame(colorLightNumber);
        Thread colorNumbersUpdateThread = new Thread(() -> {
            colorRGBNumberSet.clear();
            for (int i = colorLightNumber; i < 256; i++) {
                for (int k = colorLightNumber; k < 256; k++) {
                    for (int g = colorLightNumber; g < 256; g++) {//238
                        if (i == 238 & k == 238 & g == 238) {
                            continue;
                        }
                        colorRGBNumberSet.add(new Color(i, k, g).getRGB());
                    }
                }
            }
        });
        colorNumbersUpdateThread.start();
    }


    public static Set<Integer> getColorRGBNumberSet() {
        return colorRGBNumberSet;
    }

    public static boolean isProgramLightCatchEnable() {
        return programLightCatchEnable;
    }

    public static void setProgramLightCatchEnable(boolean programLightCatchEnable) {
        Storage.programLightCatchEnable = programLightCatchEnable;
        MainFrame.setProgramLightCatchEnableToFrame(programLightCatchEnable);
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Storage.port = port;
        MainFrame.setPortToFrame(port);
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Storage.path = path;
    }

    public static String getDefaultPath() {
        return defaultPath;
    }

    public static void setDefaultPath(String defaultPath) {
        Storage.defaultPath = defaultPath;
    }

    public static SoundSaver getSoundSaver() {
        return soundSaver;
    }

    public void setSoundSaver(SoundSaver soundSaver) {
        this.soundSaver = soundSaver;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setBundle(ResourceBundle bundle) {
        Storage.bundle = bundle;
    }

    public static int getShowFramesPercent() {
        return showFramesPercent;
    }

    public static void setShowFramesPercent(int showFramesPercent) {
        Storage.showFramesPercent = showFramesPercent;
        MainFrame.setShowFramesPercentToFrame(showFramesPercent);
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Storage.password = password;
    }

    public static Map<Integer, double[]> getPixelsSizesForHideZoneParsingMap() {
        return pixelsSizesForHideZoneParsingMap;
    }
}
