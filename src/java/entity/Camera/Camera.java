package entity.Camera;

import entity.VideoCreator;
import entity.Storage.Storage;
import org.apache.log4j.Logger;
import ui.camera.CameraPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Camera {


    private static Logger log = Logger.getLogger("file");

    private int number;
    private CameraGroup cameraGroup;
    /**
     * for counting fps from camera
     */
    private int fps;
    /**
     * for counting fps of showed images to camera panel
     */
    private int fpsShow = 0;
    /**
     * used for check connections to camera, and reconnect
     */
    private int countSecondsToHaveNotImage;

    /**
     * deque whit bytes from camera
     */
    private Deque<byte[]> bytesForImagesToShowDeque;

    private URL url;
    private HttpURLConnection connection = null;
    private BufferedInputStream bufferedInputStream;
    private InputStream inputStream;
    private ByteArrayOutputStream temporaryStream = null;

    private CameraPanel cameraPanel;

    private boolean restart;
    private boolean catchVideo;
    private boolean changeURL;

    private Thread fpsCountThread;
    /**
     * Thread to scan images for white count, and showing it to camera panel
     */
    private Thread showImageToCameraPanelThread;
    /**
     * thread to read bytes from camera and send it video creator
     */
    private Thread readBytesThread;

    /**
     * mark that program finish to scan and showing image,
     * and possible to start to scan next image, after it main thread will add one image to "bytesForImagesToShowDeque"
     */
    private boolean showImage = true;

    /**
     * counts of white percent of last 10 images
     */
    private Deque<Integer> whiteDeque;

    public Camera(int number, CameraGroup cameraGroup) {
        this.cameraGroup = cameraGroup;
        this.number = number;
        whiteDeque = new ConcurrentLinkedDeque<>();
        fpsCountThread = new Thread(() -> {
            while (true) {
                if (catchVideo) {
                    if (fps != 0) {
                        cameraPanel.getTitle().setTitle("FPS = " + fps + " : " + fpsShow);
                        cameraPanel.repaint();
                        fps = 0;
                        fpsShow = 0;
                        countSecondsToHaveNotImage = 0;
                    } else {
                        if (!restart) {
                            countSecondsToHaveNotImage++;
                            if (countSecondsToHaveNotImage > 10) {
                                restart = true;
                                cameraPanel.stopShowVideo();
                                bufferedInputStream = null;
                                countSecondsToHaveNotImage = 0;
                                createInputStream();
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        showImageToCameraPanelThread = new Thread(() -> {
            while (true) {
                if (catchVideo) {
                    byte[] bytes;
                    int totalMillisecondsToShowFrame = 0;
                    if (bytesForImagesToShowDeque.size() > 0) {
                        bytes = bytesForImagesToShowDeque.pollLast();
                        if (bytes != null) {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            try {
                                long startTime = System.currentTimeMillis();
                                ImageIO.setUseCache(false);
                                BufferedImage image = ImageIO.read(inputStream);
                                inputStream.close();

                                cameraPanel.setBufferedImage(CameraPanel.processImage(scanCountOfWhitePixelsPercent(image), cameraPanel.getWidth(), cameraPanel.getHeight()));
                                cameraPanel.repaint();
                                fpsShow++;
                                totalMillisecondsToShowFrame = (int) (System.currentTimeMillis() - startTime);
                            } catch (Exception ignored) {
                            }
                            showImage = true;
                        }

                        int timeToSleep = 1000 / Storage.getShowFramesPercent();
                        int diff = timeToSleep - totalMillisecondsToShowFrame;
                        if (diff > 0) {
                            try {
                                Thread.sleep(diff);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {

                    cameraPanel.getTitle().setTitle(Storage.getBundle().getString("cameradoesnotwork"));

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        showImageToCameraPanelThread.setPriority(Thread.MIN_PRIORITY);

        readBytesThread = new Thread(() -> {
            showImageToCameraPanelThread.start();
            fpsCountThread.start();
            log.info("Запускаем наблюдатель для камеры номер " + number);

            int x = 0;
            int t;

            while (true) {
                while (catchVideo) {
                    try {
                        if (bufferedInputStream == null) {
                            if (temporaryStream != null) {
                                temporaryStream.close();
                            }
                            temporaryStream = new ByteArrayOutputStream(35535);
                            createInputStream();
                        } else {
                            t = x;
                            try {
                                x = bufferedInputStream.read();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            temporaryStream.write(x);
                            if (x == 216 && t == 255) {// начало изображения
                                temporaryStream.reset();

                                temporaryStream.write(t);
                                temporaryStream.write(x);
                            } else if (x == 217 && t == 255) {//конец изображения
                                byte[] bytes = temporaryStream.toByteArray();
                                if (showImage) {
                                    bytesForImagesToShowDeque.addFirst(bytes);
                                    showImage = false;
                                }
                                cameraGroup.addImageBytes(System.currentTimeMillis(), bytes);
                                fps++;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Error error) {
                        log.error(error.getLocalizedMessage());
                        error.printStackTrace();
                    }
                }

                if (!catchVideo) {
                    try {
                        if (temporaryStream != null) {
                            temporaryStream.close();
                            temporaryStream = null;
                        }

                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                            bufferedInputStream = null;
                        }

                        if (inputStream != null) {
                            inputStream.close();
                            inputStream = null;
                        }

                        if (connection != null) {
                            connection.disconnect();
                            connection = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (changeURL) {
                    catchVideo = true;
                    changeURL = false;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        readBytesThread.setPriority(Thread.MAX_PRIORITY);
        bytesForImagesToShowDeque = new ConcurrentLinkedDeque<>();
    }

    public void start() {
        readBytesThread.setName("Save Stream Thread. Camera " + number);
        showImageToCameraPanelThread.setName("Update Data Thread. Camera " + number);
        fpsCountThread.setName("FPS CountThread. Camera " + number);
        readBytesThread.start();
    }

    public int getNumber() {
        return number;
    }

    private BufferedImage scanCountOfWhitePixelsPercent(BufferedImage bi) {
        if (Storage.isProgramLightCatchEnable()) {
            int countWhite = 0;
            int totalCount = 0;

            for (int y = 0; y < bi.getHeight(); y += 3) {
                for (int x = 0; x < bi.getWidth(); x += 3) {
                    totalCount++;
                    if (Storage.getColorRGBNumberSet().contains(bi.getRGB(x, y))) {
                        countWhite++;
                    }
                }
            }
            int percentOfWhite = countWhite * 100000 / totalCount;
            whiteDeque.addFirst(percentOfWhite);

            System.out.println("Процентов белого - " + percentOfWhite);
            if (whiteDeque.size() > 5) {
                int total = 0;
                for (Integer integer : whiteDeque) {
                    total += integer;
                }
                int average = total / whiteDeque.size();
                if (countWhite != 0) {
                    int differentWhitePixelsAverage = (percentOfWhite) - average;
                    if (differentWhitePixelsAverage > 0) {
                        if (average != 0) {
                            int diffPercent = differentWhitePixelsAverage * 100 / average;
                            if (diffPercent > Storage.getPercentDiffWhite() * 35) {
                                VideoCreator.startCatchVideo(true);
                                whiteDeque.clear();
                            }
                        }
                    }
                } else {
                    if (average != 0) {
                        whiteDeque.clear();
                    }
                }
                whiteDeque.pollLast();
            }
        }
        return bi;
    }

    private void createInputStream() {
        if (url != null) {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

                connection = (HttpURLConnection) url.openConnection();
                inputStream = connection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);
                cameraPanel.startShowVideo();
                restart = false;
            } catch (Exception e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (!restart && catchVideo) {
                    createInputStream();
                } else {
                    cameraPanel.getTitle().setTitle(Storage.getBundle().getString("restoreconnection"));
                    cameraPanel.repaint();
                }
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(catchVideo + " URL = NULL " + Thread.currentThread().getName() + "- " + changeURL);
            catchVideo = false;
        }
    }

    public void startReceiveVideoThread(String ipAddress, String userName, String password) {
        URL urlMainStream = null;
        if (ipAddress != null && ipAddress.length() > 4) {
            try {
                urlMainStream = new URL(ipAddress);
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password.toCharArray());
                    }
                });
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        startCatchVideo(urlMainStream);
    }

    private void startCatchVideo(URL urlMainStream) {
        if (urlMainStream != null) {
            if (this.url != null) {
                changeURL = true;
                catchVideo = false;
            } else {
                catchVideo = true;
                cameraPanel.startShowVideo();
            }
        } else {
            catchVideo = false;
            cameraPanel.stopShowVideo();
        }
        this.url = urlMainStream;
    }

    public void setCameraPanel(CameraPanel cameraPanel) {
        this.cameraPanel = cameraPanel;
    }

    public boolean isCatchVideo() {
        return catchVideo;
    }

    public CameraPanel getCameraPanel() {
        return cameraPanel;
    }

    public CameraGroup getCameraGroup() {
        return cameraGroup;
    }
}
