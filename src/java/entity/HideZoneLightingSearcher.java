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

    public static void addHideZoneAreaName(File folderWithFiles) {
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
                String zoneName;

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
                        log.info("Только одна камера работала");
                    }
                }

                if (northZoneNumber != null && southZoneNumber != null) {
                    if (northZoneNumber.compareTo(southZoneNumber) == 0) {
                        zoneName = northZoneNumber;
                    } else {
                        zoneName = "ERROR";
                    }
                } else {
                    zoneName = "NO DATA";
                }

                stringBuilder.append(zoneName);
                if (i != size - 1) {
                    stringBuilder.append(',');
                }
            }
        }
        folderWithFiles.renameTo(new File(folderWithFiles.getAbsolutePath() + "{" + stringBuilder.toString() + "}"));
    }

    private static String getZoneName(int firsCameraNumber, File firstCameraFolder, Integer firstCameraEventFramesNumber,
                                      File secondCameraFolder, Integer secondCameraEventFramesNumber) {
        BufferedImage mostWhiteImageFirstCamera = getMostWhiteImage(getFramesWithLightning(firstCameraFolder, firstCameraEventFramesNumber, Storage.getAddressSaver().getHideZoneIdentificationAccuracyCountOfFramesToAnalise()));
        BufferedImage mostWhiteImageSecondCamera = getMostWhiteImage(getFramesWithLightning(secondCameraFolder, secondCameraEventFramesNumber, Storage.getAddressSaver().getHideZoneIdentificationAccuracyCountOfFramesToAnalise()));
        BufferedImage[] imagesFromTwoCameraGroups = new BufferedImage[]{mostWhiteImageFirstCamera, mostWhiteImageSecondCamera};

        System.out.println("Ширина - " + mostWhiteImageFirstCamera.getWidth());

        return getHideZoneNumber(firsCameraNumber, imagesFromTwoCameraGroups);
    }

    private static String getHideZoneNumber(int firstCameraGroupNumber, BufferedImage[] imagesFromTwoCameraGroups) {

        String zoneName = null;

        List<int[]> firstCameraLinePoints = Storage.getLinesForHideZoneParsing().get(firstCameraGroupNumber);
        List<int[]> secondCameraLinePoints = Storage.getLinesForHideZoneParsing().get(firstCameraGroupNumber + 1);
        if (firstCameraLinePoints == null || secondCameraLinePoints == null) {
            return null;
        }

        Integer numberOfLinePointCutLightningFirst = getNumberOfLinePoint(firstCameraLinePoints, imagesFromTwoCameraGroups[0]);
        Integer numberOfLinePointCutLightningSecond = getNumberOfLinePoint(firstCameraLinePoints, imagesFromTwoCameraGroups[1]);
        if (numberOfLinePointCutLightningFirst != null && numberOfLinePointCutLightningSecond != null) {
            zoneName = findHideZone(firstCameraGroupNumber, numberOfLinePointCutLightningFirst, numberOfLinePointCutLightningSecond);
        }
        return zoneName;
    }

    private static String findHideZone(int groupNumber, int firstCameraGroupDistance, int secondCameraGroupDistance) {
        int[][] camerasPosition = Storage.getAddressSaver().getCamerasPosition();

        double angleFirstCamera = Math.PI - Math.atan(firstCameraGroupDistance + camerasPosition[groupNumber - 1][0]) / camerasPosition[groupNumber - 1][1];
        double angleSecondCamera = Math.PI - Math.atan(secondCameraGroupDistance + camerasPosition[groupNumber][0]) / camerasPosition[groupNumber][1];

        int distanceBetweenCameras = camerasPosition[groupNumber - 1][0] + camerasPosition[groupNumber][0] + 160;
        int diffBetweenCameras = camerasPosition[groupNumber - 1][0] - camerasPosition[groupNumber][0];

        int firstCameraDistanceToLightning = 0;
        int secondCameraDistanceToLightning = 0;
        int numberOfHideZone = 0;

        for (int i = camerasPosition[groupNumber - 1][0]; i < distanceBetweenCameras; i++) {
            firstCameraDistanceToLightning = (int) Math.tan(angleFirstCamera) * i;
            secondCameraDistanceToLightning = (int) Math.tan(angleSecondCamera) * (distanceBetweenCameras - i);
            if (firstCameraDistanceToLightning + diffBetweenCameras == secondCameraDistanceToLightning) {
                numberOfHideZone = (i / 10) + 1;
                break;
            }
        }
        int distanceToLightning = firstCameraDistanceToLightning - camerasPosition[groupNumber - 1][1];


        String zoneName = null;

        char[] alphabet = new char[26];
        for (int i = 0; i < 26; i++) {
            alphabet[i] = (char) ('a' + i);
        }

        if (distanceToLightning >= 87 && distanceToLightning <= 173) {
            for (int i = 0; i < 16; i++) {
                char a = alphabet[i];
                int zoneChar = 170 - i * 10;
                if (distanceToLightning > zoneChar) {
                    zoneName = String.valueOf(a + numberOfHideZone);
                }
            }
        }

        return zoneName;
    }


    private static List<BufferedImage> getFramesWithLightning(File folder, int frameNumber, int accuracy) {
        List<BufferedImage> imagesToReturn = new LinkedList<>();

//        List<File> filesList = new ArrayList<>();
        Map<File, Integer> framesInFiles = new TreeMap<>();
        int totalCountFrames = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
//                filesList.add(file);
                try {
                    String[] split = file.getName().split("\\.");
                    String[] lastSplit = split[0].split("-");
                    String countFramesString = lastSplit[1];
                    int i = Integer.parseInt(countFramesString);
                    framesInFiles.put(file, i);
                    totalCountFrames += i;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            Collections.sort(filesList);

            int firstFrameNumber = frameNumber - accuracy;
            int lastFrameNumber = frameNumber + accuracy;

            if (firstFrameNumber < 0) {
                firstFrameNumber = 1;
            }
            int currentCount = 0;
            for (File file : framesInFiles.keySet()) {
                System.out.println(file.getAbsolutePath());
                System.out.println("==========================");

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
            } catch (Exception e) {
                System.out.println("Битая картинка");
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

    private static boolean comparePoints(int[] linePoint, int[] lightningPoint, int accuracy) {
        return Math.abs(linePoint[0] - lightningPoint[0]) < accuracy && Math.abs(linePoint[1] - lightningPoint[1]) < accuracy;
    }

    private static Integer getNumberOfLinePoint(List<int[]> cameraGroupLinePoints, BufferedImage bi) {
        Deque<int[]> points = new ConcurrentLinkedDeque<>();
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                if (Storage.getColorRGBNumberSet().contains(bi.getRGB(x, y))) {
                    int[] lastWhitePoint = new int[]{y, x};
                    points.addFirst(lastWhitePoint);
                    if (points.size() < 10) {
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

        if (linePointNumber != null) {
            System.out.println("Номер точки - " + linePointNumber);
        }

        return linePointNumber;
    }
}
