package entity;

import entity.Camera.ServiceCamera;
import entity.Storage.Storage;
import org.apache.log4j.Logger;
import ui.main.MainFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HideZoneLightingSearcher {

    private static Logger log = Logger.getLogger("admin");
    private static Map<Integer, BufferedImage> images;
    private static char[] alphabet;

    static {
        alphabet = new char[26];
        for (int i = 0; i < 26; i++) {
            alphabet[i] = (char) ('a' + i);
        }
    }

    public static void findHideZoneAreaAndRenameFolder(File folderWithFiles) {
        if (!folderWithFiles.getName().contains("{")&&!folderWithFiles.getName().contains("wav")) {
            MainFrame.showInformMassage(Storage.getBundle().getString("startedprocessing"), new Color(23, 114, 26));
            try {
                Date date = new Date(Long.parseLong(folderWithFiles.getName()));
                log.info("Date - " + date.toString());
            } catch (Exception i) {
                log.error("Date - " + folderWithFiles.getName());
            }
            StringBuilder stringBuilder = new StringBuilder();
            File[] foldersFromEachCamera = folderWithFiles.listFiles();
            if (foldersFromEachCamera != null) {
                Map<Integer, File>[] maps = new Map[2];
                maps[0] = new HashMap<>();
                maps[1] = new HashMap<>();
                Map<Integer, File> foldersFromEachCameraMap = new HashMap<>();
                Map<Integer, List<Integer>> eventsNumbersMap = new HashMap<>();
                List<Integer> eventFramesNumberList = null;
                for (File oneFolderFromCamera : foldersFromEachCamera) {
                    eventFramesNumberList = new ArrayList<>();
                    String nameOfFolder = oneFolderFromCamera.getName();
                    if (!nameOfFolder.contains(".jpg")) {
                        int cameraGroupNumber = Integer.parseInt(nameOfFolder.substring(0, 1));
                        String eventsNumbersString = nameOfFolder.substring(nameOfFolder.indexOf('[') + 1, nameOfFolder.length() - 1);
                        if (eventsNumbersString.contains(",")) {
                            String[] split = eventsNumbersString.split(",");
                            for (String numberString : split) {
                                addEventNumberToList(numberString, eventFramesNumberList);
                            }
                        } else {
                            addEventNumberToList(eventsNumbersString, eventFramesNumberList);
                        }
                        eventsNumbersMap.put(cameraGroupNumber, eventFramesNumberList);

                        if (cameraGroupNumber < 3) {
                            maps[0].put(cameraGroupNumber, oneFolderFromCamera);
                        } else {
                            maps[1].put(cameraGroupNumber, oneFolderFromCamera);
                        }
                        foldersFromEachCameraMap.put(cameraGroupNumber, oneFolderFromCamera);
                    }
                }
                int size = 0;
                for (Integer gr : eventsNumbersMap.keySet()) {
                    size = eventsNumbersMap.get(gr).size();
                    if (size > 0) {
                        break;
                    }
                }

                for (int i = 0; i < size; i++) {
                    Double[] angles = new Double[4];
                    for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
                        List<Integer> integers = eventsNumbersMap.get(groupNumber);
                        Double angle = null;
                        if (integers != null) {
                            BufferedImage mostWhiteImage = getMostWhiteImage(getFramesWithLightning(foldersFromEachCameraMap.get(groupNumber),
                                    integers.get(i)));
                            if (mostWhiteImage != null) {
                                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
                                if (cameraLinePoints == null) {
                                    log.error("No border hide zone for group number - " + i);
                                    continue;
                                }
                                Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, mostWhiteImage);
                                if (numberOfLinePointCutLightning != null) {
                                    int distanceFromBeginningSarcophagusToPlaceLightningCutLine = getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(numberOfLinePointCutLightning, groupNumber);
                                    angle = getAngleFromCameraGroup(groupNumber, distanceFromBeginningSarcophagusToPlaceLightningCutLine);
                                }
                            }
                        }
                        angles[groupNumber - 1] = angle;
                    }
                    String zoneName = getZoneName(angles);
                    stringBuilder.append(zoneName);
                    if (i != size - 1) {
                        stringBuilder.append(',');
                    }
                }
            }
            Path moveFrom = Paths.get(folderWithFiles.getAbsolutePath());
            Path moveTo = Paths.get(folderWithFiles.getAbsolutePath() + "{" + stringBuilder.toString() + "}");
            renameVideo(moveFrom, moveTo, 0);
        }
    }


    private static void renameVideo(Path moveFrom, Path moveTo, int number) {
        try {
            Files.move(moveFrom, moveTo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            renameVideo(moveFrom, moveTo, number);
            e.printStackTrace();
        }
    }

    private static int getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(int numberOfLinePoint, int groupNumber) {
        double lengthOfArc = 0;
        double[] distances = Storage.getPixelsSizesForHideZoneParsingMap().get(groupNumber);
        for (int i = 0; i <= numberOfLinePoint; i++) {
            lengthOfArc += distances[i];
        }

        if (groupNumber % 2 != 0) {
            lengthOfArc = Storage.getLengthOfViewArcMap().get(groupNumber) - lengthOfArc;
        }

        double distanceToSarcophagus = Storage.getAddressSaver().getDistancesToSarcophagus()[groupNumber - 1];
        double lengthOfViewZone = Storage.getLengthOfViewArcMap().get(groupNumber);

        double mainViewAngle = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[groupNumber - 1][1]) -
                Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[groupNumber - 1][0]);

        double mainSectorAngle = lengthOfArc * mainViewAngle / lengthOfViewZone;
        double secondAngleMinOfCameraGroups = Math.PI - (Math.PI / 2 - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[groupNumber - 1][0]));
        double thirdAngle = Math.PI - mainSectorAngle - secondAngleMinOfCameraGroups;

        double distanceToLightningCutTheLine = distanceToSarcophagus * Math.sin(mainSectorAngle) / Math.sin(thirdAngle);
        return (int) distanceToLightningCutTheLine;
    }

    private static String getZoneName(Double[] angles) {
        String[] names = new String[4];
        for (int i = 0; i < 4; i++) {
            Double angeFirst = angles[i];
            Double angleSecond;
            if (i != 3) {
                angleSecond = angles[i + 1];
            } else {
                angleSecond = angles[0];
            }

            if (angeFirst != null && angleSecond != null) {
                System.out.println("making calculation ");
                if (i % 2 != 0) {
                    angeFirst = (Math.PI / 2) - angeFirst;
                    angleSecond = (Math.PI / 2) - angleSecond;
                }
                int secondGroup = i + 1;
                if (secondGroup == 4) {
                    secondGroup = 0;
                }
                int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();
                int bs;
                int as;
                int L;
                if (i % 2 == 0) {
                    bs = camerasPosition[secondGroup][1] - camerasPosition[i][1];
                    as = (int) (Math.tan(angleSecond) * bs);
                    L = camerasPosition[i][0] + 164 + camerasPosition[secondGroup][0] - as;
                } else {
                    bs = camerasPosition[secondGroup][0] - camerasPosition[i][0];
                    as = (int) (Math.tan(angleSecond) * bs);
                    L = camerasPosition[i][1] + 270 + camerasPosition[secondGroup][1] - as;
                }

                double angle = angeFirst + angleSecond;
                int disToLightningFromCameraOne = (int) (L / Math.sin(angle) * Math.sin(Math.PI / 2 - angleSecond));
                int firstСathetus = (int) (disToLightningFromCameraOne * Math.cos(Math.PI / 2 - angeFirst));
                int secondСathetus = (int) (disToLightningFromCameraOne * Math.sin(Math.PI / 2 - angeFirst));

                int numberOfHideZone;
                String letterOfZone;
                String zoneName = null;

                int distanceToLightning;
                if (i % 2 == 0) {
                    numberOfHideZone = ((firstСathetus - camerasPosition[i][0]) / 10) + 1;
                    distanceToLightning = secondСathetus - camerasPosition[i][1];
                    if (i != 0) {
                        numberOfHideZone = 17 - numberOfHideZone;
                    }
                } else {
                    numberOfHideZone = ((secondСathetus - camerasPosition[i][0]) / 10) + 1;
                    distanceToLightning = firstСathetus - camerasPosition[i][1];
                    if (i == 1) {
                        numberOfHideZone = 17 - numberOfHideZone;
                    }
                }
                if (numberOfHideZone == 0) {
                    numberOfHideZone++;
                }
                for (int letter = 0; letter < 10; letter++) {
                    if (i <= 1) {
                        int zoneChar = 175 - letter * 10;
                        if (distanceToLightning > zoneChar) {
                            letterOfZone = String.valueOf(alphabet[letter]);
                            zoneName = letterOfZone + numberOfHideZone;
                            break;
                        }
                    } else {
                        int zoneCharSize = 180 - letter * 10;
                        if (distanceToLightning > zoneCharSize) {
                            letterOfZone = String.valueOf(alphabet[9 - letter]);
                            zoneName = letterOfZone + numberOfHideZone;
                            break;
                        }
                    }
                }
                names[i] = zoneName;
            }
        }

        String zoneName = null;
        int countEqualZoneNames = 0;
        for (int i = 0; i < 4; i++) {
            String s = names[i];
            if (s != null) {
                log.info("Group number " + (i + 1) + " calculates zone number " + s);
                System.out.println("Group number " + (i + 1) + " calculates zone number " + s);
                if (zoneName == null) {
                    zoneName = s;
                }
                int n = 0;
                for (int k = 0; k < 4; k++) {
                    if (k != i &&
                            names[k] != null &&
                            s.compareTo(names[k]) == 0) {
                        n++;
                    }
                }
                if (n > countEqualZoneNames) {
                    countEqualZoneNames = n;
                    zoneName = s;
                } else {
                    if (i == 0 || i == 2) {
                        zoneName = s;
                    }
                }
            }
        }

        if (zoneName == null) {
            zoneName = "NO DATA";
            MainFrame.showInformMassage(Storage.getBundle().getString("NODATA"), new Color(23, 114, 26));
            log.info("Any lightnings inside hide zone.");
        }
        return zoneName;
    }

    private static double getAngleFromCameraGroup(int groupNumber,
                                                  int distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst) {
        int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();
        double fistCameraСathetusOne = (double) (distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst) + camerasPosition[groupNumber - 1][0];
        double fistCameraСathetusTwo = (double) camerasPosition[groupNumber - 1][1] + camerasPosition[groupNumber - 1][2];
        return Math.atan(fistCameraСathetusOne / fistCameraСathetusTwo);
    }

    private static List<BufferedImage> getFramesWithLightning(File folder, int frameNumber) {
        List<BufferedImage> imagesToReturn = new LinkedList<>();
        Map<File, Integer> framesInFiles = new TreeMap<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String[] split = file.getName().split("\\.");
                    String[] lastSplit = split[0].split("-");
                    String countFramesString = lastSplit[1];
                    int i = Integer.parseInt(countFramesString);
                    framesInFiles.put(file, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int firstFrameNumber = frameNumber - 6;
            int lastFrameNumber = frameNumber + 5;

            if (firstFrameNumber < 0) {
                firstFrameNumber = 0;
            }
            int currentCount = 0;
            for (File file : framesInFiles.keySet()) {
                Integer framesInCurrentFile = framesInFiles.get(file);
                currentCount += framesInCurrentFile;
                if (currentCount >= firstFrameNumber) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream();

                        int x = 0;
                        int t = 0;
                        int currentReadImageNumber = currentCount - framesInCurrentFile;
                        while (true) {
                            t = x;
                            try {
                                x = bufferedInputStream.read();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            temporaryStream.write(x);
                            if (x == 216 && t == 255) {// начало изображения
                                temporaryStream.reset();
                                temporaryStream.write(t);
                                temporaryStream.write(x);
                            } else if (x == 217 && t == 255) {//конец изображения
                                byte[] imageBytes = temporaryStream.toByteArray();
                                currentReadImageNumber++;
                                if (currentReadImageNumber >= firstFrameNumber ||
                                        currentReadImageNumber <= lastFrameNumber) {
                                    imagesToReturn.add(readImage(imageBytes));
                                    if (currentReadImageNumber == lastFrameNumber) {
                                        return imagesToReturn;
                                    }
                                }
                            }
                            if (x < 0) {
                                firstFrameNumber = ++currentReadImageNumber;
                                break;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return imagesToReturn;
    }

    private static BufferedImage readImage(byte[] imageBytes) {
        BufferedImage bufferedImage = null;
        if (imageBytes != null) {
            try {
                ByteArrayInputStream inputImageStream = new ByteArrayInputStream(imageBytes);
                ImageIO.setUseCache(false);
                bufferedImage = ImageIO.read(inputImageStream);
                inputImageStream.close();
            } catch (Exception ignored) {
            }
        }
        return bufferedImage;
    }

    private static void addEventNumberToList(String numberString, List<Integer> eventFramesNumberList) {
        if (numberString.contains("(")) {
            eventFramesNumberList.add(Integer.parseInt(numberString.substring(1, numberString.length() - 1)));
        } else {
            eventFramesNumberList.add(Integer.parseInt(numberString));
        }
    }

    private static BufferedImage getMostWhiteImage(List<BufferedImage> bufferedImageList) {
        BufferedImage imageToReturn = null;
        int countWhitePixels = 0;
        for (BufferedImage image : bufferedImageList) {
            if (image != null) {
                int countWhitePixelsCurrentImage = 0;
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (Storage.getColorRGBNumberSet().contains(image.getRGB(x, y))) {
                            countWhitePixelsCurrentImage++;
                        }
                    }
                }
                if (countWhitePixelsCurrentImage > countWhitePixels) {
                    imageToReturn = image;
                }
            }
        }
        return imageToReturn;
    }

    private static Integer getNumberOfLinePoint(List<int[]> cameraGroupLinePoints, BufferedImage bi) {
        Deque<int[]> points = new ConcurrentLinkedDeque<>();
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                if (Storage.getColorRGBNumberSet().contains(bi.getRGB(x, y))) {
                    int[] lastWhitePoint = new int[]{x - 1, y - 1};
                    points.addFirst(lastWhitePoint);
                    if (points.size() > 10) {
                        points.pollLast();
                    }
                }
            }
        }

        Integer linePointNumber = null;
        int countOfEqualPixels = 0;
        for (int i = 0; i < cameraGroupLinePoints.size(); i++) {
            int equals = 0;
            for (int[] p : points) {
                boolean equal = comparePoints(cameraGroupLinePoints.get(i), p, Storage.getAddressSaver().getHideZoneIdentificationAccuracyComparePixels());
                if (equal) {
                    equals++;
                }
            }
            if (equals > countOfEqualPixels) {
                countOfEqualPixels = equals;
                linePointNumber = i;
            }
        }
        return linePointNumber;
    }

    private static boolean comparePoints(int[] linePoint, int[] lightningPoint, int accuracy) {
        return Math.abs(linePoint[0] - lightningPoint[0]) < accuracy && Math.abs(linePoint[1] - lightningPoint[1]) < accuracy;
    }

    public static String getZoneNameTest() {
        log.info("TEST ");
        log.info("==========================================================");
        Double[] angles = new Double[4];
        for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
            BufferedImage testImage = Storage.getCameraGroups()[groupNumber - 1].getBackGroundImage();
            Double angle = null;
            if (testImage != null) {
                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
                if (cameraLinePoints != null) {
                    Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, testImage);
                    if (numberOfLinePointCutLightning != null) {
                        int distanceFromBeginningSarcophagusToPlaceLightningCutLine = getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(numberOfLinePointCutLightning, groupNumber);
                        angle = getAngleFromCameraGroup(groupNumber, distanceFromBeginningSarcophagusToPlaceLightningCutLine);
                    }
                }
            }
            angles[groupNumber - 1] = angle;
        }

        String zoneName = getZoneName(angles);
        log.info("==========================================================");
        return zoneName;
    }

    public static void createTestImageForCameraThreeAndFour(String zoneName) {
        if (zoneName != null) {

            if (images == null) {
                images = new HashMap<>();
                for (int i = 1; i < 5; i++) {
                    images.put(i, Storage.getCameraGroups()[i - 1].getBackGroundImage());
                }
            }

            List<Character> alphabetList = new ArrayList<>();
            char[] alphabet = new char[26];
            for (int i = 0; i < 11; i++) {
                alphabetList.add((char) ('a' + i));
                alphabet[i] = (char) ('a' + i);
            }
            String letter = zoneName.substring(0, 1);
            String number = zoneName.substring(1, zoneName.length());
            int numberInt = Integer.parseInt(number);
            int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();

            int distance = 0;
            int letterNumber = 0;
            for (int i = 0; i < 11; i++) {
                if (i == 0 || i == 10) {
                    distance += 3;
                } else {
                    distance += 10;
                }
                if (String.valueOf(alphabet[i]).compareTo(letter) == 0) {
                    letterNumber = i;
                    break;
                }
            }
//            -----------------------------------------------------
            BufferedImage backGroundImageOne = images.get(1);
            if (backGroundImageOne != null) {
                Integer k1Horizontal = camerasPosition[0][0] + numberInt * 10 - 5;
                Integer k1Vertical = camerasPosition[0][1] + camerasPosition[0][2] + 80 - distance + 5;
                double angleOne = Math.atan((double) k1Horizontal / k1Vertical);
                int k1sVertical = 80 - distance + 5;
                int k1sHorizontal = (int) (Math.tan(angleOne) * k1sVertical);
                int k1LHorizontal = numberInt * 10 - 5 - k1sHorizontal;
                if (k1LHorizontal > 0) {
                    double lengthOfViewZone = Storage.getLengthOfViewArcMap().get(1);
                    double mainViewAngle = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[0][1]) -
                            Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[0][0]);
                    double angle = angleOne - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[0][0]);
                    int distanceToDraw = (int) (angle * lengthOfViewZone / mainViewAngle);
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(1);
                    if (pixelsSizesForHideZoneParsing != null) {
                        double disPix = 0.0;
                        int n = 0;
                        for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                            disPix += pixelsSizesForHideZoneParsing[i];
                            if (disPix > distanceToDraw) {
                                n = i;
                                break;
                            }
                        }
                        int[] lightningPoint = Storage.getLinesForHideZoneParsing().get(1).get(n);
                        drawLightningToImage(backGroundImageOne, lightningPoint[0], lightningPoint[1], 1, zoneName);
                    }
                } else {
                    double atan = Math.atan((double) (camerasPosition[0][0] + k1LHorizontal) / (camerasPosition[0][1] + camerasPosition[0][2]));
                    double deltaA = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[0][0]) - atan;//в радианах
                    double distanceToDraw = (Math.abs(deltaA * ((camerasPosition[0][1] + camerasPosition[0][2]) / Math.cos(angleOne))));
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(1);
                    if (pixelsSizesForHideZoneParsing != null) {
                        double disPix = 0.0;
                        int n = 0;
                        for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                            disPix += pixelsSizesForHideZoneParsing[i];
                            if (disPix > distanceToDraw) {
                                n = i;
                                break;
                            }
                        }
                        int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(1).get(n);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(1);
                        int[] lastPoint = list.get(list.size() - 1);
                        disPix = Math.abs(lastPoint[0] - lightningPointOne[0]);
                        drawLightningToImage(backGroundImageOne, (int) (lastPoint[0] + disPix), lastPoint[1], 1, zoneName);
                    }
                }
            }
//            ======================================================================
            BufferedImage backGroundImageSecond = images.get(2);
            if (backGroundImageSecond != null) {
                Integer k2Horizontal = camerasPosition[1][0] + 164 - (numberInt * 10 - 5);
                Integer k2Vertical = camerasPosition[1][1] + camerasPosition[1][2] + 80 - (distance - 5);
                double angleSecond = Math.atan((double) k2Horizontal / k2Vertical);
                int k2sVertical = 80 - (distance - 5);
                int k2sHorizontal = (int) (Math.tan(angleSecond) * k2sVertical);
                int k2LHorizontal = 164 - (numberInt * 10 - 5) - k2sHorizontal;
                if (k2LHorizontal > 0) {
                    double lengthOfViewZone = Storage.getLengthOfViewArcMap().get(2);
                    double mainViewAngle = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[1][1]) -
                            Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[1][0]);
                    double angle = angleSecond - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[1][0]);
                    int distanceToDraw = (int) (angle * lengthOfViewZone / mainViewAngle);
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(2);
                    int n = 0;
                    double disPix = 0.0;
                    if (k2LHorizontal < 164) {
                        for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                            disPix += pixelsSizesForHideZoneParsing[i];
                            if (disPix > distanceToDraw) {
                                n = i;
                                System.out.println(disPix + ">" + distanceToDraw + " - n=" + n);
                                break;
                            }
                        }
                    } else {
                        n = pixelsSizesForHideZoneParsing.length - 1;
                    }
                    int[] lightningPointSecond = Storage.getLinesForHideZoneParsing().get(2).get(n);
                    drawLightningToImage(backGroundImageSecond, lightningPointSecond[0], lightningPointSecond[1], 2, zoneName);
                } else {
                    double atan = Math.atan((double) (camerasPosition[1][0] + k2LHorizontal) / (camerasPosition[1][1] + camerasPosition[1][2]));
                    double deltaA = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[1][0]) - atan;//в радианах
                    double distanceToDraw = (Math.abs(deltaA * ((camerasPosition[1][1] + camerasPosition[1][2]) / Math.cos(angleSecond))));
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(1);
                    if (pixelsSizesForHideZoneParsing != null) {
                        double disPix = 0.0;
                        int n = 0;
                        if (k2LHorizontal < 164) {
                            for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                                disPix += pixelsSizesForHideZoneParsing[i];
                                if (disPix > distanceToDraw) {
                                    n = i;
                                    System.out.println(disPix + ">" + distanceToDraw + " - n=" + n);
                                    break;
                                }
                            }
                        } else {
                            n = pixelsSizesForHideZoneParsing.length - 1;
                        }

                        int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(2).get(n);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(2);
                        int[] firstPoint = list.get(0);
                        disPix = Math.abs(firstPoint[0] - lightningPointOne[0]);
                        drawLightningToImage(backGroundImageSecond, (int) (firstPoint[0] - disPix), firstPoint[1], 2, zoneName);
                    }
                }
            }
//====================================================================
            BufferedImage backGroundImageThree = images.get(3);
            if (backGroundImageThree != null) {
                Integer k3Horizontal = camerasPosition[2][0] + 164 - numberInt * 10 + 5;
                Integer k3Vertical = camerasPosition[2][1] + camerasPosition[2][2] + distance - 5;

                double angleThird = Math.atan((double) k3Horizontal / k3Vertical);
                int k3sVertical = distance - 5;
                int k3sHorizontal = (int) (Math.tan(angleThird) * k3sVertical);
                int k3LHorizontal = 164 - numberInt * 10 + 5 - k3sHorizontal;

                System.out.println("distance " + k3LHorizontal);
                if (k3LHorizontal > 0) {
                    double lengthOfViewZone = Storage.getLengthOfViewArcMap().get(3);
                    double mainViewAngle = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[2][1]) -
                            Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[2][0]);
                    double angle = angleThird - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[2][0]);
                    int distanceToDraw = (int) (angle * lengthOfViewZone / mainViewAngle);
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(3);
                    double disPix = 0.0;
                    int n = 0;
                    for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                        disPix += pixelsSizesForHideZoneParsing[i];
                        if (disPix > distanceToDraw) {
                            n = i;
                            break;
                        }
                    }
                    int[] lightningPointThree = Storage.getLinesForHideZoneParsing().get(3).get(n);
                    drawLightningToImage(backGroundImageThree, lightningPointThree[0], lightningPointThree[1], 3, zoneName);
                } else {
                    double atan = Math.atan((double) (camerasPosition[2][0] + k3LHorizontal) / (camerasPosition[2][1] + camerasPosition[2][2]));
                    double deltaA = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[2][0]) - atan;//в радианах
                    double distanceToDraw = (Math.abs(deltaA * ((camerasPosition[2][1] + camerasPosition[2][2]) / Math.cos(angleThird))));
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(3);
                    if (pixelsSizesForHideZoneParsing != null) {
                        double disPix = 0.0;
                        int n = 0;
                        for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                            disPix += pixelsSizesForHideZoneParsing[i];
                            if (disPix > distanceToDraw) {
                                n = i;
                                break;
                            }
                        }
                        int[] lightningPointThree = Storage.getLinesForHideZoneParsing().get(3).get(n);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(3);
                        int[] lastPoint = list.get(list.size() - 1);
                        disPix = Math.abs(lastPoint[0] - lightningPointThree[0]);
                        drawLightningToImage(backGroundImageThree, (int) (lastPoint[0] + disPix), lastPoint[1], 3, zoneName);
                    }
                }
            }
//            ======================================================================
            BufferedImage backGroundImageFourth = images.get(4);
            if (backGroundImageFourth != null) {
                Integer k4Horizontal = camerasPosition[3][0] + numberInt * 10 - 5;
                Integer k4Vertical = camerasPosition[3][1] + camerasPosition[3][2] + distance - 5;
                double angleFourth = Math.atan((double) k4Horizontal / k4Vertical);
                int k4sVertical = distance - 5;
                int k4sHorizontal = (int) (Math.tan(angleFourth) * k4sVertical);
                int k4LHorizontal = numberInt * 10 - 5 - k4sHorizontal;
                if (k4LHorizontal > 0) {
                    double lengthOfViewZone = Storage.getLengthOfViewArcMap().get(4);
                    double mainViewAngle = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[3][1]) -
                            Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[3][0]);
                    double angle = angleFourth - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[3][0]);
                    int distanceToDraw = (int) (angle * lengthOfViewZone / mainViewAngle);
                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(4);
                    double disPix = 0.0;
                    int n = 0;
                    for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                        disPix += pixelsSizesForHideZoneParsing[i];
                        if (disPix > distanceToDraw) {
                            n = i;
                            break;
                        }
                    }
                    int[] lightningPointFourth = Storage.getLinesForHideZoneParsing().get(4).get(n);
                    drawLightningToImage(backGroundImageFourth, lightningPointFourth[0], lightningPointFourth[1], 4, zoneName);
                } else {
                    double atan = Math.atan((double) (camerasPosition[3][0] + k4LHorizontal) / (camerasPosition[3][1] + camerasPosition[3][2]));
                    double deltaA = Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[3][0]) - atan;//в радианах
                    double distanceToDraw = (Math.abs(deltaA * ((camerasPosition[3][1] + camerasPosition[3][2]) / Math.cos(angleFourth))));

                    double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(4);
                    if (pixelsSizesForHideZoneParsing != null) {
                        double disPix = 0.0;
                        int n = 0;
                        for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                            disPix += pixelsSizesForHideZoneParsing[i];
                            if (disPix > distanceToDraw) {
                                n = i;
                                break;
                            }
                        }
                        int[] lightningPointFourth = Storage.getLinesForHideZoneParsing().get(4).get(n);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(4);
                        int[] firstPoint = list.get(0);
                        disPix = Math.abs(firstPoint[0] - lightningPointFourth[0]);
                        drawLightningToImage(backGroundImageFourth, (int) (firstPoint[0] - disPix), firstPoint[1], 4, zoneName);
                    }
                }
            }
        }
    }

    private static void drawLightningToImage(BufferedImage bufferedImage, int x, int y, int groupNumber, String zoneName) {
        BufferedImage backImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D g1 = (Graphics2D) backImage.getGraphics();
        g1.setColor(Color.BLACK);
        g1.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        BufferedImage image = ServiceCamera.connectImage(bufferedImage, backImage, 0.5f);
        Graphics2D g = (Graphics2D) image.getGraphics();
        BasicStroke pen1 = new BasicStroke(4);
        g.setStroke(pen1);
        g.setColor(Color.WHITE);
        g.drawLine(x, y, x, 0);
        Storage.getCameraGroups()[groupNumber - 1].setBackGroundImage(image);
//        ====================================TEST======================================================================
        if (image != null) {
            File imageFile = new File(Storage.getPath() + "\\" + groupNumber + " - TEST " + zoneName + ".jpg");
            try {
                if (imageFile.createNewFile()) {
                    ImageIO.write(image, "jpg", imageFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
