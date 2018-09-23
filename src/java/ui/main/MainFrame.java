package ui.main;

import entity.Camera.Camera;
import entity.Camera.CameraGroup;
import entity.VideoCreator;
import entity.Storage.Storage;
import ui.camera.CameraPanel;
import ui.video.VideoPlayer;
import ui.setting.CameraAddressSetting;
import ui.setting.Setting;
import ui.video.VideoFilesPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * App frame
 */
public class MainFrame extends JFrame {
    private static Logger log = Logger.getLogger("file");

    /**
     * TEST MODE PANELS
     */
    private static JButton startEventButton;
    private static JButton startButtonProgrammingCatch;
    private static JLabel testModeLabel = new JLabel();
    /**
     * MAIN FRAME
     */
    private static MainFrame mainFrame;
    private static JPanel mainPanel;
    private static JPanel northPanel;
    private static JPanel centralPanel;
    private static JPanel southPanel;
    /**
     * show count of packets from audio module for one second,
     * and will be red if have exception during creating server socket on port, for waiting event from sensor
     */
    public static JLabel audioPacketCount;

    /**
     * NORTH PANEL
     */
    private static JLabel mainLabel;
    private JLabel recordLabel;
    private static JLabel recordSecondsLabel;

    /**
     * CENTRAL PANEL
     */
    private static CameraAddressSetting cameraAddressSetting;
    private static Setting setting;
    private static VideoFilesPanel videoFilesPanel;
    private JPanel allCameraPanel;

    /**
     * SOUTH PANEL
     */
    private static JLabel eventServerPortLabel;
    private static JLabel countSecondsToSaveVideo;
    private static JLabel opacityLabel;
    private static JLabel showImagesLabel;
    private static JLabel photosensitivityLabel;
    private static JLabel changeWhiteLabel;
    private static JLabel informLabel;


    private static boolean fullSize = false;
    private static Map<Integer, CameraPanel> cameraPanelsMap;
    private static Map<Integer, JPanel> cameraBlock;

//    /**
//     * cameraPanelsMap panel list, numbers (1-8)
//     */
//    private static Map<Integer, CameraPanel> cameraPanelsMap;
//    /**
//     * addresses for cameraPanelsMap
//     */
//    public static Map<Integer, List<String>> camerasAddress;
//    /**
//     * camera blocks (4)
//     */
//    private static Map<Integer, JPanel> cameraBlock;
//    /**
//     * backgrounds for each block
//     */
//    public static Map<Integer, BufferedImage> imagesForBlock;
//    /**
//     * video savers for each block
//     */
//    public static Map<Integer, VideoBytesSaver> videoSaversMap;
//
//    public static Map<Integer, int[][]> linePoints;
//    public static Map<Integer, List<int[]>> linesForHideZoneParsing;
//
//    public static int[][] camerasPosition;
//
//    /**
//     * address saver to save and restore setting data
//     */
//    public static AddressSaver addressSaver;
//    /**
//     * opacity of background
//     */
//    private static int opacitySetting;
//    /**
//     * seconds to save before and after lightning
//     */
//    private static int secondsToSave = 30;
//
//    /**
//     * used for scanning frames for count of white pixels to catch lightning
//     */
//    private static int percentDiffWhite = 5;
//
//    /**
//     * the darkest color number, which will be set as "WHITE"
//     * Color color= new Color(colorLightNumber,colorLightNumber,colorLightNumber);
//     * program will search on frame pixels more light then this color
//     */
//    private static int colorLightNumber = 180;
//
//    /**
//     * set with RGB numbers of color, which will bet "WHITE"
//     */
//    private Set<Integer> colorRGBNumberSet;
//    /**
//     * enable/disable scanning frames to catch lightning
//     */
//    private static boolean programLightCatchEnable;
//    /**
//     * port for server socket, for waiting request from sensor about lightning
//     */
//    private static int port;
//
//    /**
//     * path to folder, for saved bytes, during saving video
//     * can be changed in setting
//     */
//    private static String path = "C:\\LIGHTNING_STABLE\\";
//
//    /**
//     * default path for temporary files, log files and address saver files
//     * can not be changed
//     */
//    private static String defaultPath = "C:\\LIGHTNING_STABLE\\";
//
//    /**
//     * class to save audio fro audio module
//     */
//    private SoundSaver soundSaver;
//
//    /**
//     * mark that some camera is full size mode
//     */
//    private static boolean fullSize = false;
//
//
//    private static ResourceBundle bundle;
//    /**
//     * show the percent of frames from ip camera, witch will be showed on camera panel
//     */
//    private static int showFramesPercent = 15;

    private MainFrame() {
        super("LIGHTNING STABLE");
        cameraPanelsMap = new HashMap<>();
        cameraBlock = new HashMap<>();
//        imagesForBlock = new HashMap<>();
//        cameraPanelsMap = new HashMap<>();
//        cameraBlock = new HashMap<>();
//        camerasAddress = new HashMap<>();
//        videoSaversMap = new HashMap<>();
//        colorRGBNumberSet = new HashSet<>();
//        linePoints = new HashMap<>();
//        linesForHideZoneParsing = new HashMap<>();

        this.getContentPane().setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(1150, 700));
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JPanel languageSelectPanel = new JPanel(new BorderLayout(5, 5));
        JLabel languageSelectLabel = new JLabel("Select Language");
        languageSelectLabel.setFont(new Font(null, Font.BOLD, 15));
        languageSelectLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPane = new JPanel(new FlowLayout());
        JButton englishLanguageButton = new JButton("ENGLISH");
        englishLanguageButton.setPreferredSize(new Dimension(100, 50));
        englishLanguageButton.addActionListener((e) -> {
            mainPanel.removeAll();
            Locale.setDefault(new Locale("en", "US"));
            startApplication();
        });

        JButton russianLanguageButton = new JButton("РУССКИЙ");
        russianLanguageButton.setPreferredSize(new Dimension(100, 50));
        russianLanguageButton.addActionListener((e) -> {
            mainPanel.removeAll();
            Locale.setDefault(new Locale("ru", "RU"));//ru_RU
            startApplication();
        });

        buttonPane.add(englishLanguageButton);
        buttonPane.add(russianLanguageButton);
        languageSelectPanel.add(languageSelectLabel, BorderLayout.NORTH);
        languageSelectPanel.add(buttonPane, BorderLayout.CENTER);

        mainPanel.add(languageSelectPanel);

        this.add(mainPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

//    public static void addLinePoint(int groupNumber, int[][] list, boolean edit) {
//        linePoints.put(groupNumber, list);
//        if (edit) {
//            addressSaver.saveLinePoints(groupNumber, list);
//        }
//
//        if (list != null) {
//            linesForHideZoneParsing.put(groupNumber, getLineForParsing(list));
//        }
//    }
//
//    private static List<int[]> getLineForParsing(int[][] linePoints) {
//
//        List<int[]> listToReturn = new LinkedList<>();
//
//        double[][] pointsToDrawLine;
//        double[] onePointToDrawLine = new double[2];
//
//        pointsToDrawLine = new double[4][];
//        for (int i = 1; i < 5; i++) {
//            pointsToDrawLine[i - 1] = new double[2];
//        }
//
//        for (int i = 0; i < linePoints.length; i++) {
//            if (linePoints.length > i + 3) {
//                for (int j = 0; j < 4; j++) {
//                    int pointNumber = i + j;
//                    pointsToDrawLine[j][0] = linePoints[pointNumber][0];
//                    pointsToDrawLine[j][1] = linePoints[pointNumber][1];
//                }
//
//                for (double t = 0; t < 1; t += 0.001) {
//                    BackgroundImagePanel.eval(onePointToDrawLine, pointsToDrawLine, t);
//                    listToReturn.add(new int[]{(int) onePointToDrawLine[0], (int) onePointToDrawLine[1]});
//                }
//            }
//        }
//        return listToReturn;
//    }


    /**
     * starting application after choosing language
     */
    private void startApplication() {
        mainLabel = new JLabel(Storage.getBundle().getString("mainpage"));
        northPanel = new JPanel(new FlowLayout());
        northPanel.setBackground(Color.LIGHT_GRAY);
        northPanel.setPreferredSize(new Dimension(1110, 54));

        centralPanel = new JPanel();
        centralPanel.setBackground(Color.LIGHT_GRAY);

        southPanel = new JPanel();
        southPanel.setBackground(Color.LIGHT_GRAY);
        southPanel.setPreferredSize(new Dimension(1110, 45));

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        cameraAddressSetting = CameraAddressSetting.getCameraAddressSetting();
        videoFilesPanel = VideoFilesPanel.getVideoFilesPanel();

        informLabel = new JLabel();
        informLabel.setHorizontalAlignment(SwingConstants.CENTER);
        informLabel.setPreferredSize(new Dimension(270, 30));
        informLabel.setHorizontalAlignment(SwingConstants.CENTER);

        opacityLabel = new JLabel(Storage.getBundle().getString("opacitycount") + "30%");
        opacityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opacityLabel.setPreferredSize(new Dimension(150, 30));

        showImagesLabel = new JLabel(Storage.getBundle().getString("showframescountlabel") + Storage.getShowFramesPercent());
        showImagesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        showImagesLabel.setPreferredSize(new Dimension(150, 30));

        countSecondsToSaveVideo = new JLabel(Storage.getBundle().getString("savesecondcount") + Storage.getSecondsToSave() + Storage.getBundle().getString("seconds"));
        countSecondsToSaveVideo.setHorizontalAlignment(SwingConstants.CENTER);
        countSecondsToSaveVideo.setPreferredSize(new Dimension(120, 30));

        photosensitivityLabel = new JLabel(Storage.getBundle().getString("photosensitivity") + Storage.getColorLightNumber());

        photosensitivityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photosensitivityLabel.setPreferredSize(new Dimension(180, 30));
        changeWhiteLabel = new JLabel(Storage.getBundle().getString("lightening") + Storage.getPercentDiffWhite() + "%");
        changeWhiteLabel.setHorizontalAlignment(SwingConstants.CENTER);

        eventServerPortLabel = new JLabel();
        eventServerPortLabel.setHorizontalAlignment(SwingConstants.CENTER);
        eventServerPortLabel.setPreferredSize(new Dimension(100, 30));

        buildMainWindow();
        Storage.getAddressSaver().setPasswordsToFields();
        Storage.getAddressSaver().setSetting();
        setting = Setting.getSetting();

        Storage.startAllCameras();

        Thread memoryUpdateThread = new Thread(() -> {
            int playInt = 0;
            int writeLogs = 0;
            boolean red = false;
            boolean startRec = true;

            while (true) {
                if (VideoCreator.isSaveVideoEnable()) {
                    if (startRec) {
                        startRec = false;
                    }
                    if (red) {
                        recordLabel.setForeground(Color.DARK_GRAY);
                        recordSecondsLabel.setForeground(Color.DARK_GRAY);
                        red = false;
                    } else {
                        recordLabel.setForeground(Color.RED);
                        recordSecondsLabel.setForeground(Color.RED);
                        red = true;
                    }
                } else {
                    if (red || !startRec) {
                        recordLabel.setForeground(new Color(46, 139, 87));
                        recordSecondsLabel.setForeground(new Color(46, 139, 87));
                        red = false;
                        startRec = true;
                    }
                }

                if (writeLogs > 60) {
                    Runtime.getRuntime().gc();
                    long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
                    log.info("Используем памяти " + usedMemory + " mb");
                    writeLogs = 0;
                } else {
                    writeLogs++;
                }

                if (VideoPlayer.isShowVideoPlayer()) {
                    try {
                        if (playInt == 0) {
                            VideoPlayer.informLabel.setForeground(Color.RED);
                            VideoPlayer.informLabel.repaint();
                            playInt++;
                        } else {
                            playInt = 0;
                            VideoPlayer.informLabel.setForeground(Color.LIGHT_GRAY);
                            VideoPlayer.informLabel.repaint();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        memoryUpdateThread.setName("Memory Update App Thread");
        memoryUpdateThread.start();

        /*
      Thread set the socket server to wait request from sensor
     */
        Thread alarmThread = new Thread(() -> {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(Storage.getPort());
            } catch (IOException ignored) {
            }

            while (true) {
                try {
                    audioPacketCount.setForeground(new Color(29, 142, 27));
                    setAlarmServerLabelColor(new Color(29, 142, 27));
                    log.info("Ждем сигнал сработки на порт - " + Storage.getPort());
                    Socket socket = ss.accept();
                    log.info("Получили сигнал сработки на порт " + Storage.getPort());
                    VideoCreator.startCatchVideo(false);
                    socket.close();
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    setAlarmServerLabelColor(Color.red);
                    audioPacketCount.setForeground(Color.red);
                }
            }
        });
        alarmThread.setName("Alarm Thread");
        alarmThread.start();


        File fileAddressSaver = new File(Storage.getDefaultPath() + "\\data\\");
        fileAddressSaver.mkdirs();

        File fileBuffBytes = new File(Storage.getPath() + "\\bytes\\");
        fileBuffBytes.mkdirs();

        for (int i = 1; i < 5; i++) {
            File folder = new File(Storage.getDefaultPath() + "\\buff\\" + i + "\\");
            folder.mkdirs();
            File[] files = folder.listFiles();
            for (int j = 0; j < files.length; j++) {
                files[j].delete();
            }
        }

        File pathFolder = new File(Storage.getPath());
        pathFolder.mkdirs();
    }

    private void buildMainWindow() {
        buildNorthPanel();
        buildCentralPanel();
        buildSouthPanel();
    }

    private void buildNorthPanel() {
        audioPacketCount = new JLabel("AUDIO");
        JButton mainWindowButton = new JButton(Storage.getBundle().getString("mainpage"));
        mainWindowButton.setPreferredSize(new Dimension(120, 30));
        mainWindowButton.addActionListener((e) -> {
            VideoPlayer.setShowVideoPlayer(false);
            if (fullSize) {
                for (Integer cameraNumber : cameraPanelsMap.keySet()) {
                    CameraPanel cameraPanel = cameraPanelsMap.get(cameraNumber);
                    if (cameraPanel.isFullSizeEnable()) {
                        cameraPanel.setFullSizeEnable(false);
                        int blockNumber = (cameraNumber + 1) / 2;
                        JPanel blockPanel = cameraBlock.get(blockNumber);
                        if (cameraNumber % 2 == 0) {
                            blockPanel.remove(cameraPanel);
                            blockPanel.add(cameraPanel);
                            blockPanel.repaint();
                        } else {
                            Component firstCamera = blockPanel.getComponent(0);
                            blockPanel.removeAll();
                            blockPanel.add(cameraPanel);
                            blockPanel.add(firstCamera);
                            blockPanel.validate();
                            blockPanel.repaint();
                        }
                    }
                }
                fullSize = false;
            }
            setCentralPanel(allCameraPanel);
            mainLabel.setText(Storage.getBundle().getString("mainpage"));
        });

        JButton cameraButton = new JButton(Storage.getBundle().getString("camerassettingpage"));
        cameraButton.setPreferredSize(new Dimension(120, 30));
        cameraButton.addActionListener((e) -> {
            VideoPlayer.setShowVideoPlayer(false);
            cameraAddressSetting.setSaveButtonDefaultColor();
            setCentralPanel(cameraAddressSetting);
            mainLabel.setText(Storage.getBundle().getString("camerassettingpage"));
        });

        JButton videoButton = new JButton(Storage.getBundle().getString("videospage"));
        videoButton.setPreferredSize(new Dimension(120, 30));
        videoButton.addActionListener((e) -> {
            MainFrame.showSecondsAlreadySaved("");
            VideoCreator.setShowInformMessage(false);
            showVideoFilesPanel();
        });

        JButton settingButton = new JButton(Storage.getBundle().getString("settingpage"));
        settingButton.setPreferredSize(new Dimension(120, 30));
        settingButton.addActionListener((e) -> {
            VideoPlayer.setShowVideoPlayer(false);
            setting.saveButton.setForeground(Color.BLACK);
            setting.saveButton.setText(Storage.getBundle().getString("savebutton"));
            setting.reSetPassword();
            setCentralPanel(setting);
            mainLabel.setText(Storage.getBundle().getString("settingpage"));
        });

        mainLabel.setFont(new Font(null, Font.BOLD, 15));
        mainLabel.setPreferredSize(new Dimension(120, 30));

        JPanel informPane = new JPanel(new FlowLayout());
        informPane.setBackground(Color.LIGHT_GRAY);
        recordLabel = new JLabel(String.valueOf((char) 8623));
        recordLabel.setFont(new Font(null, Font.BOLD, 23));
        recordLabel.setForeground(Color.DARK_GRAY);
        informPane.add(mainLabel);
        informPane.add(recordLabel);
        recordSecondsLabel = new JLabel();
        recordSecondsLabel.setFont(new Font(null, Font.BOLD, 19));
        recordSecondsLabel.setPreferredSize(new Dimension(150, 30));
        recordSecondsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        startEventButton = new JButton("REC");
        startEventButton.setVisible(false);
        startEventButton.addActionListener((e -> {
            VideoCreator.startCatchVideo(false);
        }));

        startButtonProgrammingCatch = new JButton("REC PR");
        startButtonProgrammingCatch.setVisible(false);
        startButtonProgrammingCatch.addActionListener((e -> {
            VideoCreator.startCatchVideo(true);
        }));

        northPanel.add(audioPacketCount);
        northPanel.add(Box.createRigidArea(new Dimension(15, 10)));
        northPanel.add(mainWindowButton);
        northPanel.add(cameraButton);
        northPanel.add(videoButton);
        northPanel.add(settingButton);

        testModeLabel.setPreferredSize(new Dimension(165, 25));
        northPanel.add(testModeLabel);
        northPanel.add(startEventButton);
        northPanel.add(startButtonProgrammingCatch);

        northPanel.add(recordSecondsLabel);
        northPanel.add(informPane);
    }

    private void buildCentralPanel() {
        centralPanel.setLayout(new BorderLayout());
        GridLayout gridLayout = new GridLayout(2, 2, 2, 2);
        allCameraPanel = new JPanel();
        allCameraPanel.setLayout(gridLayout);

        CameraGroup[] cameraGroups = Storage.getCameraGroups();
        for (int groupNumber = 0; groupNumber < 4; groupNumber++) {
            JPanel blockPanel;
            blockPanel = new JPanel(new GridLayout(1, 2));
            blockPanel.setBorder(BorderFactory.createEtchedBorder());

            for (int cameraNumber = 0; cameraNumber < 2; cameraNumber++) {
                Camera camera = cameraGroups[groupNumber].getCameras()[cameraNumber];
                CameraPanel cameraPanel = new CameraPanel(camera);
                cameraPanelsMap.put(camera.getNumber(), cameraPanel);
                cameraPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            if (fullSize) {
                                for (Integer cameraNumber : cameraPanelsMap.keySet()) {
                                    CameraPanel cameraPanel = cameraPanelsMap.get(cameraNumber);
                                    if (cameraPanel.isFullSizeEnable()) {
                                        cameraPanel.setFullSizeEnable(false);
                                        int blockNumber = (cameraNumber + 1) / 2;
                                        JPanel blockPanel = cameraBlock.get(blockNumber);
                                        if (cameraNumber % 2 == 0) {
                                            blockPanel.remove(cameraPanel);
                                            blockPanel.add(cameraPanel);
                                        } else {
                                            Component firstCamera = blockPanel.getComponent(0);
                                            blockPanel.removeAll();
                                            blockPanel.add(cameraPanel);
                                            blockPanel.add(firstCamera);
                                            blockPanel.validate();
                                        }
                                    }
                                }
                                setCentralPanel(allCameraPanel);
                                fullSize = false;
                                mainLabel.setText(Storage.getBundle().getString("mainpage"));
                            } else {
                                cameraPanel.setFullSizeEnable(true);
                                setCentralPanel(cameraPanel);
                                fullSize = true;
                            }
                        }
                    }
                });
                blockPanel.add(cameraPanel);
            }

            cameraBlock.put(groupNumber + 1, blockPanel);
            allCameraPanel.add(blockPanel);
        }
        centralPanel.add(allCameraPanel);
    }

    private void buildSouthPanel() {
        southPanel.setLayout(new FlowLayout());
        southPanel.add(eventServerPortLabel);
        southPanel.add(countSecondsToSaveVideo);
        southPanel.add(opacityLabel);
        southPanel.add(showImagesLabel);
        southPanel.add(photosensitivityLabel);
        southPanel.add(changeWhiteLabel);
        southPanel.add(informLabel);
    }

    public static void setCentralPanel(JPanel panel) {
        centralPanel.removeAll();
        centralPanel.add(panel);
        centralPanel.validate();
        centralPanel.repaint();
    }

    public static void showVideoFilesPanel() {
        VideoPlayer.setShowVideoPlayer(false);
        videoFilesPanel.showVideos();
        setCentralPanel(videoFilesPanel);
        mainLabel.setText(Storage.getBundle().getString("videospage"));
    }

    public static void showInformMassage(String massage, Color color) {
        informLabel.setText(massage);
        informLabel.setForeground(color);
        informLabel.repaint();
    }

    public static void showSecondsAlreadySaved(String massage) {
        recordSecondsLabel.setText(massage);
        recordSecondsLabel.repaint();
    }

    /**
     * start/restart to read data from camera stream
     * used each time when will be changed ip address or cameraPanelsMap, and when program start
     */

    public static MainFrame getMainFrame() {
        if (mainFrame != null) {
            return mainFrame;
        } else {
            mainFrame = new MainFrame();
            return mainFrame;
        }
    }

    public static void setOpacitySettingToFrame(int opacity) {
        opacityLabel.setText(Storage.getBundle().getString("opacitycount") + opacity + "%");
        opacityLabel.repaint();
    }

    public static void setSecondsToSaveToFrame(int countSave) {
        countSecondsToSaveVideo.setText(Storage.getBundle().getString("savesecondcount") + countSave + Storage.getBundle().getString("seconds"));
        countSecondsToSaveVideo.repaint();
    }

    public static void setPercentDiffWhiteToFrame(int percentDiffWhite) {
        changeWhiteLabel.setText((Storage.getBundle().getString("lightening") + percentDiffWhite + "%"));
        changeWhiteLabel.repaint();
    }

    private void setAlarmServerLabelColor(Color color) {
        eventServerPortLabel.setForeground(color);
        eventServerPortLabel.repaint();
    }

    public static void setColorLightNumberToFrame(int colorLightNumber) {
        photosensitivityLabel.setText(Storage.getBundle().getString("photosensitivity") + colorLightNumber);
        photosensitivityLabel.repaint();
    }

    public static void setProgramLightCatchEnableToFrame(boolean programLightCatchEnable) {
        if (programLightCatchEnable) {
            photosensitivityLabel.setForeground(new Color(32, 175, 33));
            changeWhiteLabel.setForeground(new Color(32, 175, 33));
        } else {
            photosensitivityLabel.setForeground(Color.LIGHT_GRAY);
            changeWhiteLabel.setForeground(Color.LIGHT_GRAY);
        }

        photosensitivityLabel.repaint();
        changeWhiteLabel.repaint();
    }

    public static void setPortToFrame(int port) {
        eventServerPortLabel.setText(Storage.getBundle().getString("portstring") + port);
    }

    public static void setTestMode(boolean testMode) {
        testModeLabel.setVisible(!testMode);
        startButtonProgrammingCatch.setPreferredSize(new Dimension(80, 25));
        startEventButton.setPreferredSize(new Dimension(80, 25));
        startEventButton.setVisible(testMode);
        startButtonProgrammingCatch.setVisible(testMode);
    }

    public static void setShowFramesPercentToFrame(int showFramesPercent) {
        showImagesLabel.setText(Storage.getBundle().getString("showframescountlabel") + showFramesPercent);
        showImagesLabel.repaint();
    }
}










