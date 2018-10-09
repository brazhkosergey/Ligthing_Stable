package entity.Camera;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import entity.Storage.Storage;
import org.apache.log4j.Logger;
import ui.camera.CameraPanel;
import ui.main.MainFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServiceCamera {
    private static Logger log = Logger.getLogger("file");

    public static void encodeVideoXuggle(File folderWithTemporaryFiles) {
        String name = folderWithTemporaryFiles.getParentFile().getName();
        String[] split = name.split("\\{");
        long dateLong = Long.parseLong(split[0]);

        Date date = new Date(dateLong);
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern("dd MMMM yyyy,HH-mm-ss");
        String dateString = dateFormat.format(date);

        String audioPath = Storage.getPath() + "\\bytes\\" + dateLong + ".wav";
        File audioFile = new File(audioPath);
        if (audioFile.exists()) {
            File newAudioFile = new File(Storage.getPath() + "\\" + dateString + ".wav");
            try {
                if (newAudioFile.createNewFile()) {
                    log.info("Сохраняем аудиофайл " + newAudioFile.getAbsolutePath());
                    FileInputStream fileInputStream = new FileInputStream(audioFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(newAudioFile);
                    byte[] buff = new byte[1024];
                    while (fileInputStream.read(buff) > 0) {
                        fileOutputStream.write(buff);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileInputStream.close();
                    audioFile.delete();
                    log.info("Аудиофайл сохранен. " + newAudioFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        name = folderWithTemporaryFiles.getName();
        String numberOfGroupCameraString = name.substring(0, 1);
        int i = name.indexOf(")");
        String totalFpsString = name.substring(2, i);
        int totalFPS = Integer.parseInt(totalFpsString);

        String path = Storage.getPath() + "\\" + dateString + ", group -" + numberOfGroupCameraString + ".mp4";
        log.info("Сохраняем видеофайл " + path);
        BufferedImage imageToConnect = null;
        boolean connectImage = false;

        String absolutePathToImage = folderWithTemporaryFiles.getParentFile().getAbsolutePath() + "\\" + numberOfGroupCameraString + ".jpg";
        File imageFile = new File(absolutePathToImage);
        if (imageFile.exists()) {
            try {
                imageToConnect = ImageIO.read(new FileInputStream(imageFile));
                connectImage = true;
                log.info("Накладываем изображение на файл " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        File videoFile = new File(path);
        if (videoFile.exists()) {
            videoFile.delete();
        }

        try {
            if (videoFile.createNewFile()) {
                final IMediaWriter writer = ToolFactory.makeWriter(path);
                boolean addVideoStream = false;
                long nextFrameTime = 0;
                final long frameRate = (1000 / totalFPS);
                int count = 0;
                int countImageNotSaved = 0;

                FileInputStream fileInputStream = null;
                File[] temporaryFiles = folderWithTemporaryFiles.listFiles();


                int heightStream = 0;
                int weightStream = 0;

                for (File file : temporaryFiles) {
                    try {
                        fileInputStream = new FileInputStream(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    BufferedInputStream bufferedInputStream = null;
                    if (fileInputStream != null) {
                        bufferedInputStream = new BufferedInputStream(fileInputStream);
                        ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream(65535);

                        int x = 0;
                        int t;

                        BufferedImage image = null;
                        while (x >= 0) {
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
                                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

                                try {
                                    image = ImageIO.read(inputStream);
                                } catch (Exception ignored) {
                                }

                                if (image != null) {
                                    if (!addVideoStream) {
                                        heightStream = image.getHeight();
                                        weightStream = image.getWidth();

                                        writer.addVideoStream(0, 0,
                                                ICodec.ID.CODEC_ID_MPEG4,
                                                image.getWidth(), image.getHeight());
                                        addVideoStream = true;
                                    }

                                    try {
                                        if (connectImage) {
                                            BufferedImage bufferedImage = connectImage(image, imageToConnect, Storage.getOpacitySetting());
                                            writer.encodeVideo(0, bufferedImage, nextFrameTime, TimeUnit.MILLISECONDS);
                                        } else {
                                            writer.encodeVideo(0, image, nextFrameTime,
                                                    TimeUnit.MILLISECONDS);
                                        }
                                    } catch (Exception e) {
                                        count--;
                                        countImageNotSaved++;
//                                        System.out.println("Размер потока - " + heightStream + " : " + weightStream);
//                                        System.out.println("Размер ИЗОБРАЖЕНИЯ - " + image.getHeight() + " : " + image.getWidth());
//                                        System.out.println("Изображение другого размера ");
//                                        System.out.println("Сохранено - " + count);
//                                        System.out.println("НЕ СОХРАНЕНО " + countImageNotSaved);
                                    }

                                    image = null;
                                    if (count % 2 == 0) {
                                        MainFrame.showInformMassage(Storage.getBundle().getString("saveframenumber") +
                                                count++, new Color(23, 114, 26));
                                    } else {
                                        MainFrame.showInformMassage(Storage.getBundle().getString("saveframenumber") +
                                                count++, new Color(181, 31, 27));
                                    }
                                    nextFrameTime += frameRate;
                                } else {
                                    countImageNotSaved++;
                                }
                            }
                        }
                        temporaryStream.close();
                        fileInputStream.close();
                        bufferedInputStream.close();
                    }
                }

                writer.flush();
                writer.close();
                MainFrame.showInformMassage(Storage.getBundle().getString("encodingdone") + count, new Color(23, 114, 26));

                Date videoLenght = new Date(nextFrameTime);
                dateFormat.applyPattern("mm:ss");
                log.info("Видеофайл сохранен - " + path +
                        ". Сохранено кадров - " + count +
                        ". Не сохранено кадров - " + countImageNotSaved +
                        ". Длинна видео - " + dateFormat.format(videoLenght));
                System.out.println("Видеофайл сохранен - " + path +
                        ". Сохранено кадров - " + count +
                        ". Не сохранено кадров - " + countImageNotSaved +
                        ". Длинна видео - " + dateFormat.format(videoLenght));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    public static BufferedImage connectImage(BufferedImage sourceImage, BufferedImage imageToConnect, float opacity) {
        BufferedImage image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        graphics.drawImage(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        graphics.drawImage(imageToConnect, 0, 0, imageToConnect.getWidth(), imageToConnect.getHeight(), null);
        graphics.dispose();
        return image;
    }


    public static void savePartOfVideoFile(String pathToFileToSave, List<File> filesListToEncode, int totalFPS, BufferedImage backgroundImage) {
        File videoFile = new File(pathToFileToSave);
        if (videoFile.exists()) {
            videoFile.delete();
        }
        boolean connectImage = false;
        float opacity = 0;
        if (backgroundImage != null) {
            connectImage = true;
            opacity = Storage.getOpacitySetting();
        }

        try {
            if (videoFile.createNewFile()) {
                final IMediaWriter writer = ToolFactory.makeWriter(pathToFileToSave);
                boolean addVideoStream = false;
                long nextFrameTime = 0;
                final long frameRate = (1000 / totalFPS);
                int count = 0;
                int countImageNotSaved = 0;

                FileInputStream fileInputStream = null;

                for (File file : filesListToEncode) {
                    try {
                        fileInputStream = new FileInputStream(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    BufferedInputStream bufferedInputStream = null;
                    if (fileInputStream != null) {
                        bufferedInputStream = new BufferedInputStream(fileInputStream);
                        ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream(65535);

                        int x = 0;
                        int t;

                        BufferedImage image = null;
                        while (x >= 0) {
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
                                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

                                try {
                                    image = ImageIO.read(inputStream);
                                } catch (Exception ignored) {
                                }

                                if (image != null) {
                                    if (!addVideoStream) {
                                        writer.addVideoStream(0, 0,
                                                ICodec.ID.CODEC_ID_MPEG4,
                                                image.getWidth(), image.getHeight());
                                        addVideoStream = true;
                                    }

                                    if (connectImage) {
                                        writer.encodeVideo(0, connectImage(image, backgroundImage, opacity), nextFrameTime,
                                                TimeUnit.MILLISECONDS);
                                    } else {
                                        writer.encodeVideo(0, image, nextFrameTime,
                                                TimeUnit.MILLISECONDS);
                                    }
                                    image = null;


                                    if (count % 2 == 0) {
                                        MainFrame.showInformMassage(Storage.getBundle().getString("saveframenumber") +
                                                count++, new Color(23, 114, 26));
                                    } else {
                                        MainFrame.showInformMassage(Storage.getBundle().getString("saveframenumber") +
                                                count++, new Color(181, 31, 27));
                                    }
                                    nextFrameTime += frameRate;
                                } else {
                                    countImageNotSaved++;
                                }
                            }
                        }
                        temporaryStream.close();
                        fileInputStream.close();
                        bufferedInputStream.close();
                    }
                }

                writer.flush();
                writer.close();
                MainFrame.showInformMassage(Storage.getBundle().getString("encodingdone") + count, new Color(23, 114, 26));

                log.info("Видеофайл сохранен - " + pathToFileToSave +
                        ". Сохранено кадров - " + count +
                        ". Не сохранено кадров - " + countImageNotSaved);
                System.out.println("Видеофайл сохранен - " + pathToFileToSave +
                        ". Сохранено кадров - " + count +
                        ". Не сохранено кадров - " + countImageNotSaved);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    public static BufferedImage changeOpacity(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Storage.getOpacitySetting()));
        g.drawImage(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        g.dispose();
        return resizedImage;
    }
}
