package ui.setting;

import entity.HideZoneLightingSearcher;
import entity.Storage.Storage;
import org.apache.log4j.Logger;
import ui.camera.CameraPanel;
import ui.main.MainFrame;
import ui.video.HideZoneMainPanel;
import ui.video.HideZonePanel;
import ui.video.VideoFilesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * setting panel
 */
public class Setting extends JPanel {
    private static Logger log = Logger.getLogger("file");
    private static Setting setting;
    private JLabel portLabel;
    private JTextField defaultPort;
    private JTextField defaultFolder;

    private JSlider hideZoneIdentificationAccuracyComparePixelsSlider;

    public JButton saveButton;
    private JTextField timeTextField;
    private JCheckBox programCatchEnableCheckBox;

    private JPanel allSettingPane;
    private JPanel passwordPane;
    private JLabel wrongPasswordLabel;
    private JTextField passwordTextField;

    private SarcophagusSettingPanel sarcophagusSettingPanel;
    private JButton testButton;
    private JButton createTestImageButton;
    private Component testButtonRigidArea;

    private JLabel testLabel;
    private HideZonePanel hideZoneTestPanel;

    private Setting() {
        this.setPreferredSize(new Dimension(1120, 540));
        this.setLayout(new BorderLayout());
        sarcophagusSettingPanel = new SarcophagusSettingPanel(Storage.getAddressSaver().getCamerasPosition());
        buildSetting();
    }

    public static Setting getSetting() {
        if (setting != null) {
            return setting;
        } else {
            setting = new Setting();
            return setting;
        }
    }

    private void buildSetting() {
        JPanel mainSettingPanel = new JPanel(new FlowLayout());
        mainSettingPanel.setBorder(BorderFactory.createEtchedBorder());
        mainSettingPanel.setPreferredSize(new Dimension(700, 530));

        JPanel firstPanel = new JPanel(new GridLayout(2, 1));
        firstPanel.setBorder(BorderFactory.createEtchedBorder());
        firstPanel.setPreferredSize(new Dimension(690, 80));

        JPanel checkBoxPane = new JPanel(new FlowLayout());
        JCheckBox testModeCheckBox = new JCheckBox(Storage.getBundle().getString("testmode"));
        testModeCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        testModeCheckBox.setSelected(false);
        programCatchEnableCheckBox = new JCheckBox(Storage.getBundle().getString("programcatchcheckboxlabel"));
        programCatchEnableCheckBox.setSelected(Storage.isProgramLightCatchEnable());
        checkBoxPane.add(programCatchEnableCheckBox);
        checkBoxPane.add(testModeCheckBox);

        JPanel timePanel = new JPanel(new FlowLayout());
        timeTextField = new JTextField();
        timeTextField.setText(String.valueOf(Storage.getSecondsToSave()));
        timeTextField.setHorizontalAlignment(SwingConstants.CENTER);
        timeTextField.setPreferredSize(new Dimension(50, 25));
        JLabel textLabel = new JLabel(Storage.getBundle().getString("timetosavevideolabel"));

        portLabel = new JLabel("port - " + Storage.getPort());
        defaultPort = new JTextField();
        defaultPort.setHorizontalAlignment(SwingConstants.CENTER);
        defaultPort.setText(String.valueOf(Storage.getPort()));
        defaultPort.setPreferredSize(new Dimension(50, 25));

        timePanel.add(textLabel);
        timePanel.add(timeTextField);
        timePanel.add(Box.createRigidArea(new Dimension(25, 25)));
        timePanel.add(portLabel);
        timePanel.add(defaultPort);

        firstPanel.add(checkBoxPane);
        firstPanel.add(timePanel);

        JPanel programLightCatchSettingPanel = new JPanel(new FlowLayout());
        programLightCatchSettingPanel.setBorder(BorderFactory.createEtchedBorder());
        programLightCatchSettingPanel.setPreferredSize(new Dimension(690, 215));

        JLabel headLabel = new JLabel(Storage.getBundle().getString("programcatchsettinglabel"));
        headLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headLabel.setFont(new Font(null, Font.BOLD, 17));
        headLabel.setPreferredSize(new Dimension(680, 25));

        JLabel lightSensitivityLabel = new JLabel(Storage.getBundle().getString("photosensitivitysettinglabel") + Storage.getColorLightNumber());
        lightSensitivityLabel.setPreferredSize(new Dimension(680, 25));

        JPanel whitePanel = new JPanel(new FlowLayout());
        whitePanel.setPreferredSize(new Dimension(680, 38));
        whitePanel.setBackground(Color.darkGray);
        whitePanel.setBorder(BorderFactory.createEtchedBorder());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 37; i++) {
            stringBuilder.append(String.valueOf((char) 8623));
        }

        JLabel whiteColorLabel = new JLabel(stringBuilder.toString());
        whiteColorLabel.setPreferredSize(new Dimension(680, 25));
        whiteColorLabel.setFont(new Font(null, Font.ITALIC, 25));
        whiteColorLabel.setForeground(new Color(Storage.getColorLightNumber(), Storage.getColorLightNumber(), Storage.getColorLightNumber()));
        whitePanel.add(whiteColorLabel);

        JSlider lightSensitivitySlider = new JSlider();
        lightSensitivitySlider.setPreferredSize(new Dimension(680, 28));
        lightSensitivitySlider.setMinorTickSpacing(1);
        lightSensitivitySlider.setPaintTicks(true);
        lightSensitivitySlider.setMinimum(190);
        lightSensitivitySlider.setMaximum(230);
        lightSensitivitySlider.setValue(Storage.getColorLightNumber());
        lightSensitivitySlider.addChangeListener(e -> {
            int value = lightSensitivitySlider.getValue();
            whiteColorLabel.setForeground(new Color(value, value, value));
            lightSensitivityLabel.setText(Storage.getBundle().getString("photosensitivitysettinglabel") + value);
        });

        JLabel changeWhiteLabel = new JLabel(Storage.getBundle().getString("lightening") + Storage.getPercentDiffWhite() + " %");
        changeWhiteLabel.setPreferredSize(new Dimension(680, 25));

        JSlider sliderChangeWhite = new JSlider();
        sliderChangeWhite.setPreferredSize(new Dimension(680, 30));
        sliderChangeWhite.setMinorTickSpacing(1);
        sliderChangeWhite.setPaintTicks(true);
        sliderChangeWhite.setMinimum(2);
        sliderChangeWhite.setMaximum(90);
        sliderChangeWhite.setValue(Storage.getPercentDiffWhite());
        sliderChangeWhite.addChangeListener(e -> {
            changeWhiteLabel.setText(Storage.getBundle().getString("lightening") + sliderChangeWhite.getValue() + " %");
        });

        programLightCatchSettingPanel.add(headLabel);
        programLightCatchSettingPanel.add(lightSensitivityLabel);
        programLightCatchSettingPanel.add(whitePanel);
        programLightCatchSettingPanel.add(lightSensitivitySlider);
        programLightCatchSettingPanel.add(changeWhiteLabel);
        programLightCatchSettingPanel.add(sliderChangeWhite);

        JPanel otherSetting = new JPanel(new FlowLayout());
        otherSetting.setPreferredSize(new Dimension(690, 68));
        otherSetting.setBorder(BorderFactory.createEtchedBorder());

        JLabel opacityLabel = new JLabel(Storage.getBundle().getString("backimageopacitylabel") + (int) (Storage.getOpacitySetting() * 100) + " %");
        opacityLabel.setPreferredSize(new Dimension(680, 25));
        JSlider slider = new JSlider();
        slider.setPreferredSize(new Dimension(680, 28));
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setValue((int) (Storage.getOpacitySetting() * 100));
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            opacityLabel.setText(Storage.getBundle().getString("backimageopacitylabel") + value + " %");
        });

        otherSetting.add(opacityLabel);
        otherSetting.add(slider);

        JPanel countImageToShowPanel = new JPanel(new FlowLayout());
        countImageToShowPanel.setPreferredSize(new Dimension(690, 68));
        countImageToShowPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel countShowLabel = new JLabel(Storage.getBundle().getString("showframescountlabel") + Storage.getShowFramesPercent());
        countShowLabel.setPreferredSize(new Dimension(680, 25));
        JSlider countShowSlider = new JSlider();
        countShowSlider.setPreferredSize(new Dimension(680, 28));
        countShowSlider.setMinimum(1);
        countShowSlider.setMaximum(60);
        countShowSlider.setMinorTickSpacing(2);
        countShowSlider.setPaintTicks(true);
        countShowSlider.setValue(Storage.getShowFramesPercent());
        countShowSlider.addChangeListener(e -> {
            int value = countShowSlider.getValue();
            countShowLabel.setText(Storage.getBundle().getString("showframescountlabel") + value);
        });

        countImageToShowPanel.add(countShowLabel);
        countImageToShowPanel.add(countShowSlider);

        JPanel lastPanel = new JPanel(new FlowLayout());
        lastPanel.setPreferredSize(new Dimension(690, 70));
        lastPanel.setBorder(BorderFactory.createEtchedBorder());


        JPanel pathPanel = new JPanel(new FlowLayout());
        pathPanel.setPreferredSize(new Dimension(375, 60));
        pathPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel addressSaverLabel = new JLabel(Storage.getBundle().getString("foldertosavevideo"));
        addressSaverLabel.setFont(new Font(null, Font.BOLD, 15));
        addressSaverLabel.setPreferredSize(new Dimension(370, 20));
        addressSaverLabel.setHorizontalAlignment(SwingConstants.CENTER);

        defaultFolder = new JTextField(Storage.getPath());//"C:\\ipCamera\\"
        defaultFolder.setPreferredSize(new Dimension(365, 25));

        pathPanel.add(addressSaverLabel);
        pathPanel.add(defaultFolder);

        saveButton = new JButton();
        saveButton.setPreferredSize(new Dimension(150, 60));
        saveButton.setFont(new Font(null, Font.BOLD, 20));
        saveButton.addActionListener((e) -> {
            try {
                MainFrame.setTestMode(testModeCheckBox.isSelected());
                testButton.setVisible(testModeCheckBox.isSelected());
                createTestImageButton.setVisible(testModeCheckBox.isSelected());
                testButtonRigidArea.setVisible(!testModeCheckBox.isSelected());
                VideoFilesPanel.setTestMode(testModeCheckBox.isSelected());

//                if (testModeCheckBox.isSelected()) {
//                    MainFrame.setTestMode(true);
//                    testButton.setVisible(true);
//                    createTestImageButton.setVisible(true);
//                    testButtonRigidArea.setVisible(false);
//                } else {
//                    MainFrame.setTestMode(false);
//                    testButton.setVisible(false);
//                    createTestImageButton.setVisible(false);
//                    testButtonRigidArea.setVisible(true);
//                }
                int countShowFrames = countShowSlider.getValue();
                Storage.setShowFramesPercent(countShowFrames);

                Storage.setProgramLightCatchEnable(programCatchEnableCheckBox.isSelected());
                int changeWhitePercent = sliderChangeWhite.getValue();
                Storage.setPercentDiffWhite(changeWhitePercent);

                int lightSensitivity = lightSensitivitySlider.getValue();
                Storage.setColorLightNumber(lightSensitivity);

                String text = timeTextField.getText();
                int countSecondsToSaveVideo = Integer.parseInt(text);

                if (countSecondsToSaveVideo < 2) {
                    countSecondsToSaveVideo = 2;
                    timeTextField.setText(String.valueOf(countSecondsToSaveVideo));
                }

                Storage.setSecondsToSave(countSecondsToSaveVideo);

                int opacity = slider.getValue();
                Storage.setOpacitySetting(opacity);

                int port = 9999;
                try {
                    port = Integer.valueOf(defaultPort.getText());
                } catch (Exception ignored) {
                }

                Storage.setPort(port);
                portLabel.setText("port сервера - " + port);

                String path = defaultFolder.getText();
                boolean mkdirs = false;
                if (path != null && path.length() > 2) {
                    File file = new File(path + "\\bytes\\");
                    try {
                        mkdirs = file.mkdirs();
                    } catch (Exception ignored) {
                    }
                }

                Storage.getAddressSaver().saveSetting(countSecondsToSaveVideo, programCatchEnableCheckBox.isSelected(),
                        changeWhitePercent, lightSensitivity, opacity, port, path,
                        hideZoneIdentificationAccuracyComparePixelsSlider.getValue(), countShowFrames);
                log.info("Настройки изменены. Время сохранения: " + countSecondsToSaveVideo +
                        ", Фиксируем програмные сработки: " + programCatchEnableCheckBox.isSelected() +
                        ", процент вспышки на изображении: " + changeWhitePercent +
                        ", чуствительность камеры: " + lightSensitivity +
                        ", прозрачность фона: " + opacity +
                        ", порт для ожидания сиграла аппаратной сработки: " + port +
                        ", путь к папке для сохранения данных: " + path +
                        ", тестовый режим: " + testModeCheckBox.isSelected() + ".");

                saveButton.setText(Storage.getBundle().getString("savedbutton"));
                MainFrame.showInformMassage(Storage.getBundle().getString("savedbutton"), new Color(46, 139, 87));
                saveButton.setForeground(new Color(46, 139, 87));
            } catch (Exception exc) {
                exc.printStackTrace();
                log.error(exc.getLocalizedMessage());
            }
        });

        lastPanel.add(pathPanel);
        lastPanel.add(Box.createRigidArea(new Dimension(70, 60)));
        lastPanel.add(saveButton);
        lastPanel.add(Box.createRigidArea(new Dimension(70, 60)));

        mainSettingPanel.add(firstPanel);
        mainSettingPanel.add(programLightCatchSettingPanel);
        mainSettingPanel.add(otherSetting);
        mainSettingPanel.add(countImageToShowPanel);
        mainSettingPanel.add(lastPanel);

        JPanel hideZoneSettingPanel = new JPanel(new FlowLayout());
        hideZoneSettingPanel.setBorder(BorderFactory.createEtchedBorder());
        hideZoneSettingPanel.setPreferredSize(new Dimension(380, 530));

        buildHideZoneSetting(hideZoneSettingPanel);

        allSettingPane = new JPanel(new FlowLayout());
        allSettingPane.add(mainSettingPanel);
        allSettingPane.add(hideZoneSettingPanel);

        passwordPane = new JPanel(new FlowLayout());
        passwordPane.setPreferredSize(new Dimension(1000, 500));
        passwordPane.setBackground(Color.LIGHT_GRAY);
        JLabel passwordLabel = new JLabel(Storage.getBundle().getString("editpasswordlabel"));

        wrongPasswordLabel = new JLabel(Storage.getBundle().getString("wrongpasswordlabel"));
        wrongPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wrongPasswordLabel.setPreferredSize(new Dimension(900, 30));
        wrongPasswordLabel.setVisible(false);

        passwordTextField = new JTextField();
        passwordTextField.setPreferredSize(new Dimension(150, 30));
        passwordTextField.addKeyListener((new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String passwordString = passwordTextField.getText();
                    if (passwordString.length() > 1 && passwordString.compareTo(Storage.getPassword()) == 0) {
                        passwordPane.setVisible(false);
                        allSettingPane.setVisible(true);
                    } else {
                        wrongPasswordLabel.setVisible(true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        }));
        JButton passwordButton = new JButton(Storage.getBundle().getString("editpasswordbutton"));
        passwordButton.setPreferredSize(new Dimension(150, 30));
        passwordButton.addActionListener((e) -> {
            String passwordString = passwordTextField.getText();
            if (passwordString.length() > 1 && passwordString.compareTo(Storage.getPassword()) == 0) {
                passwordPane.setVisible(false);
                allSettingPane.setVisible(true);
            } else {
                wrongPasswordLabel.setVisible(true);
            }
        });

        passwordPane.add(passwordLabel);
        passwordPane.add(Box.createRigidArea(new Dimension(10, 30)));
        passwordPane.add(passwordTextField);
        passwordPane.add(passwordButton);
        passwordPane.add(wrongPasswordLabel);
        allSettingPane.setVisible(false);

        this.setBackground(Color.LIGHT_GRAY);
        this.add(passwordPane, BorderLayout.NORTH);
        this.add(allSettingPane, BorderLayout.CENTER);
    }

    private void buildHideZoneSetting(JPanel hideZoneSettingPanel) {
        JPanel backgroundSettingPanel = new JPanel(new BorderLayout());
        backgroundSettingPanel.setPreferredSize(new Dimension(375, 340));
        backgroundSettingPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel backgroundSettingLabel = new JLabel(Storage.getBundle().getString("backgroundSettingLabel"));
        backgroundSettingLabel.setFont(new Font(null, Font.BOLD, 17));
        backgroundSettingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundSettingLabel.setAlignmentX(CENTER_ALIGNMENT);

        BackgroundPreViewPanel backgroundPreViewPanel = new BackgroundPreViewPanel(null);
        backgroundPreViewPanel.setPreferredSize(new Dimension(300, 300));
        backgroundPreViewPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel southBackgroundSettingPanel = new JPanel(new FlowLayout());
        southBackgroundSettingPanel.setBorder(BorderFactory.createEtchedBorder());


        JComboBox<String> backgroundSettingComboBox = new JComboBox<>();
        for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
            backgroundSettingComboBox.addItem(Storage.getBundle().getString("cameragroupsetting") + groupNumber);
        }

        backgroundSettingComboBox.addActionListener((event) -> {
            int selectedIndex = backgroundSettingComboBox.getSelectedIndex();
            File imageFile = new File(Storage.getDefaultPath() + "\\buff\\" + (selectedIndex + 1) + ".jpg");
            BufferedImage bufferedImage = null;
            if (imageFile.exists()) {
                try {
                    bufferedImage = ImageIO.read(imageFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            backgroundPreViewPanel.setBufferedImage(bufferedImage);
            backgroundPreViewPanel.setGroupNumber(selectedIndex + 1);
            backgroundPreViewPanel.revalidate();
            backgroundPreViewPanel.repaint();

            if (testLabel != null || hideZoneTestPanel != null) {
                if (testLabel != null) {
                    backgroundSettingPanel.remove(testLabel);
                    testLabel = null;
                } else {
                    backgroundSettingPanel.remove(hideZoneTestPanel);
                    hideZoneTestPanel = null;
                }

                backgroundSettingPanel.add(backgroundPreViewPanel, BorderLayout.CENTER);
                setting.revalidate();
                setting.repaint();
            }
        });
        backgroundSettingComboBox.setSelectedIndex(0);
        backgroundSettingComboBox.setPreferredSize(new Dimension(257, 25));

        JButton editBackGroundButton = new JButton(Storage.getBundle().getString("editButton"));
        editBackGroundButton.addActionListener((r) -> {
            int selectedIndex = backgroundSettingComboBox.getSelectedIndex() + 1;
            File imageFile = new File(Storage.getDefaultPath() + "\\buff\\" + selectedIndex + ".jpg");
            BufferedImage bufferedImage = null;
            if (imageFile.exists()) {
                try {
                    bufferedImage = ImageIO.read(imageFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (bufferedImage != null) {
                HideZoneLineMarkerPanel hideZoneLineMarkerPanel = new HideZoneLineMarkerPanel(bufferedImage, selectedIndex);
                this.removeAll();
                this.add(hideZoneLineMarkerPanel);
            }

            this.revalidate();
            this.repaint();
        });

        southBackgroundSettingPanel.add(backgroundSettingComboBox);
        southBackgroundSettingPanel.add(editBackGroundButton);

        backgroundSettingPanel.add(backgroundSettingLabel, BorderLayout.NORTH);
        backgroundSettingPanel.add(backgroundPreViewPanel, BorderLayout.CENTER);
        backgroundSettingPanel.add(southBackgroundSettingPanel, BorderLayout.SOUTH);
        hideZoneSettingPanel.add(backgroundSettingPanel);

        JPanel cameraPositionSetting = new JPanel();
        cameraPositionSetting.setPreferredSize(new Dimension(375, 40));
        cameraPositionSetting.setBorder(BorderFactory.createEtchedBorder());

        JLabel setCameraPositionLabel = new JLabel(Storage.getBundle().getString("setCameraPositionLabel"));
        setCameraPositionLabel.setFont(new Font(null, Font.BOLD, 13));
        setCameraPositionLabel.setPreferredSize(new Dimension(142, 25));//257 = 70

        JButton setCameraPositionButton = new JButton(Storage.getBundle().getString("editButton"));
        setCameraPositionButton.setPreferredSize(new Dimension(94, 25));
        setCameraPositionButton.addActionListener((as) -> {
            this.removeAll();
            this.add(sarcophagusSettingPanel);
            this.revalidate();
            this.repaint();
        });

        testButton = new JButton("T2");
        testButton.setPreferredSize(new Dimension(55, 25));
        testButton.addActionListener((df) -> {
            String zoneNameTest = HideZoneLightingSearcher.getZoneNameTest();
            backgroundSettingPanel.remove(backgroundPreViewPanel);
            if (hideZoneTestPanel != null) {
                backgroundSettingPanel.remove(hideZoneTestPanel);
            }

            if (testLabel != null) {
                backgroundSettingPanel.remove(testLabel);
            }
            if (zoneNameTest != null) {
                if (zoneNameTest.length() < 5) {
                    hideZoneTestPanel = new HideZonePanel(zoneNameTest, false);
                    backgroundSettingPanel.add(hideZoneTestPanel,
                            BorderLayout.CENTER);
                } else {
                    testLabel = new JLabel(zoneNameTest);
                    testLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    testLabel.setAlignmentX(CENTER_ALIGNMENT);
                    backgroundSettingPanel.add(testLabel,
                            BorderLayout.CENTER);
                }
            } else {
                testLabel = new JLabel("No Image");
                testLabel.setHorizontalAlignment(SwingConstants.CENTER);
                testLabel.setAlignmentX(CENTER_ALIGNMENT);
                backgroundSettingPanel.add(testLabel,
                        BorderLayout.CENTER);
            }
            backgroundPreViewPanel.revalidate();
            backgroundPreViewPanel.repaint();
            setting.revalidate();
            setting.repaint();
        });
        testButtonRigidArea = Box.createRigidArea(new Dimension(115, 25));
        testButton.setVisible(false);

        createTestImageButton = new JButton("T1");
        createTestImageButton.setPreferredSize(new Dimension(55, 25));
        createTestImageButton.setVisible(false);
        createTestImageButton.addActionListener((asdf) -> {
            MainFrame.setCentralPanel(new HideZoneMainPanel(true, "", null));
        });

        cameraPositionSetting.add(setCameraPositionLabel);
        cameraPositionSetting.add(createTestImageButton);
        cameraPositionSetting.add(testButton);
        cameraPositionSetting.add(testButtonRigidArea);
        cameraPositionSetting.add(setCameraPositionButton);
        hideZoneSettingPanel.add(cameraPositionSetting);

        JPanel hideZoneAccuracyPanel = new JPanel(new FlowLayout());
        hideZoneAccuracyPanel.setBorder(BorderFactory.createEtchedBorder());
        hideZoneAccuracyPanel.setPreferredSize(new Dimension(375, 130));

//        JLabel hideZoneIdentificationAccuracyLabel = new JLabel(Storage.getBundle().getString("hideZoneIdentificationAccuracyLabel") +
//                " " + Storage.getAddressSaver().getHideZoneIdentificationAccuracy());
//        hideZoneIdentificationAccuracyLabel.setPreferredSize(new Dimension(370, 25));
//
//        hideZoneIdentificationAccuracySlider = new JSlider();
//        hideZoneIdentificationAccuracySlider.setPreferredSize(new Dimension(370, 28));
//        hideZoneIdentificationAccuracySlider.setMinorTickSpacing(1);
//        hideZoneIdentificationAccuracySlider.setPaintTicks(true);
//        hideZoneIdentificationAccuracySlider.setMinimum(5);
//        hideZoneIdentificationAccuracySlider.setMaximum(10);
//        hideZoneIdentificationAccuracySlider.setValue(Storage.getAddressSaver().getHideZoneIdentificationAccuracy());
//        hideZoneIdentificationAccuracySlider.addChangeListener((g) -> {
//            hideZoneIdentificationAccuracyLabel.setText(Storage.getBundle().getString("hideZoneIdentificationAccuracyLabel") +
//                    " " + hideZoneIdentificationAccuracySlider.getValue());
//        });

        JLabel hideZoneIdentificationAccuracyComparePixelsLabel = new JLabel(Storage.getBundle().getString("hideZoneIdentificationAccuracyComparePixelsLabel") +
                " " + Storage.getAddressSaver().getHideZoneIdentificationAccuracyComparePixels());
        hideZoneIdentificationAccuracyComparePixelsLabel.setPreferredSize(new Dimension(370, 25));

        hideZoneIdentificationAccuracyComparePixelsSlider = new JSlider();
        hideZoneIdentificationAccuracyComparePixelsSlider.setPreferredSize(new Dimension(370, 28));
        hideZoneIdentificationAccuracyComparePixelsSlider.setMinorTickSpacing(1);
        hideZoneIdentificationAccuracyComparePixelsSlider.setPaintTicks(true);
        hideZoneIdentificationAccuracyComparePixelsSlider.setMinimum(1);
        hideZoneIdentificationAccuracyComparePixelsSlider.setMaximum(6);
        hideZoneIdentificationAccuracyComparePixelsSlider.setValue(Storage.getAddressSaver().getHideZoneIdentificationAccuracyComparePixels());
        hideZoneIdentificationAccuracyComparePixelsSlider.addChangeListener((g) -> {
            hideZoneIdentificationAccuracyComparePixelsLabel.setText(Storage.getBundle().getString("hideZoneIdentificationAccuracyComparePixelsLabel") +
                    " " + hideZoneIdentificationAccuracyComparePixelsSlider.getValue());
        });

//        hideZoneAccuracyPanel.add(hideZoneIdentificationAccuracyLabel);
//        hideZoneAccuracyPanel.add(hideZoneIdentificationAccuracySlider);
        hideZoneAccuracyPanel.add(hideZoneIdentificationAccuracyComparePixelsLabel);
        hideZoneAccuracyPanel.add(hideZoneIdentificationAccuracyComparePixelsSlider);
        hideZoneSettingPanel.add(hideZoneAccuracyPanel);
    }

    /**
     * set password panel every time to trying open setting
     */
    public void reSetPassword() {
        this.removeAll();
        this.add(passwordPane, BorderLayout.NORTH);
        this.add(allSettingPane, BorderLayout.CENTER);
        passwordTextField.setText("");
        allSettingPane.setVisible(false);
        passwordPane.setVisible(true);
        wrongPasswordLabel.setVisible(false);
    }

    private class BackgroundPreViewPanel extends JPanel {
        BufferedImage bufferedImage;
        int groupNumber;
        double[][] pointsToDrawLine;
        double[] onePointToDrawLine;

        BackgroundPreViewPanel(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
            onePointToDrawLine = new double[2];
            pointsToDrawLine = new double[4][];
            for (int i = 1; i < 5; i++) {
                pointsToDrawLine[i - 1] = new double[2];
            }

        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            if (bufferedImage != null) {
                BufferedImage smallBufferedImage = CameraPanel.processImage(bufferedImage, panelWidth, panelHeight);
                int imageWidth = smallBufferedImage.getWidth();
                int imageHeight = smallBufferedImage.getHeight();
                int x = (panelWidth - imageWidth) / 2;
                int y = (panelHeight - imageHeight) / 2;
                g.drawImage(smallBufferedImage, x, y, null);
                int[][] ints = Storage.getLinePoints().get(groupNumber);

                if (ints != null) {
                    g.setColor(Color.GREEN);
                    for (int i = 0; i < ints.length; i++) {
                        if (ints.length > i + 3) {
                            for (int j = 0; j < 4; j++) {
                                int pointNumber = i + j;
                                pointsToDrawLine[j][0] = (double) ints[pointNumber][0] / bufferedImage.getWidth() * imageWidth + x;
                                pointsToDrawLine[j][1] = (double) ints[pointNumber][1] / bufferedImage.getHeight() * imageHeight + y;
                            }

                            for (double t = 0; t < 1; t += 0.005) {
//                            for (double t = 0; t < 1; t += 0.01) {
                                BackgroundImagePanel.eval(onePointToDrawLine, pointsToDrawLine, t);
                                g.fillRect((int) onePointToDrawLine[0], (int) onePointToDrawLine[1], 2, 2);
                            }
                        }
                    }
                }

            } else {
                g.setFont(new Font(null, Font.BOLD, 35));
                g.drawString("\u2300", panelWidth / 2 - 10, panelHeight / 2);
            }
        }

        public void setGroupNumber(int groupNumber) {
            this.groupNumber = groupNumber;
        }

        void setBufferedImage(BufferedImage bufferedImage) {
            this.bufferedImage = bufferedImage;
        }
    }

    private class HideZoneLineMarkerPanel extends JPanel {
        private BufferedImage background;
        private int groupNumber;
        BackgroundImagePanel backgroundImagePanel;

        public HideZoneLineMarkerPanel(BufferedImage background, int groupNumber) {
            this.background = background;
            this.groupNumber = groupNumber;
            this.setLayout(new BorderLayout());
            buildPanel();
        }

        private void buildPanel() {
            backgroundImagePanel = new BackgroundImagePanel(background, groupNumber);
            backgroundImagePanel.setPreferredSize(backgroundImagePanel.getDimension());

            BackgroundSettingListener backgroundSettingListener = new BackgroundSettingListener(backgroundImagePanel, backgroundImagePanel.getSourcePoints());
            backgroundImagePanel.addMouseListener(backgroundSettingListener);

            JScrollPane scrollPane = new JScrollPane(backgroundImagePanel);
            scrollPane.setBorder(BorderFactory.createEtchedBorder());


            JPanel westPanel = new JPanel(new BorderLayout());
            westPanel.setBorder(BorderFactory.createEtchedBorder());

            JButton saveButton = new JButton("<html>&#128190</html>");
            saveButton.setFont(new Font(null, Font.BOLD, 30));
            saveButton.addActionListener((t) -> {
                backgroundImagePanel.setSavePoints(true);
                backgroundImagePanel.revalidate();
                backgroundImagePanel.repaint();
                saveButton.setForeground(new Color(52, 117, 38));
            });

            JButton backButton = new JButton("<html>&#11178</html>");
            backButton.setFont(new Font(null, Font.BOLD, 30));
            backButton.addActionListener((f) -> {
                Setting.getSetting().backToSetting();
                saveButton.setForeground(Color.DARK_GRAY);
            });


            westPanel.add(saveButton, BorderLayout.NORTH);
            westPanel.add(backButton, BorderLayout.SOUTH);

            this.add(westPanel, BorderLayout.WEST);
            this.add(scrollPane, BorderLayout.CENTER);
        }
    }

    void backToSetting() {
        Setting.getSetting().removeAll();
        Setting.getSetting().add(passwordPane, BorderLayout.NORTH);
        Setting.getSetting().add(allSettingPane, BorderLayout.CENTER);
        passwordTextField.setText("");
        allSettingPane.setVisible(true);
        passwordPane.setVisible(false);
        wrongPasswordLabel.setVisible(false);
        Setting.getSetting().revalidate();
        Setting.getSetting().repaint();
    }
}
