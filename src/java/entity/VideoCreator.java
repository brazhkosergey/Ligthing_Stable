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

    private static boolean showInformMessage = false;

    private static boolean informFrameNewVideo = false;

    /**
     * @param programingLightCatch - program or sensor catch lightning
     */
    public static void startCatchVideo(boolean programingLightCatch) {
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
//                date = new Date(System.currentTimeMillis());
                folderForBytes = new File(Storage.getPath() + "\\bytes\\" + System.currentTimeMillis());
                boolean mkdirs = folderForBytes.mkdirs();
                if (mkdirs) {
                    log.info("Событие " + new Date(System.currentTimeMillis()).toString() + event + ". Сохраняем секунд - " + Storage.getSecondsToSave());
                    startSaveVideoForAllCreatorsThread = new Thread(() -> {
                        saveVideoEnable = true;
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

//                    HideZoneLightingSearcher.addHideZoneAreaName(date);

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
        SoundSaver soundSaver = Storage.getSoundSaver();
        if (soundSaver != null) {
            soundSaver.stopSaveAudio();
        }

        if (!showInformMessage) {
            showInformMessage = programCatchLightning;
        }

        if (showInformMessage) {
            MainFrame.showSecondsAlreadySaved(Storage.getBundle().getString("endofsavinglabel"));
            if (!informFrameNewVideo) {
                new NewVideoInformFrame();
                informFrameNewVideo = true;
            }
        } else {
            MainFrame.showSecondsAlreadySaved(" ");
        }
        saveVideoEnable = false;


        Thread lookingForHideZoneLightingThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            File storageFolder = new File(Storage.getPath() + "\\bytes\\");
            File[] videoFiles = storageFolder.listFiles();
            for (File file : videoFiles) {
                if (!file.getName().contains("{")) {

                    srtgvf
                    file.renameTo(new File(file.getAbsolutePath()+"{f5}"));

                    wrtvadf

                }
            }
        });
        lookingForHideZoneLightingThread.start();
    }


    static void restartNewVideoFrame() {
        VideoCreator.informFrameNewVideo = false;
    }

    public static void setShowInformMessage(boolean showInformMessage) {
        VideoCreator.showInformMessage = showInformMessage;
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