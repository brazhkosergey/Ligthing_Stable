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

    //TODO - DONE
    public static void findHideZoneAreaAndRenameFolder(File folderWithFiles) {
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
            List<Integer> eventFramesNumberList = null;

            for (File oneFolderFromCamera : foldersFromEachCamera) {
                String nameOfFolder = oneFolderFromCamera.getName();
                if (!nameOfFolder.contains(".jpg")) {
                    int cameraGroupNumber = Integer.parseInt(nameOfFolder.substring(0, 1));
                    eventFramesNumberList = new ArrayList<>();
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
            int size = Objects.requireNonNull(eventFramesNumberList).size();

            for (int i = 0; i < size; i++) {
                String zoneName = null;
                String northZoneNumber = null;
                String southZoneNumber = null;
                for (int groupNumber = 1; groupNumber < 5; groupNumber += 2) {
                    File firstCameraFolder = foldersFromEachCameraMap.get(groupNumber);
                    File secondCameraFolder = foldersFromEachCameraMap.get(groupNumber + 1);
                    if (firstCameraFolder != null && secondCameraFolder != null) {
                        switch (groupNumber) {
                            case 1:
                                northZoneNumber = getZoneName(groupNumber, firstCameraFolder, eventsNumbersMap.get(groupNumber).get(i),
                                        secondCameraFolder, eventsNumbersMap.get(groupNumber + 1).get(i));
                                break;
                            case 3:
                                southZoneNumber = getZoneName(groupNumber, firstCameraFolder, eventsNumbersMap.get(groupNumber).get(i),
                                        secondCameraFolder, eventsNumbersMap.get(groupNumber + 1).get(i));
                                break;
                        }
                    } else {
                        String m = null;
                        switch (groupNumber) {
                            case 1:
                                m = "Север";
                                break;
                            case 3:
                                m = "Юг";
                                break;
                        }
                        log.error("Только одна камера работала на стороне - " + m);
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
                    if (northZoneNumber.compareTo(southZoneNumber) == 0) {
                        zoneName = northZoneNumber;
                        log.info("Номер квадрата подтверждают все камеры. Квадрат - " + zoneName);
                    } else {
                        zoneName = "ERROR";
                        log.error("Камеры с севера и юга указали на разные квадраты.");
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
        folderWithFiles.renameTo(new File(folderWithFiles.getAbsolutePath() + "{" + stringBuilder.toString() + "}"));
    }

    //TODO - DONE
    private static String getZoneName(int firsCameraNumber, File firstCameraFolder, Integer firstCameraEventFramesNumber,
                                      File secondCameraFolder, Integer secondCameraEventFramesNumber) {
        BufferedImage mostWhiteImageFirstCamera = getMostWhiteImage(getFramesWithLightning(firstCameraFolder, firstCameraEventFramesNumber, Storage.getAddressSaver().getHideZoneIdentificationAccuracyCountOfFramesToAnalise()));
        BufferedImage mostWhiteImageSecondCamera = getMostWhiteImage(getFramesWithLightning(secondCameraFolder, secondCameraEventFramesNumber, Storage.getAddressSaver().getHideZoneIdentificationAccuracyCountOfFramesToAnalise()));
        BufferedImage[] imagesFromTwoCameraGroups = new BufferedImage[]{mostWhiteImageFirstCamera, mostWhiteImageSecondCamera};
        return getHideZoneNumberFromFrames(firsCameraNumber, imagesFromTwoCameraGroups);
    }

    //TODO - DONE
    private static String getHideZoneNumberFromFrames(int firstCameraGroupNumber, BufferedImage[] imagesFromTwoCameraGroups) {
        String zoneName = null;
        List<int[]> firstCameraLinePoints = Storage.getLinesForHideZoneParsing().get(firstCameraGroupNumber);
        List<int[]> secondCameraLinePoints = Storage.getLinesForHideZoneParsing().get(firstCameraGroupNumber + 1);
        if (firstCameraLinePoints == null || secondCameraLinePoints == null) {
            int cameraNumber = 1;
            if (secondCameraLinePoints == null) {
                cameraNumber = 2;
            }
            log.error("Не указаны линии границы верха саркофага и скрытой зоны. Группа камер номер - " + (firstCameraGroupNumber + cameraNumber));
            return null;
        }

        Integer numberOfLinePointCutLightningFirst = getNumberOfLinePoint(firstCameraLinePoints, imagesFromTwoCameraGroups[0]);
        Integer numberOfLinePointCutLightningSecond = getNumberOfLinePoint(secondCameraLinePoints, imagesFromTwoCameraGroups[1]);
        if (numberOfLinePointCutLightningFirst != null && numberOfLinePointCutLightningSecond != null) {
            zoneName = calculateHideZoneNumber(firstCameraGroupNumber,
                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(numberOfLinePointCutLightningFirst, 1),
                    getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(numberOfLinePointCutLightningSecond, 2));
        }
        return zoneName;
    }


    public static int getDistanceFromBeginningSarcophagusToPlaceLightningCutLine(int numberOfLinePoint, int numberOfCamera) {
        int distanceToReturn = 0;
        int distance = numberOfLinePoint;//TODO Здесь нужн сделать поправку на углы обзора
        if (numberOfCamera == 1) {
            distanceToReturn = 160 - distance;
        } else {
            distanceToReturn = distance;
        }
        return distanceToReturn;
    }

    public static String calculateHideZoneNumber(int groupNumber,
                                                 int DistanceFromBeginningSarcophagusToPlaceLightningCutLineFirst,
                                                 int DistanceFromBeginningSarcophagusToPlaceLightningCutLineSecond) {
        int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();

        int verticalDistanceToSarc;
        int sizeOfHideZone;
        int endOfHideZone;

        if (groupNumber == 1) {
            verticalDistanceToSarc = 87;
            sizeOfHideZone = 173;
        } else {
            verticalDistanceToSarc = 97;
            sizeOfHideZone = 183;
        }
        double fistCameraСathetusOne = (double) (DistanceFromBeginningSarcophagusToPlaceLightningCutLineFirst) + camerasPosition[groupNumber - 1][0];
        double fistCameraСathetusTwo = (double) camerasPosition[groupNumber - 1][1] + verticalDistanceToSarc;
        double secondCameraСathetusOne = (double) DistanceFromBeginningSarcophagusToPlaceLightningCutLineSecond + camerasPosition[groupNumber][0];
        double secondCameraСathetusTwo = (double) camerasPosition[groupNumber][1] + verticalDistanceToSarc;

        double angleFirstCamera = Math.atan(fistCameraСathetusOne / fistCameraСathetusTwo);
        double angleSecondCamera = Math.atan(secondCameraСathetusOne / secondCameraСathetusTwo);

        double v = Math.toDegrees(angleFirstCamera);//28
        double v1 = Math.toDegrees(angleSecondCamera);//41

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
            if (firstCameraDistanceToLightning - diffBetweenCameras == secondCameraDistanceToLightning) {
                numberOfHideZone = (int) ((i - (double) camerasPosition[groupNumber - 1][0]) / 10) + 1;
                if (groupNumber != 1) {
                    numberOfHideZone = 17-numberOfHideZone;
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
            for (int i = 0; i < 10; i++) {
                if (groupNumber == 1) {
                    int zoneChar = 170 - i * 10;
                    if (distanceToLightning > zoneChar) {
                        String letterOfZone;
                        letterOfZone = String.valueOf(alphabet[i]);
                        zoneName = letterOfZone + numberOfHideZone;
                        break;
                    }
                } else {
                    int zoneCharSize = 180 - i*10;
                    if (distanceToLightning > zoneCharSize) {
                        String letterOfZone;
                        letterOfZone = String.valueOf(alphabet[9-i]);
                        zoneName = letterOfZone + numberOfHideZone;
                        break;
                    }
                }
            }
        }
        return zoneName;
    }

    //TODO - DONE
    private static List<BufferedImage> getFramesWithLightning(File folder, int frameNumber, int accuracy) {
        log.info("Ищем кадры с молнией. Количество кадров - " + accuracy + " до и после сработки.");
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

            int firstFrameNumber = frameNumber - accuracy - 1;
            int lastFrameNumber = frameNumber + accuracy;

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

    //TODO - DONE
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

    //TODO - DONE
    private static void addEventNumberToList(String numberString, List<Integer> eventFramesNumberList) {
        if (numberString.contains("(")) {
            eventFramesNumberList.add(Integer.parseInt(numberString.substring(1, numberString.length() - 1)));
        } else {
            eventFramesNumberList.add(Integer.parseInt(numberString));
        }
    }

    //TODO - DONE
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

    //TODO - DONE
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

    //TODO - DONE
    private static boolean comparePoints(int[] linePoint, int[] lightningPoint, int accuracy) {
        return Math.abs(linePoint[0] - lightningPoint[0]) < accuracy && Math.abs(linePoint[1] - lightningPoint[1]) < accuracy;
    }

    //TODO - TEST
    public static List<int[]> getTest(List<int[]> cameraGroupLinePoints, BufferedImage bi) {
        List<int[]> list = new ArrayList<>();
        Deque<int[]> points = new ConcurrentLinkedDeque<>();
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                if (Storage.getColorRGBNumberSet().contains(bi.getRGB(x, y))) {
                    int[] lastWhitePoint = new int[]{x - 1, y - 1};
//                    list.add(lastWhitePoint);
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
