package entity.Camera;

import entity.Storage.Storage;
import entity.VideoCreator;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CameraGroup {
    private static Logger log = Logger.getLogger("file");

    private Camera[] cameras;
    private int groupNumber;
    private BufferedImage backGroundImage;
    /**
     * deque to save time, when image bytes was read to RAM
     */
    private Deque<Long> dequeImagesTime;

    /**
     * map to save byte array and time, when it was read to RAM
     */
    private Map<Long, byte[]> buffMapImages;

    /**
     * deque of links to temporary files with bytes from cameras on disk
     */
    private Deque<File> fileDeque;

    /**
     * counts of frames, in each temporary file
     */
    private Map<File, Integer> countsOfFramesInEachFile;

    /**
     * total FPS from both cameras
     */
    private int totalFPS = 0;

    /**
     * deque with total fps count for as many last second as program should save
     */
    private Deque<Integer> fpsDeque;

    /**
     * list with total fps count for as many last second as program should save
     */
    private List<Integer> fpsList;


    /**
     * mark if almost one of catchers is worked
     */
    private boolean creatorWork;

    /**
     * mark one second gone
     */
    private boolean oneSecond = false;
    /**
     * total count of frames in temporary files and RAM
     */
    private int totalCountFrames;

    /**
     * map with number of frame, when was lightning, and type of event (program - true or sensor - false)
     */
    private Map<Integer, Boolean> eventsFramesNumber;

    /**
     * mark that bytes from camera should be saved
     */
    private boolean enableSaveVideo;
    /**
     * mark count of already saved second after lightning
     */
    private int stopSaveVideoInt;

    /**
     * date when lightning was
     */
//    private Date date;
    private File folderToSave;

    private boolean containProgramCatchLightning;

    public CameraGroup(int groupNumber) {
        this.groupNumber = groupNumber;

        fileDeque = new ConcurrentLinkedDeque<>();
        countsOfFramesInEachFile = new HashMap<>();
        fpsDeque = new ConcurrentLinkedDeque<>();

        fpsList = new ArrayList<>();

        dequeImagesTime = new ConcurrentLinkedDeque<>();
        buffMapImages = new HashMap<>();
        eventsFramesNumber = new TreeMap<>();

        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    creatorWork = false;
                    for (Camera camera : cameras) {
                        if (!creatorWork) {
                            creatorWork = camera.isCatchVideo();
                        }

                        if (fileDeque.size() >= Storage.getSecondsToSave()) {
                            camera.getCameraPanel().getTitle().setTitleColor(new Color(70, 193, 84));
                        } else {
                            camera.getCameraPanel().getTitle().setTitleColor(Color.RED);
                        }
                    }
                    if (creatorWork) {
                        fpsList.add(totalFPS);
                        fpsDeque.addFirst(totalFPS);
                        totalFPS = 0;
                        oneSecond = true;
                        if (enableSaveVideo) {
                            stopSaveVideoInt++;
                        }
                    }

                    if (!creatorWork) {
                        while (fileDeque.size() > 0) {
                            try {
                                File fileToDel = fileDeque.pollLast();
                                Integer remove = countsOfFramesInEachFile.remove(fileToDel);
                                totalCountFrames -= remove;//Удаляем все временные файлы, в случае если камеры будут отключены.
                                fileToDel.delete();
                                fpsList.remove(0);
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                        stopSaveVideoInt = 0;
                    }

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        timerThread.setName("VideoCreatorTimer Thread " + groupNumber);
        timerThread.start();

        /*Thread to save image bytes to files*/
        Thread saveBytesThread = new Thread(() -> {
            while (true) {
                if (oneSecond) {
                    try {
                        Thread saveFileThread = new Thread(() -> {
                            if (dequeImagesTime.size() > 0) {
                                int size = fpsDeque.size();
                                File temporaryFile = new File(Storage.getDefaultPath() + "\\buff\\" + groupNumber + "\\" + System.currentTimeMillis() + ".tmp");
                                int countImagesInFile = 0;
                                try {
                                    if (temporaryFile.createNewFile()) {
                                        FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
                                        for (int i = 0; i < size; i++) {
                                            Integer integer = fpsDeque.pollLast();
                                            if (integer != null) {
                                                for (int j = 0; j < integer; j++) {
                                                    Long aLong = dequeImagesTime.pollLast();
                                                    if (aLong != null) {
                                                        byte[] remove = buffMapImages.remove(aLong);
                                                        if (remove != null) {
                                                            try {
                                                                countImagesInFile++;
                                                                fileOutputStream.write(remove);
                                                            } catch (Exception e) {
                                                                log.error(e.getMessage());
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            totalCountFrames -= 1;//in case when temporary stream was not converted to byte array, but was added null to collection
                                                        }
                                                    }
//                                                    else {
//                                                        log.error("Потеряли кадр, время байта - НУЛЛ");
//                                                    }
                                                }
                                            }
                                        }
                                        try {
                                            fileOutputStream.flush();
                                            fileOutputStream.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                    e.printStackTrace();
                                }
                                File file = new File(Storage.getDefaultPath() + "\\buff\\" + groupNumber + "\\"
                                        + System.currentTimeMillis() + "-" + countImagesInFile + ".tmp");
                                if (temporaryFile.renameTo(file)) {
                                    fileDeque.addFirst(file);
                                    countsOfFramesInEachFile.put(file, countImagesInFile);
                                }
                            }

                            if (enableSaveVideo) {
                                if (stopSaveVideoInt >= Storage.getSecondsToSave() && totalCountFrames > 0) {
                                    stopSaveVideoInt = 0;
                                    VideoCreator.stopCatchVideo(containProgramCatchLightning);
                                    containProgramCatchLightning = false;
                                    log.info("Сохраняем данные. Группа номер - " + groupNumber);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("[");
                                    int iCount = 0;
                                    int currentTotalCountImage = totalCountFrames;

                                    for (Integer integer : eventsFramesNumber.keySet()) {
                                        iCount++;
                                        if (eventsFramesNumber.get(integer)) {
                                            stringBuilder.append("(").append(integer).append(")");
                                        } else {
                                            stringBuilder.append(integer);
                                        }
                                        if (iCount != eventsFramesNumber.size()) {
                                            stringBuilder.append(",");
                                        }
                                    }

                                    eventsFramesNumber.clear();
                                    stringBuilder.append("]");

                                    int totalFPSForFile = 0;
                                    int sizeFps = fpsList.size();
                                    for (int i = 0; i < sizeFps; i++) {
                                        Integer integer = fpsList.get(i);
                                        totalFPSForFile += integer;
                                    }

                                    for (int i = 0; i < sizeFps; i++) {
                                        fpsList.remove(0);
                                    }
                                    double d = (double) totalFPSForFile / sizeFps;
                                    totalFPSForFile = (int) (d + 0.5);

                                    String eventPercent = stringBuilder.toString();
                                    String path = folderToSave.getAbsolutePath() + "\\" + groupNumber + "(" + totalFPSForFile + ")"
                                            + eventPercent;

                                    File destFolder = new File(path);
                                    int size = fileDeque.size();
                                    int secondsCount = 0;

                                    if (destFolder.mkdirs()) {
                                        for (int i = 0; i < size; i++) {
                                            try {
                                                File fileToSave = fileDeque.pollLast();
                                                if (fileToSave != null) {
                                                    Integer remove = countsOfFramesInEachFile.remove(fileToSave);
                                                    totalCountFrames -= remove;//Отнимаем количество кадров, которое пересохранили в конце сохранения видео.
                                                    secondsCount++;
                                                    boolean reSave = fileToSave.renameTo(new File(destFolder, fileToSave.getName()));
                                                    if (!reSave) {
                                                        fileToSave.delete();
                                                    }
                                                }
                                            } catch (Exception ignored) {
                                            }
                                        }

                                        if (backGroundImage != null) {
                                            File imageFile = new File(folderToSave.getAbsolutePath() + "\\" + groupNumber + ".jpg");
                                            try {
                                                if (imageFile.createNewFile()) {
                                                    ImageIO.write(backGroundImage, "jpg", imageFile);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    log.info("Сохранили файл. Группа - " + groupNumber + ". " +
                                            "Кадров - " + currentTotalCountImage + ". " +
                                            "Файлов в буфере " + size + ". " +
                                            "Сохранили секунд " + secondsCount);
                                    enableSaveVideo = false;
                                }
                            } else {
                                while (fileDeque.size() > Storage.getSecondsToSave()) {
                                    try {
                                        if (!enableSaveVideo) {
                                            File fileToDel = fileDeque.pollLast();
                                            if (fileToDel != null) {
                                                Integer remove = countsOfFramesInEachFile.remove(fileToDel);
                                                totalCountFrames -= remove;//отнимаем количество кадров, которое было в временном файле, который удалили.

                                                if (eventsFramesNumber.size() != 0) {
                                                    Map<Integer, Boolean> temporaryMap = new TreeMap<>();
                                                    for (Integer integer : eventsFramesNumber.keySet()) {
                                                        temporaryMap.put(integer - remove, eventsFramesNumber.get(integer));
                                                    }
                                                    eventsFramesNumber.clear();
                                                    for (Integer integer : temporaryMap.keySet()) {
                                                        eventsFramesNumber.put(integer, temporaryMap.get(integer));
                                                    }
                                                }
                                                fpsList.remove(0);
                                                fileToDel.delete();
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.error(e.getMessage());
                                    }
                                }
                            }
                        });
                        saveFileThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    oneSecond = false;
                } else {
                    VideoCreator.isSaveVideoEnable();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        saveBytesThread.setName("Video Creator SaveBytesThread " + groupNumber);
        saveBytesThread.start();
    }

    public void setBackGroundImage(BufferedImage backGroundImage) {
        this.backGroundImage = backGroundImage;
    }

    public BufferedImage getBackGroundImage() {
        return backGroundImage;
    }

    public Camera[] getCameras() {
        return cameras;
    }

    void addImageBytes(long time, byte[] bytes) {
        while (dequeImagesTime.contains(time)) {
            time++;
        }

        if (!dequeImagesTime.contains(time)) {
            dequeImagesTime.addFirst(time);
            buffMapImages.put(time, bytes);//здесь складываем байты в кучку
            totalFPS++;
            totalCountFrames++;
        }
    }

    public void startSaveVideo(boolean programEventDetection, File folderToSave) {
        if (!containProgramCatchLightning) {
            containProgramCatchLightning = programEventDetection;
        }
        int imageNumber = totalCountFrames;
        boolean work = false;

        for (Camera camera : cameras) {
            work = camera.isCatchVideo();
            if (work) {
                break;
            }
        }

        if (work) {
            eventsFramesNumber.put(imageNumber, programEventDetection);
            if (!enableSaveVideo) {
                log.info("Начинаем запись. Группа " + groupNumber + ". Кадр номер - " + imageNumber + ". Время - " + System.currentTimeMillis());
                enableSaveVideo = true;
                this.folderToSave = folderToSave;
            } else {
                log.info("Продлжаем запись. Группа " + groupNumber + ". Кадр номер - " + imageNumber + ". Время - " + System.currentTimeMillis());
                stopSaveVideoInt = 0;
            }
        }
    }

    public void setCameras(Camera[] cameras) {
        this.cameras = cameras;
    }

    public int getGroupNumber() {
        return groupNumber;
    }
}
