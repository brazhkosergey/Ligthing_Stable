package entity;

import entity.Camera.Camera;
import entity.Camera.CameraGroup;
import entity.Storage.Storage;
import entity.sound.SoundSaver;
import ui.main.MainFrame;
import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * class for start/stop save bytes from cameras, creating video files and save it to disk
 */
public class VideoCreator {
    private static Logger log = Logger.getLogger("file");
    /**
     * date for saving time, when lightning was
     */
//    private static Date date;
    private static File folderForBytes;
    /**
     * this boolean mark saving video now
     */
    private static boolean saveVideoEnable;
    /**
     * Thread run the starting video from cameras
     */
    private static Thread startSaveVideoForAllCreatorsThread;
    /**
     * Thread start work when have one more lightning during saving video, mark it,
     * and block marking lightnings more then one for one second.
     */
    private static Thread continueSaveVideoThread;
    /**
     * int to show on inform panel, how many seconds already saved
     */
    private static int secondVideoAlreadySave = 1;

    private static Set<CameraGroup> groups = new HashSet<>();

    /**
     * @param programingLightCatch - program or sensor catch lightning
     */
    public static synchronized void startCatchVideo(boolean programingLightCatch) {
        boolean anyCameraEnable = false;
        for (CameraGroup cameraGroup : Storage.getCameraGroups()) {
            for (Camera camera : cameraGroup.getCameras()) {
                if (!anyCameraEnable) {
                    anyCameraEnable = camera.isCatchVideo();
                } else {
                    break;
                }
            }
        }

        if (anyCameraEnable) {
            SoundSaver soundSaver = Storage.getSoundSaver();
            if (soundSaver != null) {
                soundSaver.startSaveAudio();
            }
            String event;
            if (programingLightCatch) {
                event = ". Сработка - програмная.";
            } else {
                event = ". Сработка - аппаратная.";
            }

            if (!saveVideoEnable) {
                saveVideoEnable = true;
                if (folderForBytes == null) {
                    long l = System.currentTimeMillis();
                    folderForBytes = new File(Storage.getPath() + "\\bytes\\" +l );
                    System.out.println("Запускаем сохранение, поток - " + Thread.currentThread().getName()+"\n" + "Name of File "+ l);
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
        }
    }

    /**
     * stop catch bytes from cameras
     */
    public static void stopCatchVideo(boolean programCatchLightning) {
        if (saveVideoEnable) {
            folderForBytes = null;
            saveVideoEnable = false;
            SoundSaver soundSaver = Storage.getSoundSaver();
            if (soundSaver != null) {
                soundSaver.stopSaveAudio();
            }
            if (programCatchLightning) {
                MainFrame.showSecondsAlreadySaved(Storage.getBundle().getString("endofsavinglabel"));
                NewVideoInformFrame.getNewVideoInformFrame();
            } else {
                MainFrame.showSecondsAlreadySaved(" ");
            }
        }
    }

    public static boolean informCreatorAboutStartingSaving(CameraGroup cameraGroup) {
        boolean add = groups.add(cameraGroup);
        return add;
    }

    public static void informCreatorAboutCompletingSaving(CameraGroup cameraGroup, File folderToScan) {
        groups.remove(cameraGroup);
        if (groups.size() == 0) {
            Thread lookingForHideZoneLightingThread = new Thread(() -> {
                HideZoneLightingSearcher.findHideZoneAreaAndRenameFolder(folderToScan);
            });
            lookingForHideZoneLightingThread.setPriority(Thread.MIN_PRIORITY);
            lookingForHideZoneLightingThread.start();
        }
    }

    public static void saveAudioBytes(Map<Long, byte[]> map) {
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
//        File audioFile = new File(Storage.getPath() + "\\bytes\\" + date.getTime() + ".wav");
        File audioFile = new File(folderForBytes.getAbsolutePath() + ".wav");
        final AudioInputStream audioStream = new AudioInputStream(interleavedStream, audioFormat, numberOfFrames);

        try {
            if (audioFile.createNewFile()) {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error(e1.getLocalizedMessage());
        }
    }

    public static boolean isSaveVideoEnable() {
        return saveVideoEnable;
    }
}