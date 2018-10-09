package entity;

import entity.Camera.ServiceCamera;
import entity.Storage.Storage;
import org.apache.log4j.Logger;
import ui.main.MainFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HideZoneLightingSearcher {

    private static Logger log = Logger.getLogger("admin");

    static boolean findHideZoneAreaAndRenameFolder(File folderWithFiles) {
        MainFrame.showInformMassage(Storage.getBundle().getString("startedprocessing"), new Color(23, 114, 26));
        try {
            Date date = new Date(Long.parseLong(folderWithFiles.getName()));
            log.info("Анализируем запись " + date.toString());
        } catch (Exception i) {
            log.error("Анализируем запись " + folderWithFiles.getName());
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
                String nameOfFolder = oneFolderFromCamera.getName();
                if (!nameOfFolder.contains(".jpg")) {
                    eventFramesNumberList = new ArrayList<>();
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

            int size = eventFramesNumberList.size();
            for (int i = 0; i < size; i++) {
                String zoneName = null;
                String[] arr = new String[2];
                for (int site = 1; site < 3; site++) {
                    if (maps[site - 1].size() == 2) {
                        List<Integer> linePointsNumbers = new ArrayList<>();
                        for (Integer groupNumber : maps[site - 1].keySet()) {
                            BufferedImage mostWhiteImage = getMostWhiteImage(getFramesWithLightning(foldersFromEachCameraMap.get(groupNumber),
                                    eventsNumbersMap.get(groupNumber).get(i)));

                            if (mostWhiteImage != null) {
                                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
                                if (cameraLinePoints == null) {
                                    log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + i);
                                    continue;
                                }
                                Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, mostWhiteImage);
                                if (numberOfLinePointCutLightning != null) {
                                    linePointsNumbers.add(numberOfLinePointCutLightning);
                                }
                            }
                        }
                        if (linePointsNumbers.size() == 2) {
                            arr[site - 1] = calculateHideZoneNumber(site * 2 - 1,
                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(0), site * 2 - 1),
                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(1), site * 2));
                        }
                    }
                }


                if (arr[0] != null) {
                    zoneName = arr[0];
                    log.info("Камеры с СЕВЕРА определили квадрат - " + zoneName);
                }

                if (arr[1] != null) {
                    zoneName = arr[1];
                    log.info("Камеры с ЮГА определили квадрат - " + zoneName);
                }

                if (arr[0] != null && arr[1] != null) {
                    if (arr[0].compareTo(arr[1]) != 0) {
                        zoneName = "ERROR";
                        log.error("Камеры с севера и юга указали на разные квадраты.");
                    } else {
                        MainFrame.showInformMassage(Storage.getBundle().getString("zone") + zoneName, new Color(23, 114, 26));
                    }
                }
                if (zoneName == null) {
                    zoneName = "NO DATA";
                    MainFrame.showInformMassage(Storage.getBundle().getString("NODATA"), new Color(23, 114, 26));
                    log.error("Номер квадрата не удалось определить.");
                }

                stringBuilder.append(zoneName);
                if (i != size - 1) {
                    stringBuilder.append(',');
                }
//                if (maps[0].size() == 2) {
//                    List<Integer> linePointsNumbers = new ArrayList<>();
//                    for (Integer groupNumber : maps[0].keySet()) {
//                        BufferedImage mostWhiteImage = getMostWhiteImage(getFramesWithLightning(foldersFromEachCameraMap.get(groupNumber),
//                                eventsNumbersMap.get(groupNumber).get(i)));
//
//                        if (mostWhiteImage != null) {
//                            List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
//                            if (cameraLinePoints == null) {
//                                log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + i);
//                                continue;
//                            }
//                            Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, mostWhiteImage);
//                            if (numberOfLinePointCutLightning != null) {
//                                linePointsNumbers.add(numberOfLinePointCutLightning);
//                            }
//                        }
//                    }
//                    northZoneNumber = calculateHideZoneNumber(1,
//                            getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(0), 1),
//                            getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(1), 2));
//                }
            }

//            if (foldersFromEachCameraMap.size() == 4) {
//                for (int i = 0; i < size; i++) {
//                    Map<Integer, Integer> linePointsNumbers = new HashMap<>();
//                    Map<Integer, BufferedImage> imageWithLightnings = new HashMap<>();
//
//                    for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
//                        BufferedImage mostWhiteImage = getMostWhiteImage(getFramesWithLightning(foldersFromEachCameraMap.get(groupNumber),
//                                eventsNumbersMap.get(groupNumber).get(i)));
//
//                        if (mostWhiteImage != null) {
//                            imageWithLightnings.put(groupNumber, mostWhiteImage);
//                            List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(i);
//                            if (cameraLinePoints == null) {
//                                log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + i);
//                                continue;
//                            }
//                            Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, mostWhiteImage);
//                            if (numberOfLinePointCutLightning != null) {
//                                linePointsNumbers.put(groupNumber, numberOfLinePointCutLightning);
//                            }
//                        }
//                    }
//
//                    String zoneName = null;
//                    if (linePointsNumbers.size() == 4) {
//                        String northZoneNumber = null;
//                        String southZoneNumber = null;
//                        for (int groupNumber = 1; groupNumber < 5; groupNumber += 2) {
//                            String string = calculateHideZoneNumber(groupNumber,
//                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber), groupNumber),
//                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber + 1), groupNumber + 1));
//                            switch (groupNumber) {
//                                case 1:
//                                    northZoneNumber = string;
//                                    break;
//                                case 3:
//                                    southZoneNumber = string;
//                                    break;
//                            }
//                        }
//
//                        if (northZoneNumber != null) {
//                            zoneName = northZoneNumber;
//                            log.info("Камеры с СЕВЕРА определили квадрат - " + zoneName);
//                        }
//
//                        if (southZoneNumber != null) {
//                            zoneName = southZoneNumber;
//                            log.info("Камеры с ЮГА определили квадрат - " + zoneName);
//                        }
//
//                        if (northZoneNumber != null && southZoneNumber != null) {
//                            if (northZoneNumber.compareTo(southZoneNumber) != 0) {
//                                zoneName = "ERROR";
//                                log.error("Камеры с севера и юга указали на разные квадраты.");
//                            } else {
//                                MainFrame.showInformMassage(Storage.getBundle().getString("zone") + zoneName, new Color(23, 114, 26));
//                            }
//                        }
//                    }
//                    if (zoneName == null) {
//                        zoneName = "NO DATA";
//                        MainFrame.showInformMassage(Storage.getBundle().getString("NODATA"), new Color(23, 114, 26));
//                        log.error("Номер квадрата не удалось определить.");
//                    }
//
//                    stringBuilder.append(zoneName);
//                    if (i != size - 1) {
//                        stringBuilder.append(',');
//                    }
//                }
//
//
//            } else {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                MainFrame.showInformMassage(Storage.getBundle().getString("NOTALLCAMERAS"), new Color(23, 114, 26));
//                stringBuilder.append("NOT ALL CAMERAS");
//            }
        }


        return folderWithFiles.renameTo(new

                File(folderWithFiles.getAbsolutePath() + "{" + stringBuilder.toString() + "}"));
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
        double firstAngleOfSector = lengthOfArc / distanceToSarcophagus;
        double secondAngleMinOfCameraGroups = Math.PI - (Math.PI / 2 - Math.atan(Storage.getAddressSaver().getCamerasViewAnglesTangens()[groupNumber - 1][0]));
        double thirdAngle = Math.PI - firstAngleOfSector - secondAngleMinOfCameraGroups;

        double distanceToLightningCutTheLine = distanceToSarcophagus * Math.sin(firstAngleOfSector) / Math.sin(thirdAngle);

        return (int) distanceToLightningCutTheLine;
    }

    private static String calculateHideZoneNumber(int groupNumber,
                                                  int distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst,
                                                  int distanceFromBeginningSarcophagusToPlaceLightningCutLineSecond) {

        int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();

//        int verticalDistanceToSarc;//Своя для каждой камеры
        int sizeOfHideZone;//своя для первой и третьей камеры
//
        if (groupNumber == 1) {
//            verticalDistanceToSarc = 87;
//            sizeOfHideZone = 173;
            sizeOfHideZone = 171;
        } else {
//            verticalDistanceToSarc = 97;
//            sizeOfHideZone = 183;
            sizeOfHideZone = 179;
        }

        double fistCameraСathetusOne = (double) (distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst) + camerasPosition[groupNumber - 1][0];
//        double fistCameraСathetusTwo = (double) camerasPosition[groupNumber - 1][1] + verticalDistanceToSarc;
        double fistCameraСathetusTwo = (double) camerasPosition[groupNumber - 1][1] + camerasPosition[groupNumber - 1][2];
        double secondCameraСathetusOne = (double) distanceFromBeginningSarcophagusToPlaceLightningCutLineSecond + camerasPosition[groupNumber][0];
        double secondCameraСathetusTwo = (double) camerasPosition[groupNumber][1] + camerasPosition[groupNumber][2];

        double angleFirstCamera = Math.atan(fistCameraСathetusOne / fistCameraСathetusTwo);
        double angleSecondCamera = Math.atan(secondCameraСathetusOne / secondCameraСathetusTwo);

        int distanceBetweenCameras = camerasPosition[groupNumber - 1][0] + camerasPosition[groupNumber][0] + 164;
        int diffBetweenCameras = camerasPosition[groupNumber - 1][1] - camerasPosition[groupNumber][1];
        int firstCameraDistanceToLightning = 0;
        int secondCameraDistanceToLightning = 0;
        int numberOfHideZone = 0;

        double tanOne;
        double tanTwo;

        for (double i = camerasPosition[groupNumber - 1][0]; i < distanceBetweenCameras; i += 0.1) {
            tanOne = Math.tan(angleFirstCamera);
            tanTwo = Math.tan(angleSecondCamera);
            firstCameraDistanceToLightning = (int) (i / tanOne);
            secondCameraDistanceToLightning = (int) ((distanceBetweenCameras - i) / tanTwo);
            int firstCameraTotalDistanceToLightning = firstCameraDistanceToLightning - diffBetweenCameras;

            if (Math.abs(firstCameraTotalDistanceToLightning - secondCameraDistanceToLightning) < Storage.getAddressSaver().getHideZoneIdentificationAccuracy()) {
                numberOfHideZone = (int) ((i - (double) camerasPosition[groupNumber - 1][0]) / 10) + 1;
                if (groupNumber != 1) {
                    numberOfHideZone = 17 - numberOfHideZone;
                }
                break;
            }
        }

        int distanceToLightning = firstCameraDistanceToLightning - camerasPosition[groupNumber - 1][1];
        String zoneName = null;
        char[] alphabet = new char[26];
        for (int i = 0; i < 26; i++) {
            alphabet[i] = (char) ('a' + i);
        }


        if (distanceToLightning >= camerasPosition[groupNumber - 1][2] && distanceToLightning <= sizeOfHideZone) {
            String letterOfZone = null;
            for (int i = 0; i < 10; i++) {
                if (groupNumber == 1) {
                    int zoneChar = 170 - i * 10;
                    if (distanceToLightning > zoneChar) {
                        letterOfZone = String.valueOf(alphabet[i]);
                        zoneName = letterOfZone + numberOfHideZone;
                        break;
                    }
                } else {
                    int zoneCharSize = 180 - i * 10;
                    if (distanceToLightning > zoneCharSize) {
                        letterOfZone = String.valueOf(alphabet[9 - i]);
                        zoneName = letterOfZone + numberOfHideZone;
                        break;
                    }
                }
            }
        } else {
            log.error("Out of hide zone");
        }
        return zoneName;
    }

    private static List<BufferedImage> getFramesWithLightning(File folder, int frameNumber) {
        log.info("Ищем кадры с молнией. 5 кадров до и после сработки.");
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

        log.info("Возвращаю кадров - " + imagesToReturn.size());
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
        log.info("Тестовое определение квадрата попадания молнии.");

        StringBuilder stringBuilder = new StringBuilder();
        String[] arr = new String[2];
        for (int site = 1; site < 3; site++) {
            List<Integer> linePointsNumbers = new ArrayList<>();

            for (int i = 1; i >= 0; i--) {
                BufferedImage testImage = Storage.getCameraGroups()[(site * 2 - 1) - i].getBackGroundImage();
                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(site * 2 - i);
                if (cameraLinePoints == null) {
                    stringBuilder.append("<html>Не указаны линии границы </br> верха саркофага и скрытой зоны.</br>Группа камер номер - " +
                            (site * 2 - i) + ". ");
                    log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + (site * 2 - i) + ". ");
                    continue;
                }
                Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints,
                        testImage);
                if (numberOfLinePointCutLightning != null) {
                    linePointsNumbers.add(numberOfLinePointCutLightning);
                } else {
                    stringBuilder.append("<html>Конец молнии не совпал с </br> границей видимости саркофага на одном из кадров.</br>Группа камер - "
                            + (site * 2 - i) + ". ");
                    log.error("Конец молнии не совпал с границей видимости саркофага на одном из кадров. Группа камер - "
                            + (site * 2 - i) + ". Определить квадрат невозможно.");
                }
            }

            if (linePointsNumbers.size() == 2) {
                arr[site - 1] = calculateHideZoneNumber(site * 2 - 1,
                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(0), site * 2 - 1),
                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(1), site * 2));
            }
        }

        for (String s : arr) {
            if (s != null) {
                System.out.println(s);
            }
        }

        if (arr[0] != null && arr[1] != null) {
            if (arr[0].compareTo(arr[1]) != 0) {
                stringBuilder.append("North - " + arr[0] + ". South - " + arr[1]);
                log.error("Камеры с севера и юга указали на разные квадраты.");
            } else {
                stringBuilder.append(arr[0]);
            }
        } else {
            if (arr[0] != null) {
                stringBuilder.append("South - " + arr[0] + ".</html>");
            }
            if (arr[1] != null) {
                stringBuilder.append("North - " + arr[1] + ".</html>");
            }
        }

        System.out.println("Возвращаем - " + stringBuilder.toString());
        return stringBuilder.toString();
//        if (zoneName == null) {
//            zoneName = "NO DATA";
//            MainFrame.showInformMassage(Storage.getBundle().getString("NODATA"), new Color(23, 114, 26));
//            log.error("Номер квадрата не удалось определить.");
//        }
//        Map<Integer, Integer> linePointsNumbers = new HashMap<>();
//        Map<Integer, BufferedImage> imageWithLightnings = new HashMap<>();
//        String zoneName = null;


//        for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
//            BufferedImage testImage = Storage.getCameraGroups()[groupNumber - 1].getBackGroundImage();
//            if (testImage != null) {
//                imageWithLightnings.put(groupNumber, testImage);
//                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
//                if (cameraLinePoints == null) {
//                    zoneName = "<html>Не указаны линии границы </br> верха саркофага и скрытой зоны.</br>Группа камер номер - " + groupNumber + "<html>";
//                    log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + groupNumber);
//                    return zoneName;
//                }
//                Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, testImage);
//                if (numberOfLinePointCutLightning != null) {
//                    linePointsNumbers.put(groupNumber, numberOfLinePointCutLightning);
//                } else {
//                    zoneName = "<html>Конец молнии не совпал с </br> границей видимости саркофага на одном из кадров.</br>Группа камер - "
//                            + groupNumber + "</html>";
//                    log.error("Конец молнии не совпал с границей видимости саркофага на одном из кадров. Группа камер - "
//                            + groupNumber + ". Определить квадрат невозможно.");
//                    return zoneName;
//                }
//            }
//        }
//
//        if (linePointsNumbers.size() == 4) {
//            String northZoneNumber = null;
//            String southZoneNumber = null;
//            for (int groupNumber = 1; groupNumber < 5; groupNumber += 2) {
//                String string = calculateHideZoneNumber(groupNumber,
//                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber), groupNumber),
//                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber + 1), groupNumber + 1));
//                switch (groupNumber) {
//                    case 1:
//                        northZoneNumber = string;
//                        System.out.println("С севера - " + northZoneNumber);
//                        break;
//                    case 3:
//                        southZoneNumber = string;
//                        System.out.println("С Юга - " + southZoneNumber);
//                        break;
//                }
//            }
//
//            if (northZoneNumber != null) {
//                zoneName = northZoneNumber;
//                log.info("Камеры с СЕВЕРА определили квадрат - " + zoneName);
//            }
//
//            if (southZoneNumber != null) {
//                zoneName = southZoneNumber;
//                log.info("Камеры с ЮГА определили квадрат - " + zoneName);
//            }
//
//            if (northZoneNumber != null && southZoneNumber != null) {
//                if (northZoneNumber.compareTo(southZoneNumber) != 0) {
//                    zoneName = "Север - " + northZoneNumber + ". Юг - " + southZoneNumber;
//                    log.error("Камеры с севера и юга указали на разные квадраты.");
//                } else {
//                    log.info("Квадрат - " + zoneName);
//                }
//            } else {
//                zoneName = "";
//            }
//        }
//        return zoneName;
    }

    public static void createTestImageForCameraThreeAndFour(String zoneName) {
        if (zoneName != null) {
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
            Integer k1Horizontal = camerasPosition[0][0] + numberInt * 10 - 5;
            Integer k1Vertical = camerasPosition[0][1] + camerasPosition[0][2] + 80 - distance + 5;

            double angleOne = Math.atan((double) k1Horizontal / k1Vertical);
            int k1sVertical = 80 - distance + 5;
            int k1sHorizontal = (int) (Math.tan(angleOne) * k1sVertical);
            int k1LHorizontal = numberInt * 10 - 5 - k1sHorizontal;
            if (k1LHorizontal > 0) {
                System.out.println("Можем отрисовать линию - " + k1LHorizontal);
                int distanceToDraw = (int) (Math.cos(angleOne) * k1LHorizontal);
                BufferedImage backGroundImageOne = Storage.getCameraGroups()[0].getBackGroundImage();
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(1);
                double disPix = 0.0;
                int n = 0;
                for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                    disPix += pixelsSizesForHideZoneParsing[i];
                    if (disPix > distanceToDraw) {
                        n = i;
                        System.out.println(disPix + " > " + distanceToDraw);
                        break;
                    }
                }
                int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(1).get(n);
                drawLightningToImage(backGroundImageOne, lightningPointOne, 1);
            } else {
                System.out.println("Линия за пределами границы - " + k1LHorizontal);
            }

//            ======================================================================
            Integer k2Horizontal = camerasPosition[1][0] + 164 - (numberInt * 10 - 5);
            Integer k2Vertical = camerasPosition[1][1] + camerasPosition[1][2] + 80 - (distance - 5);

            double angleSecond = Math.atan((double) k2Horizontal / k2Vertical);
            int k2sVertical = 80 - (distance - 5);
            int k2sHorizontal = (int) (Math.tan(angleSecond) * k2sVertical);
            int k2LHorizontal = 164 - (numberInt * 10 - 5) - k2sHorizontal;
            if (k2LHorizontal > 0) {
                System.out.println("Можем отрисовать линию - " + k2LHorizontal);
                int distanceToDraw = (int) (Math.cos(angleSecond) * k2LHorizontal);
                BufferedImage backGroundImageSecond = Storage.getCameraGroups()[1].getBackGroundImage();
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(2);
                double disPix = 0.0;
                int n = 0;
                for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                    disPix += pixelsSizesForHideZoneParsing[i];
                    if (disPix > distanceToDraw) {
                        n = i;
                        System.out.println(disPix + " > " + distanceToDraw);
                        break;
                    }
                }
                int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(2).get(n);
                drawLightningToImage(backGroundImageSecond, lightningPointOne, 2);
            } else {
                System.out.println("Линия за пределами границы - " + k2LHorizontal);
            }

            Integer k3Horizontal = camerasPosition[2][0] + 164 - numberInt * 10 + 5;
            Integer k3Vertical = camerasPosition[2][1] + camerasPosition[2][2] + distance - 5;

            double angleThird = Math.atan((double) k3Horizontal / k3Vertical);
            int k3sVertical = distance - 5;
            int k3sHorizontal = (int) (Math.tan(angleThird) * k3sVertical);
            int k3LHorizontal = 164 - numberInt * 10 + 5 - k3sHorizontal;
            if (k3LHorizontal > 0) {
                System.out.println("Можем отрисовать линию - " + k3LHorizontal);
                int distanceToDraw = (int) (Math.cos(angleThird) * k3LHorizontal);
                BufferedImage backGroundImageOne = Storage.getCameraGroups()[2].getBackGroundImage();
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(3);
                double disPix = 0.0;
                int n = 0;
                for (int i = pixelsSizesForHideZoneParsing.length - 1; i >= 0; i--) {
                    disPix += pixelsSizesForHideZoneParsing[i];
                    if (disPix > distanceToDraw) {
                        n = i;
                        System.out.println(disPix + " > " + distanceToDraw);
                        break;
                    }
                }
                int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(3).get(n);
                drawLightningToImage(backGroundImageOne, lightningPointOne, 3);
            } else {
                System.out.println("Линия за пределами границы - " + k3LHorizontal);
            }


//            ======================================================================
            Integer k4Horizontal = camerasPosition[3][0] + numberInt * 10 - 5;
            Integer k4Vertical = camerasPosition[3][1] + camerasPosition[3][2] + distance - 5;

            double angleFourth = Math.atan((double) k4Horizontal / k4Vertical);
            int k4sVertical = distance - 5;
            int k4sHorizontal = (int) (Math.tan(angleFourth) * k4sVertical);
            int k4LHorizontal = numberInt * 10 - 5 - k4sHorizontal;
            if (k4LHorizontal > 0) {
                System.out.println("Можем отрисовать линию - " + k4LHorizontal);
                int distanceToDraw = (int) (Math.cos(angleFourth) * k4LHorizontal);
                BufferedImage backGroundImageSecond = Storage.getCameraGroups()[3].getBackGroundImage();
                double[] pixelsSizesForHideZoneParsing = Storage.getPixelsSizesForHideZoneParsingMap().get(4);
                double disPix = 0.0;
                int n = 0;
                for (int i = 0; i < pixelsSizesForHideZoneParsing.length; i++) {
                    disPix += pixelsSizesForHideZoneParsing[i];
                    if (disPix > distanceToDraw) {
                        n = i;
                        System.out.println(disPix + " > " + distanceToDraw);
                        break;
                    }
                }
                int[] lightningPointOne = Storage.getLinesForHideZoneParsing().get(4).get(n);
                drawLightningToImage(backGroundImageSecond, lightningPointOne, 4);
            } else {
                System.out.println("Линия за пределами границы - " + k4LHorizontal);
            }
        }
    }

    private static void drawLightningToImage(BufferedImage bufferedImage, int[] lightningPoint, int groupNumber) {
        BufferedImage backImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D g1 = (Graphics2D) backImage.getGraphics();
        g1.setColor(Color.BLACK);
        g1.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        BufferedImage image = ServiceCamera.connectImage(bufferedImage, backImage, 0.5f);
        Graphics2D g = (Graphics2D) image.getGraphics();
        BasicStroke pen1 = new BasicStroke(3); //толщина линии 20
        g.setStroke(pen1);
        g.setColor(Color.WHITE);
        g.drawLine(lightningPoint[0], lightningPoint[1], lightningPoint[0], 0);
        Storage.getCameraGroups()[groupNumber - 1].setBackGroundImage(image);
    }
}
