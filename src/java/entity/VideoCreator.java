package entity;

import entity.Camera.Camera;
import entity.Camera.CameraGroup;
import entity.Storage.Storage;
import entity.sound.SoundSaver;
import ui.main.MainFrame;
import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * class for start/stop save bytes from cameras, creating video files and save it to disk
 */
public class VideoCreator {
    private static Logger log = Logger.getLogger("file");


    private static VideoCreator videoCreator;

    /**
     * date for saving time, when lightning was
     */
//    private static Date date;
    private File folderForBytes;
    /**
     * this boolean mark saving video now
     */
    private boolean saveVideoEnable;
    /**
     * Thread run the starting video from cameras
     */
    private Thread startSaveVideoForAllCreatorsThread;
    /**
     * Thread start work when have one more lightning during saving video, mark it,
     * and block marking lightnings more then one for one second.
     */
    private Thread continueSaveVideoThread;
    /**
     * int to show on inform panel, how many seconds already saved
     */
    private int secondVideoAlreadySave = 1;

    private Set<CameraGroup> groups = new HashSet<>();

    boolean anyCameraEnable = false;


    private boolean threadInside = false;
    private static boolean test = false;


    public static VideoCreator getVideoCreator() {
        if (videoCreator == null) {
            videoCreator = new VideoCreator();

            Thread testThread = new Thread(() -> {
                while (true) {
                    test = true;

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            testThread.start();
        }
        return videoCreator;
    }


    public static boolean isTest() {
        return test;
    }

    public void setAnyCameraEnable(boolean anyCameraEnable) {
        this.anyCameraEnable = anyCameraEnable;
    }

    public synchronized boolean isThreadInside() {
        return threadInside;
    }

    /**
     * @param programingLightCatch - program or sensor catch lightning
     */
    public void startCatchVideo(boolean programingLightCatch) {
        if (anyCameraEnable) {
            if (!isThreadInside()) {
                test = false;
                threadInside = true;
                String event;
                if (programingLightCatch) {
                    event = ". Сработка - програмная.";
                } else {
                    event = ". Сработка - аппаратная.";
                }
                if (!saveVideoEnable) {
                    saveVideoEnable = true;
                    if (folderForBytes == null) {
                        SoundSaver soundSaver = Storage.getSoundSaver();
                        if (soundSaver != null) {
                            soundSaver.startSaveAudio();
                        }
                        long l = System.currentTimeMillis();
                        folderForBytes = new File(Storage.getPath() + "\\bytes\\" + l);
                        System.out.println("Запускаем сохранение, поток - " + Thread.currentThread().getName() + "\n" + "Name of File " + l);
                        if (folderForBytes.mkdirs()) {
                            log.info("Событие " + new Date(l).toString() + event + ". Сохраняем секунд - " + Storage.getSecondsToSave());
                            startSaveVideoForAllCreatorsThread = new Thread(() -> {
                                while (saveVideoEnable) {
                                    MainFrame.showSecondsAlreadySaved(Storage.getBundle().getString("savedword") +
                                            (secondVideoAlreadySave++) + Storage.getBundle().getString("seconds"));
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                secondVideoAlreadySave = 1;
                                startSaveVideoForAllCreatorsThread = null;
                            });
                            startSaveVideoForAllCreatorsThread.start();
                        }
                    }
                } else {
                    log.info("Еще одна сработка, продолжаем событие " + new Date(System.currentTimeMillis()).toString() + " " + event);
                    System.out.println("Продолжаем сохранение, поток - " + Thread.currentThread().getName() + "\n" + "Name of File " + folderForBytes.getName());
                    secondVideoAlreadySave = 1;
                }

                if (continueSaveVideoThread == null) {
                    continueSaveVideoThread = new Thread(() -> {
                        for (CameraGroup cameraGroup : Storage.getCameraGroups()) {
                            cameraGroup.startSaveVideo(programingLightCatch, folderForBytes);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continueSaveVideoThread = null;
                    });
                    continueSaveVideoThread.start();
                } else {
                    log.info("С прошлой сработки не прошло 2 секунды.");
                }
                threadInside = false;
            }
        }
    }

    /**
     * stop catch bytes from cameras
     */
    public void stopCatchVideo(boolean programCatchLightning) {
        if (saveVideoEnable) {
            System.out.println("==========================COMPLETE==========================");
            String path = folderForBytes.getAbsolutePath();
            folderForBytes = null;
            saveVideoEnable = false;
            SoundSaver soundSaver = Storage.getSoundSaver();
            if (soundSaver != null) {
                soundSaver.stopSaveAudio(path);
            }
            if (programCatchLightning) {
                MainFrame.showSecondsAlreadySaved(Storage.getBundle().getString("endofsavinglabel"));
                NewVideoInformFrame.getNewVideoInformFrame();
            } else {
                MainFrame.showSecondsAlreadySaved(" ");
            }
        }
    }

    public boolean informCreatorAboutStartingSaving(CameraGroup cameraGroup) {
        boolean add = groups.add(cameraGroup);
        return add;
    }

    public void informCreatorAboutCompletingSaving(CameraGroup cameraGroup, File folderToScan) {
        groups.remove(cameraGroup);
        if (groups.size() == 0) {
            Thread lookingForHideZoneLightingThread = new Thread(() -> {
                HideZoneLightingSearcher.findHideZoneAreaAndRenameFolder(folderToScan);
            });
            lookingForHideZoneLightingThread.setPriority(Thread.MIN_PRIORITY);
            lookingForHideZoneLightingThread.start();
        }
    }

    public void saveAudioBytes(Map<Long, byte[]> map, String pathToFile) {
        int size = 0;
        List<Long> longList = new ArrayList<>();

        for (Long l : map.keySet()) {
            longList.add(l);
        }

        Collections.sort(longList);
        for (Long integer : map.keySet()) {
            byte[] bytes = map.get(integer);
            size = size + bytes.length;
        }

        ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream(35535);
        for (Long l : longList) {
            byte[] bytes = map.get(l);
            try {
                temporaryStream.write(bytes);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        ByteArrayInputStream interleavedStream = new ByteArrayInputStream(temporaryStream.toByteArray());
        final AudioFormat audioFormat = new AudioFormat(
                AudioFormat.Encoding.ULAW,
                8000f, // sample rate - you didn't specify, 44.1k is typical
                8, // how many bits per sample, i.e. per value in your byte array
                1,       // you want two channels (stereo)
                1,      // number of bytes per frame (frame == a sample for each channel)
                8000f,  // frame rate
                true);  // byte order

        final int numberOfFrames = size;

        File file = new File(pathToFile);

        Date date = new Date(Long.parseLong(file.getName()));
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("dd MMMM yyyy,HH-mm-ss");
        String dateString = dateFormat.format(date);

        String path = file.getParentFile().getAbsolutePath() + "\\" + dateString + ".wav";
        System.out.println(path);
        File audioFile = new File(path);
        final AudioInputStream audioStream = new AudioInputStream(interleavedStream, audioFormat, numberOfFrames);
        try {
            if (audioFile.createNewFile()) {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
                System.out.println("Complete");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error(e1.getLocalizedMessage());
        }
    }

    public boolean isSaveVideoEnable() {
        return saveVideoEnable;
    }
}