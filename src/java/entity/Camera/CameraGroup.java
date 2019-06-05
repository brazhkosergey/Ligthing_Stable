package entity.Camera;

import entity.HideZoneLightingSearcher;
import entity.Storage.Storage;
import entity.VideoCreator;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private boolean groupReceiveData;

    /**
     * mark one second gone
     */
    private boolean oneSecond = false;
    /**
     * total count of frames in temporary files and RAM
     */
    private volatile int totalCountFrames;

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

    private boolean informVideoCreatorAboutStartingSaving = false;

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

                long start = System.currentTimeMillis();
                try {
                    groupReceiveData = false;
                    for (Camera camera : cameras) {
                        if (!groupReceiveData) {
                            groupReceiveData = camera.isCatchVideo();
                            if (fileDeque.size() >= Storage.getSecondsToSave()) {
                                camera.getCameraPanel().getTitle().setTitleColor(new Color(70, 193, 84));
                            } else {
                                camera.getCameraPanel().getTitle().setTitleColor(Color.RED);
                            }
                        }
                    }
                    if (groupReceiveData) {
                        fpsList.add(totalFPS);
                        fpsDeque.addFirst(totalFPS);
                        totalFPS = 0;
                        oneSecond = true;
                        if (enableSaveVideo) {
                            stopSaveVideoInt++;
                        }
                    } else {
                        while (fileDeque.size() > 0) {
                            try {
                                File fileToDel = fileDeque.pollLast();
                                Integer remove = countsOfFramesInEachFile.remove(fileToDel);
                                decNumberOfFramesFromTotalCount(remove);//Удаляем все временные файлы, в случае если камеры будут отключены.
                                deleteFile(fileToDel);
                                fpsList.remove(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error(e.getMessage());
                            }
                        }
                        stopSaveVideoInt = 0;
                    }
                    Thread.sleep(1000 - (System.currentTimeMillis() - start));
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
                        if (dequeImagesTime.size() > 0) {
                            createTempFile();
                        }

                        if (enableSaveVideo) {
                            if (!informVideoCreatorAboutStartingSaving) {
                                informVideoCreatorAboutStartingSaving = VideoCreator.getVideoCreator().informCreatorAboutStartingSaving(this);
                            }
                            if (stopSaveVideoInt >= Storage.getSecondsToSave() && getTotalCountFrames() > 0) {
                                saveVideoToStorage();
                            }
                        } else {
                            cleanBuffer();
                        }
                        oneSecond = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    VideoCreator.getVideoCreator().isSaveVideoEnable();
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

    private void createTempFile() {
        int size = fpsDeque.size();
        File temporaryFile = new File(Storage.getDefaultPath() + "\\buff\\" + groupNumber + "\\" + System.currentTimeMillis() + ".tmp");
        int countImagesInFile = 0;


        try {
            if (temporaryFile.createNewFile()) {
                temporaryFile.deleteOnExit();
                FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
                for (int i = 0; i < size; i++) {
                    Integer integer = fpsDeque.pollLast();
                    if (integer != null) {
                        for (int j = 0; j < integer; j++) {
                            Long aLong = dequeImagesTime.pollLast();
                            if (aLong != null) {
                                byte[] remove = buffMapImages.remove(aLong);
                                try {
                                    fileOutputStream.write(remove);
                                    countImagesInFile++;
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        File file = new File(Storage.getDefaultPath() + "\\buff\\" + groupNumber + "\\"
                + System.currentTimeMillis() + "-" + countImagesInFile + ".tmp");
        file.deleteOnExit();
        Path moveFrom = Paths.get(temporaryFile.getAbsolutePath());
        Path moveTo = Paths.get(file.getAbsolutePath());
        HideZoneLightingSearcher.renameFile(moveFrom, moveTo, true);
        fileDeque.addFirst(file);
        countsOfFramesInEachFile.put(file, countImagesInFile);
    }

    private void cleanBuffer() {
        while (fileDeque.size() > Storage.getSecondsToSave()) {
            try {
                if (!enableSaveVideo) {
                    File fileToDel = fileDeque.pollLast();
                    if (fileToDel != null) {
                        Integer remove = countsOfFramesInEachFile.remove(fileToDel);
                        decNumberOfFramesFromTotalCount(remove);//отнимаем количество кадров, которое было в временном файле, который удалили.
                        fpsList.remove(0);
                        deleteFile(fileToDel);
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
    }

    private void saveVideoToStorage() {
        stopSaveVideoInt = 0;
        VideoCreator.getVideoCreator().stopCatchVideo(containProgramCatchLightning);
        containProgramCatchLightning = false;
        log.info("Сохраняем данные. Группа номер - " + groupNumber);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        int iCount = 0;
        int currentTotalCountImage = getTotalCountFrames();

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
        for (Integer integer : fpsList) {
            totalFPSForFile += integer;
        }

        int sizeFps = fpsList.size();
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
                        decNumberOfFramesFromTotalCount(remove);//Отнимаем количество кадров, которое пересохранили в конце сохранения видео.
                        secondsCount++;
                        Path from = Paths.get(fileToSave.getAbsolutePath());
                        Path to = Paths.get(destFolder.getAbsolutePath() + "\\" + fileToSave.getName());
                        HideZoneLightingSearcher.renameFile(from, to, false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VideoCreator.getVideoCreator().informCreatorAboutCompletingSaving(this, folderToSave);
        enableSaveVideo = false;
        informVideoCreatorAboutStartingSaving = false;
    }

    private synchronized void incCountImage() {
        totalCountFrames++;
    }

    private synchronized int getTotalCountFrames() {
        return totalCountFrames;
    }

    private synchronized void decNumberOfFramesFromTotalCount(int number) {
        totalCountFrames -= number;
    }

    private void deleteFile(File fileToDel) {
        Thread thread = new Thread(() -> {
            boolean del;
            do {
                del = fileToDel.delete();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            } while (!del);
        });
        thread.start();
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

    synchronized void addImageBytes(long time, byte[] bytes) {
        if (bytes != null) {
            while (dequeImagesTime.contains(time)) {
                time++;
            }

            if (!dequeImagesTime.contains(time)) {
                dequeImagesTime.addFirst(time);
                buffMapImages.put(time, bytes);//здесь складываем байты в кучку
                totalFPS++;
                incCountImage();
            }
        }
    }

    public void startSaveVideo(boolean programEventDetection, File folderToSave) {
        boolean work = false;
        do {
            for (Camera camera : cameras) {
                work = camera.isCatchVideo();
                if (work) {
                    break;
                }
            }
            if (!containProgramCatchLightning) {
                containProgramCatchLightning = programEventDetection;
            }
        } while (oneSecond);

        if (work) {
            int imageNumber = getTotalCountFrames();
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
