package entity;

import entity.Storage.Storage;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HideZoneLightingSearcher {

    private static Logger log = Logger.getLogger("admin");

    static void findHideZoneAreaAndRenameFolder(File folderWithFiles) {
        try {
            Date date = new Date(Long.parseLong(folderWithFiles.getName()));
            log.info("Анализируем запись " + date.toString());
        } catch (Exception i) {
            log.error("Анализируем запись " + folderWithFiles.getName());
        }

        StringBuilder stringBuilder = new StringBuilder();
        File[] foldersFromEachCamera = folderWithFiles.listFiles();
        if (foldersFromEachCamera != null) {
            Map<Integer, File> foldersFromEachCameraMap = new HashMap<>();
            Map<Integer, List<Integer>> eventsNumbersMap = new HashMap<>();
            List<Integer> eventFramesNumberList = new ArrayList<>();
            for (File oneFolderFromCamera : foldersFromEachCamera) {
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
                    foldersFromEachCameraMap.put(cameraGroupNumber, oneFolderFromCamera);
                }
            }

            int size = eventFramesNumberList.size();
            if (foldersFromEachCameraMap.size() == 4) {
                for (int i = 0; i < size; i++) {
                    Map<Integer, Integer> linePointsNumbers = new HashMap<>();
                    Map<Integer, BufferedImage> imageWithLightnings = new HashMap<>();

                    for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
                        BufferedImage mostWhiteImage = getMostWhiteImage(getFramesWithLightning(foldersFromEachCameraMap.get(groupNumber),
                                eventsNumbersMap.get(groupNumber).get(i)));

                        if (mostWhiteImage != null) {
                            imageWithLightnings.put(groupNumber, mostWhiteImage);
                            List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(i);
                            if (cameraLinePoints == null) {
                                log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + i);
                                continue;
                            }
                            Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, mostWhiteImage);
                            if (numberOfLinePointCutLightning != null) {
                                linePointsNumbers.put(groupNumber, numberOfLinePointCutLightning);
                            }
                        }
                    }

                    String zoneName = null;
                    if (linePointsNumbers.size() == 4) {
                        String northZoneNumber = null;
                        String southZoneNumber = null;
                        for (int groupNumber = 1; groupNumber < 5; groupNumber += 2) {
                            String string = calculateHideZoneNumber(groupNumber,
                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber), groupNumber),
                                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber + 1), groupNumber + 1));
                            switch (groupNumber) {
                                case 1:
                                    northZoneNumber = string;
                                    break;
                                case 3:
                                    southZoneNumber = string;
                                    break;
                            }
                        }

                        if (northZoneNumber != null) {
                            zoneName = northZoneNumber;
                            log.info("Камеры с СЕВЕРА определили квадрат - " + zoneName);
                        }

                        if (southZoneNumber != null) {
                            zoneName = southZoneNumber;
                            log.info("Камеры с ЮГА определили квадрат - " + zoneName);
                        }

                        if (northZoneNumber != null && southZoneNumber != null) {
                            if (northZoneNumber.compareTo(southZoneNumber) != 0) {
                                zoneName = "ERROR";
                                log.error("Камеры с севера и юга указали на разные квадраты.");
                            }
                        }
                    }

                    if (zoneName == null) {
                        zoneName = "NO DATA";
                        log.error("Номер квадрата не удалось определить.");
                    }

                    stringBuilder.append(zoneName);
                    if (i != size - 1) {
                        stringBuilder.append(',');
                    }
                }
            }
        }
        folderWithFiles.renameTo(new File(folderWithFiles.getAbsolutePath() + "{" + stringBuilder.toString() + "}"));
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

        System.out.println("Искомая точка номер - " + numberOfLinePoint + ". Group " + groupNumber);
        System.out.println("Длинна дуги - " + lengthOfArc);
        System.out.println("Первый угол - " + Math.toDegrees(firstAngleOfSector));
        System.out.println("Второй угол - " + Math.toDegrees(secondAngleMinOfCameraGroups));
        System.out.println("Третий угол - " + Math.toDegrees(thirdAngle));
        System.out.println("Реальное расстояние - " + distanceToLightningCutTheLine);
        System.out.println("=======================================================");
        return (int) distanceToLightningCutTheLine;
    }

    private static String calculateHideZoneNumber(int groupNumber,
                                                  int distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst,
                                                  int distanceFromBeginningSarcophagusToPlaceLightningCutLineSecond) {

        System.out.println("First camera distance - " + distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst);
        System.out.println("Second Camera distance - " + distanceFromBeginningSarcophagusToPlaceLightningCutLineSecond);

        int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();

        int verticalDistanceToSarc;
        int sizeOfHideZone;

        if (groupNumber == 1) {
            verticalDistanceToSarc = 87;
            sizeOfHideZone = 173;
        } else {
            verticalDistanceToSarc = 97;
            sizeOfHideZone = 183;
        }
        double fistCameraСathetusOne = (double) (distanceFromBeginningSarcophagusToPlaceLightningCutLineFirst) + camerasPosition[groupNumber - 1][0];
        double fistCameraСathetusTwo = (double) camerasPosition[groupNumber - 1][1] + verticalDistanceToSarc;
        double secondCameraСathetusOne = (double) distanceFromBeginningSarcophagusToPlaceLightningCutLineSecond + camerasPosition[groupNumber][0];
        double secondCameraСathetusTwo = (double) camerasPosition[groupNumber][1] + verticalDistanceToSarc;

        double angleFirstCamera = Math.atan(fistCameraСathetusOne / fistCameraСathetusTwo);
        double angleSecondCamera = Math.atan(secondCameraСathetusOne / secondCameraСathetusTwo);

        int distanceBetweenCameras = camerasPosition[groupNumber - 1][0] + camerasPosition[groupNumber][0] + 160;
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

        if (distanceToLightning >= verticalDistanceToSarc && distanceToLightning <= sizeOfHideZone) {
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

    public static Integer getNumberOfLinePoint(List<int[]> cameraGroupLinePoints, BufferedImage bi) {
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
        Map<Integer, Integer> linePointsNumbers = new HashMap<>();
        Map<Integer, BufferedImage> imageWithLightnings = new HashMap<>();

        String zoneName = null;

        for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
            BufferedImage testImage = Storage.getCameraGroups()[groupNumber - 1].getBackGroundImage();
            if (testImage != null) {
                imageWithLightnings.put(groupNumber, testImage);
                List<int[]> cameraLinePoints = Storage.getLinesForHideZoneParsing().get(groupNumber);
                if (cameraLinePoints == null) {
                    zoneName = "<html>Не указаны линии границы </br> верха саркофага и скрытой зоны.</br>Группа камер номер - " + groupNumber + "<html>";
                    log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + groupNumber);
                    return zoneName;
                }
                Integer numberOfLinePointCutLightning = getNumberOfLinePoint(cameraLinePoints, testImage);
                if (numberOfLinePointCutLightning != null) {
                    linePointsNumbers.put(groupNumber, numberOfLinePointCutLightning);
                    System.out.println("Для камеры - " + groupNumber + ". Точка пересечения саркофага равна - " + numberOfLinePointCutLightning);
                } else {
                    zoneName = "<html>Конец молнии не совпал с </br> границей видимости саркофага на одном из кадров.</br>Группа камер - "
                            + groupNumber + "</html>";
                    log.error("Конец молнии не совпал с границей видимости саркофага на одном из кадров. Группа камер - "
                            + groupNumber+". Определить квадрат невозможно.");
                    return zoneName;
                }
            }
        }

        if (linePointsNumbers.size() == 4) {
            String northZoneNumber = null;
            String southZoneNumber = null;
            for (int groupNumber = 1; groupNumber < 5; groupNumber += 2) {
                String string = calculateHideZoneNumber(groupNumber,
                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber), groupNumber),
                        getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(linePointsNumbers.get(groupNumber + 1), groupNumber + 1));
                switch (groupNumber) {
                    case 1:
                        northZoneNumber = string;
                        System.out.println("С севера - " + northZoneNumber);
                        break;
                    case 3:
                        southZoneNumber = string;
                        System.out.println("С Юга - " + southZoneNumber);
                        break;
                }
            }

            if (northZoneNumber != null) {
                zoneName = northZoneNumber;
                log.info("Камеры с СЕВЕРА определили квадрат - " + zoneName);
            }

            if (southZoneNumber != null) {
                zoneName = southZoneNumber;
                log.info("Камеры с ЮГА определили квадрат - " + zoneName);
            }

            if (northZoneNumber != null && southZoneNumber != null) {
                if (northZoneNumber.compareTo(southZoneNumber) != 0) {
                    zoneName = "Север - " + northZoneNumber + ". Юг - " + southZoneNumber;
                    log.error("Камеры с севера и юга указали на разные квадраты.");
                }else {
                    log.info("Квадрат - " + zoneName);
                }
            } else {
                zoneName = "";
            }
        }
        return zoneName;
    }

    //TEST METHOD
    public static List<int[]> getTest(List<int[]> cameraGroupLinePoints, BufferedImage bi) {
        List<int[]> list = new ArrayList<>();
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

        list.addAll(points);
        return list;
    }
}
