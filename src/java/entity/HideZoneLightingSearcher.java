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
        if (!folderWithFiles.getName().contains("{") && !folderWithFiles.getName().contains("wav")) {
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
        double[] distances = Storage.getPixelsSizesForHideZoneParsingMap().get(groupNumber);
        double distance = distances[numberOfLinePoint];
        if (groupNumber % 2 != 0) {
            distance = 164 - distance;
        }
        return (int) distance;
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
                if (numberOfHideZone > 16) {
                    numberOfHideZone = 16;
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

        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < 4; i++) {
            String s = names[i];
            if (s != null) {
                log.info("Group number " + (i + 1) + " calculates zone number " + s);
                System.out.println("Group number " + (i + 1) + " calculates zone number " + s);
                if (map.containsKey(s)) {
                    Integer integer = map.get(s);
                    integer++;
                    map.put(s, integer);
                } else {
                    map.put(s, 1);
                }
            }
        }

        for (String zone : map.keySet()) {
            Integer integer = map.get(zone);
            if (integer > countEqualZoneNames) {
                countEqualZoneNames = integer;
                zoneName = zone;
            }
        }

//        for (int i = 0; i < 4; i++) {
//            String s = names[i];
//            if (s != null) {
//                log.info("Group number " + (i + 1) + " calculates zone number " + s);
//                System.out.println("Group number " + (i + 1) + " calculates zone number " + s);
//                if (zoneName == null) {
//                    zoneName = s;
//                }
//                int n = 0;
//                for (int k = 0; k < 4; k++) {
//                    if (k != i &&
//                            names[k] != null &&
//                            s.compareTo(names[k]) == 0) {
//                        n++;
//                    }
//                }
//                if (n > countEqualZoneNames) {
//                    countEqualZoneNames = n;
//                    zoneName = s;
//                } else {
//                    if (s.contains("j")) {
//                        if (i == 1 || i == 3) {
//                            zoneName = s;
//                        }
//                    } else {
//                        if (i == 0 || i == 2) {
//                            zoneName = s;
//                        }
//                    }
//
//
//                }
//            }
//        }

        if (zoneName == null) {
            zoneName = "NO DATA";
            MainFrame.showInformMassage(Storage.getBundle().getString("NODATA"), new Color(23, 114, 26));
            log.info("Any lightnings inside hide zone.");
        } else {
            MainFrame.showInformMassage(zoneName, new Color(23, 114, 26));
            log.info("Zone number - " + zoneName);
            System.out.println("Zone number - " + zoneName);
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

            int firstFrameNumber = frameNumber - 5;
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
                    countWhitePixels = countWhitePixelsCurrentImage;
                    imageToReturn = image;
                }
            }
        }

        File file = new File(Storage.getPath() + "\\" + System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            ImageIO.write(imageToReturn, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
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


    private static int getPixelNumber(double[] pixelsSizesForHideZoneParsing, double lHorizontal, int groupNubmer) {
        int n = 0;
        double diff = 165.0;
        double searchLength;
        searchLength = Math.abs(lHorizontal);
        if (groupNubmer % 2 != 0) {
            searchLength = (double) 164 - Math.abs(lHorizontal);
        }
        for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
            double abs = Math.abs(pixelsSizesForHideZoneParsing[i] - searchLength);
            if (abs < diff) {
                diff = abs;
                n = i;
            }
        }
        return n;
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
            int heightOfHideZone = 86;
            int distanceHorizontal;
            if (numberInt == 1) {
                distanceHorizontal = 1;
            } else if (numberInt == 16) {
                distanceHorizontal = 163;
            } else {
                distanceHorizontal = numberInt * 10 - 5;
            }
            int distanceVertical = 0;
            for (int i = 0; i < 11; i++) {
                if (i == 0 || i == 10) {
                    distanceVertical += 3;
                } else {
                    distanceVertical += 10;
                }
                if (String.valueOf(alphabet[i]).compareTo(letter) == 0) {
                    if (i != 0 && i != 10) {
                        distanceVertical -= 5;
                    } else {
                        if (i == 0) {
                            distanceVertical = 1;
                        } else {
                            distanceVertical--;
                        }
                    }
                    break;
                }
            }
//            -----------------------------------------------------
            BufferedImage backGroundImageOne = images.get(1);
            if (backGroundImageOne != null) {
                Integer k1Horizontal = camerasPosition[0][0] + distanceHorizontal;
                Integer k1Vertical = camerasPosition[0][1] + camerasPosition[0][2] + heightOfHideZone - distanceVertical;
                double angleOne = Math.atan((double) k1Horizontal / k1Vertical);
                int k1sVertical = heightOfHideZone - distanceVertical;
                int k1sHorizontal = (int) (Math.tan(angleOne) * k1sVertical);
                int k1LHorizontal = distanceHorizontal - k1sHorizontal;
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(1);
                if (pixelsSizesForHideZoneParsing != null) {
                    int n;
                    double disPix;
                    int x;
                    int y;
                    int[] lightningPointFirst;
                    if (k1LHorizontal > 0) {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k1LHorizontal, 1);
                        lightningPointFirst = Storage.getLinesForHideZoneParsing().get(1).get(n);
                        x = lightningPointFirst[0];
                        y = lightningPointFirst[1];
                    } else {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k1LHorizontal, 1);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(1);
                        lightningPointFirst = list.get(n);
                        int[] lastPoint = list.get(list.size() - 1);

                        int horizontal = lastPoint[0] - lightningPointFirst[0];
                        int vertical = lastPoint[1] - lightningPointFirst[1];
                        disPix = Math.sqrt((Math.pow(horizontal, 2.0) +
                                Math.pow(vertical, 2.0)));
                        x = (int) (lastPoint[0] + disPix);
                        y = lastPoint[1];
                    }
                    drawLightningToImage(backGroundImageOne, x, y, 1, zoneName);
                }
            }
//            ======================================================================
            BufferedImage backGroundImageSecond = images.get(2);
            if (backGroundImageSecond != null) {
                Integer k2Horizontal = camerasPosition[1][0] + 164 - distanceHorizontal;
                Integer k2Vertical = camerasPosition[1][1] + camerasPosition[1][2] + heightOfHideZone - distanceVertical;
                double angleSecond = Math.atan((double) k2Horizontal / k2Vertical);
                int k2sVertical = heightOfHideZone - distanceVertical;
                int k2sHorizontal = (int) (Math.tan(angleSecond) * k2sVertical);
                int k2LHorizontal = 164 - distanceHorizontal - k2sHorizontal;
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(2);
                if (pixelsSizesForHideZoneParsing != null) {
                    int n;
                    double disPix;
                    int x;
                    int y;
                    int[] lightningPointSecond;
                    if (k2LHorizontal > 0) {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k2LHorizontal, 2);
                        lightningPointSecond = Storage.getLinesForHideZoneParsing().get(2).get(n);
                        x = lightningPointSecond[0];
                        y = lightningPointSecond[1];
                    } else {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k2LHorizontal, 2);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(2);
                        lightningPointSecond = list.get(++n);
                        int[] firstPoint = list.get(0);
                        int horizontal = firstPoint[0] - lightningPointSecond[0];
                        int vertical = firstPoint[1] - lightningPointSecond[1];
                        disPix = Math.sqrt((Math.pow(horizontal, 2.0) +
                                Math.pow(vertical, 2.0)));
                        x = (int) (firstPoint[0] - disPix);
                        y = firstPoint[1];
                    }
                    drawLightningToImage(backGroundImageSecond, x, y, 2, zoneName);
                }
            }
            //====================================================================
            BufferedImage backGroundImageThree = images.get(3);
            if (backGroundImageThree != null) {
                Integer k3Horizontal = camerasPosition[2][0] + 164 - distanceHorizontal;
                Integer k3Vertical = camerasPosition[2][1] + camerasPosition[2][2] + distanceVertical;
                double angleThird = Math.atan((double) k3Horizontal / k3Vertical);
                int k3sHorizontal = (int) (Math.tan(angleThird) * distanceVertical);
                int k3LHorizontal = 164 - distanceHorizontal - k3sHorizontal;
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(3);
                if (pixelsSizesForHideZoneParsing != null) {
                    int n;
                    double disPix;
                    int x;
                    int y;
                    int[] lightningPointThree;
                    if (k3LHorizontal > 0) {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k3LHorizontal, 3);
                        lightningPointThree = Storage.getLinesForHideZoneParsing().get(3).get(n);
                        x = lightningPointThree[0];
                        y = lightningPointThree[1];
                    } else {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k3LHorizontal, 3);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(3);
                        lightningPointThree = list.get(n);
                        int[] lastPoint = list.get(list.size() - 1);

                        int horizontal = lastPoint[0] - lightningPointThree[0];
                        int vertical = lastPoint[1] - lightningPointThree[1];
                        disPix = Math.sqrt((Math.pow(horizontal, 2.0) +
                                Math.pow(vertical, 2.0)));
                        x = (int) (lastPoint[0] + disPix);
                        y = lastPoint[1];
                    }
                    drawLightningToImage(backGroundImageThree, x, y, 3, zoneName);
                }
            }
            //            ======================================================================
            BufferedImage backGroundImageFourth = images.get(4);
            if (backGroundImageFourth != null) {
                Integer k4Horizontal = camerasPosition[3][0] + distanceHorizontal;
                Integer k4Vertical = camerasPosition[3][1] + camerasPosition[3][2] + distanceVertical;
                double angleFourth = Math.atan((double) k4Horizontal / k4Vertical);
                int k4sHorizontal = (int) (Math.tan(angleFourth) * distanceVertical);
                int k4LHorizontal = distanceHorizontal - k4sHorizontal;
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(4);
                if (pixelsSizesForHideZoneParsing != null) {
                    int n;
                    double disPix;
                    int x;
                    int y;
                    int[] lightningPointFourth;
                    if (k4LHorizontal > 0) {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k4LHorizontal, 4);
                        lightningPointFourth = Storage.getLinesForHideZoneParsing().get(4).get(n);
                        x = lightningPointFourth[0];
                        y = lightningPointFourth[1];
                    } else {
                        n = getPixelNumber(pixelsSizesForHideZoneParsing, k4LHorizontal, 4);
                        List<int[]> list = Storage.getLinesForHideZoneParsing().get(4);
                        lightningPointFourth = list.get(n);
                        int[] firstPoint = list.get(0);
                        int horizontal = firstPoint[0] - lightningPointFourth[0];
                        int vertical = firstPoint[1] - lightningPointFourth[1];
                        disPix = Math.sqrt((Math.pow(horizontal, 2.0) +
                                Math.pow(vertical, 2.0)));
                        x = (int) (firstPoint[0] - disPix);
                        y = firstPoint[1];
                    }
                    drawLightningToImage(backGroundImageFourth, x, y, 4, zoneName);
                }
            }
        }
    }

    public static void injectImage(File folder, BufferedImage image) {
        if (folder != null) {
            String name = folder.getName();
            int frameToReplace = 0;
            int first = name.indexOf("[");
            int second = name.indexOf("]");
            String substring = name.substring(first + 1, second);
            String[] eventsSplit = substring.split(",");
            String aSplit = eventsSplit[0];
            boolean contains = aSplit.contains("(");
            if (contains) {
                String s = aSplit.substring(1, aSplit.length() - 1);
                try {
                    frameToReplace = Integer.parseInt(s);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    frameToReplace = Integer.parseInt(aSplit);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            int x = 0;
            int t;
            int totalCountFrames = 0;
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {

                    String fileName = file.getName();

                    String[] fileNameSplit = fileName.split("\\.");
                    String[] lastSplit = fileNameSplit[0].split("-");
                    String countFramesString = lastSplit[1];
                    int countFrames = Integer.parseInt(countFramesString);
                    totalCountFrames += countFrames;
                    if (totalCountFrames >= frameToReplace) {
                        totalCountFrames = totalCountFrames - countFrames;
                        File testFile = new File(file.getAbsolutePath() + "test");
                        try {

                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                            ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream(65535);
                            FileOutputStream fileOutputStream = new FileOutputStream(testFile);


                            testFile.createNewFile();
                            do {
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
                                    totalCountFrames++;
                                    if (totalCountFrames == frameToReplace) {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        ImageIO.write(image, "jpg", baos);
                                        baos.flush();
                                        imageBytes = baos.toByteArray();
                                        baos.close();
                                    }
                                    fileOutputStream.write(imageBytes);
                                }
                            } while (x > -1);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            fileInputStream.close();
                            bufferedInputStream.close();
                            temporaryStream.close();
                            file.delete();
                            testFile.renameTo(file);
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        }
    }
}
