package ui.setting;

import ui.main.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * panel with camera ipAddress, user names and passwords setting
 */
public class CameraAddressSetting extends JPanel {
    private static CameraAddressSetting cameraAddressSetting;
    private JPanel mainCameraSettingPanel;

    /**
     * collections for each camera - number of camera(1-8) as key, and text field as value
     */
    private Map<Integer, JTextField> textFieldsIpAddressMap;
    private Map<Integer, JTextField> textFieldsUsernameMap;
    private Map<Integer, JTextField> textFieldsPasswordMap;
    private Map<Integer, JLabel> labelMap;
    private Map<Integer, JPanel> groupsPanelMap;

    private CameraAddressSetting() {
        textFieldsIpAddressMap = new HashMap<>();
        textFieldsUsernameMap = new HashMap<>();
        textFieldsPasswordMap = new HashMap<>();
        labelMap = new HashMap<>();
        groupsPanelMap = new HashMap<>();

        buildNewCamerasAddressPanel();

        buildCameraSetting();
        this.setLayout(new FlowLayout());
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        this.add(mainCameraSettingPanel, BorderLayout.CENTER);
    }

    public static CameraAddressSetting getCameraAddressSetting() {
        if (cameraAddressSetting != null) {
            return cameraAddressSetting;
        } else {
            cameraAddressSetting = new CameraAddressSetting();
            return cameraAddressSetting;
        }
    }

    private void buildNewCamerasAddressPanel() {
        mainCameraSettingPanel = new JPanel(new BorderLayout(2, 2));

        JPanel mainLeftPanel = new JPanel(new BorderLayout(2, 2));
        JPanel mainCenterPanel = new JPanel(new BorderLayout(2, 2));

        JPanel groupSelectionPanel = new JPanel(new GridLayout(5, 1, 2, 2));
        groupSelectionPanel.setBorder(BorderFactory.createEtchedBorder());

        List<JPanel> groupButtonsPanelList = new ArrayList<>();

        for (int groupNumber = 1; groupNumber < 5; groupNumber++) {
            JPanel groupMainPanel = new JPanel(new BorderLayout());
            TitledBorder groupBorder = BorderFactory.createTitledBorder("Group " + groupNumber);
            groupBorder.setTitleJustification(TitledBorder.CENTER);
            groupMainPanel.setBorder(groupBorder);
            JPanel addressPanel = new JPanel(new GridLayout(2, 1));
            addressPanel.setBorder(BorderFactory.createTitledBorder("IP address"));
            for (int numberOfCamera = 1; numberOfCamera < 3; numberOfCamera++) {

                JPanel cameraSetting = new JPanel(new FlowLayout());
                JLabel firstCameraLabel = new JLabel(MainFrame.getBundle().getString("cameraword") + numberOfCamera);
                firstCameraLabel.setFont(new Font(null, Font.BOLD, 15));
                JTextField firstCameraTextField = new JTextField();
                firstCameraTextField.setPreferredSize(new Dimension(350, 25));

                JLabel firstCameraUserNameLabel = new JLabel(MainFrame.getBundle().getString("username"));
                JTextField firstCameraUserNameTextField = new JTextField();
                firstCameraUserNameTextField.setPreferredSize(new Dimension(150, 25));
                JLabel firstCameraPasswordLabel = new JLabel(MainFrame.getBundle().getString("password"));
                JTextField firstCameraPasswordTextField = new JTextField();
                firstCameraPasswordTextField.setPreferredSize(new Dimension(150, 25));

                int numberToSaveData;
                if (numberOfCamera == 1) {
                    numberToSaveData = groupNumber * 2 - 1;
                } else {
                    numberToSaveData = groupNumber * 2;
                }

                textFieldsIpAddressMap.put(numberToSaveData, firstCameraTextField);
                textFieldsUsernameMap.put(numberToSaveData, firstCameraUserNameTextField);
                textFieldsPasswordMap.put(numberToSaveData, firstCameraPasswordTextField);
                cameraSetting.add(firstCameraLabel);
                cameraSetting.add(firstCameraTextField);
                cameraSetting.add(Box.createRigidArea(new Dimension(5, 20)));
                cameraSetting.add(firstCameraUserNameLabel);
                cameraSetting.add(firstCameraUserNameTextField);
                cameraSetting.add(firstCameraPasswordLabel);
                cameraSetting.add(firstCameraPasswordTextField);
                addressPanel.add(cameraSetting);
            }

            groupMainPanel.add(addressPanel, BorderLayout.NORTH);

            JPanel imageBackGroundPanel = new JPanel(new BorderLayout(2, 2));
            imageBackGroundPanel.setBorder(BorderFactory.createTitledBorder("Background"));
            JPanel imagePanel = new JPanel(new BorderLayout());

            imageBackGroundPanel.add(imagePanel);

            JPanel addImageButtonsPanel = new JPanel(new GridLayout(2, 1));
            JButton addImageButton = new JButton(MainFrame.getBundle().getString("selectimagefilebutton"));
            int number = groupNumber;

            File backGroundFile = new File(MainFrame.getDefaultPath() + "\\buff\\" + number + ".jpg");
            if (backGroundFile.exists()) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(backGroundFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                BufferedImage bf = bufferedImage;

                imagePanel.add(new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (bf != null) {
                            int x = 0;

                            int imageWidth = imagePanel.getWidth();
                            int panelWidth = imagePanel.getWidth();
                            if (panelWidth > imageWidth) {
                                x = (panelWidth - imageWidth) / 2;
                            }
                            g.drawImage(bf, x, 0, null);
                        }
                    }
                });
            } else {
                imagePanel.removeAll();
                JLabel selectImageLabel = new JLabel(MainFrame.getBundle().getString("selectimage"));
                selectImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                selectImageLabel.setVerticalAlignment(SwingConstants.CENTER);
                imagePanel.add(selectImageLabel);
                imagePanel.validate();
                imagePanel.repaint();
            }
            addImageButton.addActionListener((e) -> {
                JFileChooser fileChooser = new JFileChooser(MainFrame.getBundle().getString("selectimagefilebutton"));
                fileChooser.setFont(new Font(null, Font.BOLD, 12));
                fileChooser.setApproveButtonText(MainFrame.getBundle().getString("selectimagefilebutton"));
                int ret = fileChooser.showDialog(null, MainFrame.getBundle().getString("selectimagefilebutton"));
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    File imageFile = new File(MainFrame.getDefaultPath() + "\\buff\\" + number + ".jpg");
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }

                    InputStream is;
                    OutputStream os;
                    try {
                        imageFile.createNewFile();
                        is = new FileInputStream(file);
                        os = new FileOutputStream(imageFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        is.close();
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (bufferedImage != null) {
                        BufferedImage bf = bufferedImage;
                        MainFrame.addBackgroundForBlock(bufferedImage, number);
                        MainFrame.videoSaversMap.get(number).setBackGroundImage(bufferedImage);
                        imagePanel.removeAll();
                        imagePanel.add(new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                if (bf != null) {
                                    int x = 0;

                                    int imageWidth = imagePanel.getWidth();
                                    int panelWidth = imagePanel.getWidth();
                                    if (panelWidth > imageWidth) {
                                        x = (panelWidth - imageWidth) / 2;
                                    }
                                    g.drawImage(bf, x, 0, null);

                                }
                            }

                        });
                        imagePanel.repaint();
                        imagePanel.validate();

                    }
                }
            });

            JButton removeButton = new JButton(MainFrame.getBundle().getString("deleteimagefilebutton"));
            removeButton.addActionListener((e) -> {
                File imageFile = new File(MainFrame.getDefaultPath() + "\\buff\\" + number + ".jpg");

                if (imageFile.exists()) {
                    imageFile.delete();
                }

                imagePanel.removeAll();
                imagePanel.add(new JLabel(MainFrame.getBundle().getString("selectimage")));
                imagePanel.validate();
                imagePanel.repaint();
                MainFrame.removeBackgroundForBlock(number);
            });

            addImageButtonsPanel.add(addImageButton);
            addImageButtonsPanel.add(removeButton);

            imageBackGroundPanel.add(addImageButtonsPanel, BorderLayout.EAST);
            groupMainPanel.add(imageBackGroundPanel, BorderLayout.CENTER);
            if (groupNumber == 1) {
                mainCenterPanel.add(groupMainPanel, BorderLayout.CENTER);
            }
            groupsPanelMap.put(groupNumber, groupMainPanel);

            JPanel groupPanel = new JPanel(new BorderLayout());
            groupButtonsPanelList.add(groupPanel);
            groupPanel.setBorder(BorderFactory.createEtchedBorder());
            JLabel groupLabel = new JLabel("Group " + groupNumber);
            groupLabel.setHorizontalAlignment(SwingConstants.CENTER);
            groupLabel.setVerticalAlignment(SwingConstants.CENTER);
            groupPanel.add(groupLabel);
            int grN = groupNumber;
            groupPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    JPanel currentPanel = groupsPanelMap.get(grN);
                    mainCenterPanel.removeAll();
                    mainCenterPanel.add(currentPanel, BorderLayout.CENTER);
                    mainCenterPanel.validate();
                    mainCenterPanel.repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    for (JPanel panel : groupButtonsPanelList) {
                        if (panel != groupPanel) {
                            panel.setBackground(new Color(233, 233, 233));
                        }
                    }

                    groupPanel.setBackground(new Color(251, 249, 250));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    groupPanel.setBackground(Color.LIGHT_GRAY);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            groupSelectionPanel.add(groupPanel);
        }


        mainLeftPanel.add(groupSelectionPanel, BorderLayout.CENTER);

        JPanel buttonStartCamerasPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonStartCamerasPanel.setPreferredSize(new Dimension(250, 53));
        buttonStartCamerasPanel.setBorder(BorderFactory.createEtchedBorder());
        JButton startButton = new JButton(MainFrame.getBundle().getString("startcameras"));
        startButton.setFont(new Font(null, Font.BOLD, 17));
        startButton.addActionListener((e) -> {
            MainFrame.addressSaver.cleanSaver();
            saveAddressToMap();
            MainFrame.getMainFrame().startAllCameras();
        });
        buttonStartCamerasPanel.add(startButton, BorderLayout.CENTER);
        mainLeftPanel.add(buttonStartCamerasPanel, BorderLayout.SOUTH);

        mainCameraSettingPanel.add(mainLeftPanel, BorderLayout.WEST);

        JPanel audioPane = new JPanel(new FlowLayout());
        audioPane.setMaximumSize(new Dimension(1110, 110));
        TitledBorder titleAudio = BorderFactory.createTitledBorder(MainFrame.getBundle().getString("soundtitle"));
        titleAudio.setTitleJustification(TitledBorder.CENTER);
        titleAudio.setTitleFont((new Font(null, Font.BOLD, 14)));
        titleAudio.setBorder(BorderFactory.createEtchedBorder());
        audioPane.setBorder(titleAudio);
        JLabel addressAudioLabel = new JLabel(MainFrame.getBundle().getString("rtspaddress"));
        JTextField addressAudioTextField = new JTextField();
        textFieldsIpAddressMap.put(null, addressAudioTextField);
        addressAudioTextField.setPreferredSize(new Dimension(350, 25));
        audioPane.add(addressAudioLabel);
        audioPane.add(addressAudioTextField);
        audioPane.add(Box.createRigidArea(new Dimension(220, 10)));
        mainCenterPanel.add(audioPane, BorderLayout.SOUTH);


        mainCameraSettingPanel.add(mainCenterPanel, BorderLayout.CENTER);
    }

    private void buildCameraSetting() {
        JButton saveButton = new JButton(MainFrame.getBundle().getString("startcameras"));
        saveButton.setFont(new Font(null, Font.BOLD, 17));
        saveButton.addActionListener((e) -> {
            MainFrame.addressSaver.cleanSaver();
            saveAddressToMap();
            MainFrame.getMainFrame().startAllCameras();
        });
        mainCameraSettingPanel = new JPanel();
        mainCameraSettingPanel.setBorder(BorderFactory.createEtchedBorder());
        mainCameraSettingPanel.setLayout(new BoxLayout(mainCameraSettingPanel, BoxLayout.Y_AXIS));
//        mainCameraSettingPanel.setLayout(new GridLayout(6, 1));
        for (int i = 1; i < 5; i++) {
//        for (int i = 1; i < 6; i++) {
            JPanel blockPanel = new JPanel(new FlowLayout());
            JPanel inputPanel = new JPanel(new FlowLayout());
            JPanel cameraBlock = new JPanel();
            cameraBlock.setLayout(new BoxLayout(cameraBlock, BoxLayout.Y_AXIS));

            JPanel cameraOneSetting = new JPanel(new FlowLayout());
            JLabel firstCameraLabel = new JLabel(MainFrame.getBundle().getString("cameraword") + (i * 2 - 1));
            firstCameraLabel.setFont(new Font(null, Font.BOLD, 15));
            JTextField firstCameraTextField = new JTextField();
            firstCameraTextField.setPreferredSize(new Dimension(320, 20));

            JLabel firstCameraUserNameLabel = new JLabel(MainFrame.getBundle().getString("username"));
            JTextField firstCameraUserNameTextField = new JTextField();
            firstCameraUserNameTextField.setPreferredSize(new Dimension(100, 20));
            JLabel firstCameraPasswordLabel = new JLabel(MainFrame.getBundle().getString("password"));
            JTextField firstCameraPasswordTextField = new JTextField();
            firstCameraPasswordTextField.setPreferredSize(new Dimension(100, 20));
            textFieldsIpAddressMap.put(i * 2 - 1, firstCameraTextField);
            textFieldsUsernameMap.put(i * 2 - 1, firstCameraUserNameTextField);
            textFieldsPasswordMap.put(i * 2 - 1, firstCameraPasswordTextField);
            cameraOneSetting.add(firstCameraLabel);
            cameraOneSetting.add(firstCameraTextField);
            cameraOneSetting.add(Box.createRigidArea(new Dimension(5, 20)));
            cameraOneSetting.add(firstCameraUserNameLabel);
            cameraOneSetting.add(firstCameraUserNameTextField);
            cameraOneSetting.add(firstCameraPasswordLabel);
            cameraOneSetting.add(firstCameraPasswordTextField);

            JPanel cameraTwoSetting = new JPanel(new FlowLayout());
            JLabel secondCameraLabel = new JLabel(MainFrame.getBundle().getString("cameraword") + (i * 2));
            secondCameraLabel.setFont(new Font(null, Font.BOLD, 15));
            JTextField secondCameraTextField = new JTextField();
            secondCameraTextField.setPreferredSize(new Dimension(320, 20));

            JLabel secondCameraUserNameLabel = new JLabel(MainFrame.getBundle().getString("username"));
            JTextField secondCameraUserNameTextField = new JTextField();
            secondCameraUserNameTextField.setPreferredSize(new Dimension(100, 20));

            JLabel secondCameraPasswordLabel = new JLabel(MainFrame.getBundle().getString("password"));
            JTextField secondCameraPasswordTextField = new JTextField();
            secondCameraPasswordTextField.setPreferredSize(new Dimension(100, 20));
            textFieldsIpAddressMap.put(i * 2, secondCameraTextField);
            textFieldsUsernameMap.put(i * 2, secondCameraUserNameTextField);
            textFieldsPasswordMap.put(i * 2, secondCameraPasswordTextField);

            cameraTwoSetting.add(secondCameraLabel);
            cameraTwoSetting.add(secondCameraTextField);
            cameraTwoSetting.add(Box.createRigidArea(new Dimension(5, 20)));
            cameraTwoSetting.add(secondCameraUserNameLabel);
            cameraTwoSetting.add(secondCameraUserNameTextField);
            cameraTwoSetting.add(secondCameraPasswordLabel);
            cameraTwoSetting.add(secondCameraPasswordTextField);

            cameraBlock.add(cameraOneSetting);
            cameraBlock.add(cameraTwoSetting);

            JLabel addImageLabel = new JLabel(MainFrame.getBundle().getString("selectimage"));
            labelMap.put(i, addImageLabel);
            addImageLabel.setFont(new Font(null, Font.BOLD, 15));
            addImageLabel.setPreferredSize(new Dimension(210, 20));

            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));

            JButton addImageButton = new JButton(MainFrame.getBundle().getString("selectimagefilebutton"));
            int number = i;
            addImageButton.addActionListener((e) -> {
                JFileChooser fileChooser = new JFileChooser(MainFrame.getBundle().getString("selectimagefilebutton"));
                fileChooser.setFont(new Font(null, Font.BOLD, 12));
                fileChooser.setApproveButtonText(MainFrame.getBundle().getString("selectimagefilebutton"));
                int ret = fileChooser.showDialog(null, MainFrame.getBundle().getString("selectimagefilebutton"));
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    File imageFile = new File(MainFrame.getDefaultPath() + "\\buff\\" + number + ".jpg");
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }

                    InputStream is;
                    OutputStream os;
                    try {
                        imageFile.createNewFile();
                        is = new FileInputStream(file);
                        os = new FileOutputStream(imageFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        is.close();
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (bufferedImage != null) {
                        MainFrame.addBackgroundForBlock(bufferedImage, number);
                        MainFrame.videoSaversMap.get(number).setBackGroundImage(bufferedImage);
                    }

                    addImageLabel.setText(MainFrame.getBundle().getString("imagehadadded"));
                    addImageLabel.setForeground(new Color(46, 139, 87));

                }
            });

            JButton removeButton = new JButton(MainFrame.getBundle().getString("deleteimagefilebutton"));
            removeButton.addActionListener((e) -> {
                File imageFile = new File("C:\\LIGHTNING_STABLE\\buff\\" + number + ".jpg");
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                MainFrame.removeBackgroundForBlock(number);
                addImageLabel.setText(MainFrame.getBundle().getString("selectimage"));
                addImageLabel.setForeground(Color.BLACK);
            });

            buttonPane.add(addImageButton);
            buttonPane.add(Box.createRigidArea(new Dimension(20, 5)));
            buttonPane.add(removeButton);

            inputPanel.add(cameraBlock);
            inputPanel.add(Box.createRigidArea(new Dimension(5, 5)));
            inputPanel.add(addImageLabel);
            inputPanel.add(Box.createRigidArea(new Dimension(5, 5)));
            inputPanel.add(buttonPane);

            TitledBorder titleMainSetting = BorderFactory.createTitledBorder(MainFrame.getBundle().getString("cameragroup") + i);
            titleMainSetting.setTitleJustification(TitledBorder.CENTER);
            titleMainSetting.setTitleFont((new Font(null, Font.BOLD, 14)));
            titleMainSetting.setTitleColor(new Color(46, 139, 87));
            titleMainSetting.setBorder(BorderFactory.createEtchedBorder());
            blockPanel.setBorder(titleMainSetting);

            blockPanel.add(inputPanel);
            blockPanel.setMaximumSize(new Dimension(1110, 110));
            mainCameraSettingPanel.add(blockPanel);

            if (i == 4) {
//            if (i == 5) {
                JPanel audioPane = new JPanel(new FlowLayout());
                audioPane.setMaximumSize(new Dimension(1110, 110));
                TitledBorder titleAudio = BorderFactory.createTitledBorder(MainFrame.getBundle().getString("soundtitle"));
                titleAudio.setTitleJustification(TitledBorder.CENTER);
                titleAudio.setTitleFont((new Font(null, Font.BOLD, 14)));
                titleAudio.setTitleColor(new Color(46, 139, 87));
                titleAudio.setBorder(BorderFactory.createEtchedBorder());
                audioPane.setBorder(titleAudio);
                JLabel addressAudioLabel = new JLabel(MainFrame.getBundle().getString("rtspaddress"));
                JTextField addressAudioTextField = new JTextField();
                textFieldsIpAddressMap.put(null, addressAudioTextField);
                addressAudioTextField.setPreferredSize(new Dimension(250, 25));
                audioPane.add(addressAudioLabel);
                audioPane.add(addressAudioTextField);
                audioPane.add(Box.createRigidArea(new Dimension(220, 10)));
                audioPane.add(saveButton);
                mainCameraSettingPanel.add(audioPane);
            }
        }
    }

    public void setHaveImage(int integer) {
        if (labelMap.containsKey(integer)) {
            labelMap.get(integer).setText(MainFrame.getBundle().getString("imagehadadded"));
            labelMap.get(integer).setForeground(new Color(46, 139, 87));
        }
    }

    /**
     * save data from text fields
     */
    public void saveAddressToMap() {
        for (Integer textFieldNumber : textFieldsIpAddressMap.keySet()) {
            if (textFieldNumber != null) {
                String ipAddress = textFieldsIpAddressMap.get(textFieldNumber).getText();
                if (ipAddress.length() > 3) {
                    String userName = textFieldsUsernameMap.get(textFieldNumber).getText();
                    String password = textFieldsPasswordMap.get(textFieldNumber).getText();
                    List<String> list = new ArrayList<>();
                    list.add(ipAddress);
                    list.add(userName);
                    list.add(password);
                    MainFrame.addressSaver.saveCameraData(textFieldNumber, ipAddress, userName, password);
                    MainFrame.camerasAddress.put(textFieldNumber, list);
                } else {
                    MainFrame.camerasAddress.put(textFieldNumber, null);
                }
            } else {
                String ipAddress = textFieldsIpAddressMap.get(textFieldNumber).getText();
                if (ipAddress.length() > 3) {
                    if (ipAddress.length() > 1) {
                        List<String> list = new ArrayList<>();
                        list.add(ipAddress);
                        MainFrame.camerasAddress.put(null, list);
                        MainFrame.addressSaver.saveCameraData(0, ipAddress, null, null);
                    } else {
                        MainFrame.camerasAddress.put(null, new ArrayList<>());
                    }
                }
            }
        }
    }

    public Map<Integer, JTextField> getTextFieldsIpAddressMap() {
        return textFieldsIpAddressMap;
    }

    public Map<Integer, JTextField> getTextFieldsUsernameMap() {
        return textFieldsUsernameMap;
    }

    public Map<Integer, JTextField> getTextFieldsPasswordMap() {
        return textFieldsPasswordMap;
    }

}
