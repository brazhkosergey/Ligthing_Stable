package ui.setting;

import entity.Storage.Storage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
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


    private JTextField[][] textFields;
    private JTextField addressAudioTextField;
    private Map<Integer, JLabel> labelMap;

    private CameraAddressSetting() {
        textFields = new JTextField[8][];
        labelMap = new HashMap<>();
        buildCameraSetting();
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

    private void buildCameraSetting() {
        JButton saveButton = new JButton(Storage.getBundle().getString("savepartvideobutton"));
        saveButton.setFont(new Font(null, Font.BOLD, 17));
        saveButton.addActionListener((e) -> {
            saveCameraInformationToAddressSaver();
            Storage.startAllCameras();
        });

        mainCameraSettingPanel = new JPanel();
        mainCameraSettingPanel.setBorder(BorderFactory.createEtchedBorder());
        mainCameraSettingPanel.setLayout(new BoxLayout(mainCameraSettingPanel, BoxLayout.Y_AXIS));
        for (int cameraGroup = 0; cameraGroup < 4; cameraGroup++) {
            JPanel blockPanel = new JPanel(new FlowLayout());
            JPanel inputPanel = new JPanel(new FlowLayout());
            JPanel cameraBlock = new JPanel();
            cameraBlock.setLayout(new BoxLayout(cameraBlock, BoxLayout.Y_AXIS));

            for (int cameraNumber = 0; cameraNumber < 2; cameraNumber++) {
                JPanel cameraOneSetting = new JPanel(new FlowLayout());
                JLabel firstCameraLabel = new JLabel(Storage.getBundle().getString("cameraword") + ((cameraGroup * 2) + cameraNumber+1));
                firstCameraLabel.setFont(new Font(null, Font.BOLD, 15));
                JTextField firstCameraIpAddressTextField = new JTextField();
                firstCameraIpAddressTextField.setPreferredSize(new Dimension(320, 20));

                JLabel firstCameraUserNameLabel = new JLabel(Storage.getBundle().getString("username"));
                JTextField firstCameraUserNameTextField = new JTextField();
                firstCameraUserNameTextField.setPreferredSize(new Dimension(100, 20));
                JLabel firstCameraPasswordLabel = new JLabel(Storage.getBundle().getString("password"));
                JTextField firstCameraPasswordTextField = new JTextField();
                firstCameraPasswordTextField.setPreferredSize(new Dimension(100, 20));

                textFields[cameraGroup * 2 + cameraNumber] = new JTextField[3];
                textFields[cameraGroup * 2 + cameraNumber][0] = firstCameraIpAddressTextField;
                textFields[cameraGroup * 2 + cameraNumber][1] = firstCameraUserNameTextField;
                textFields[cameraGroup * 2 + cameraNumber][2] = firstCameraPasswordTextField;

                cameraOneSetting.add(firstCameraLabel);
                cameraOneSetting.add(firstCameraIpAddressTextField);
                cameraOneSetting.add(Box.createRigidArea(new Dimension(5, 20)));
                cameraOneSetting.add(firstCameraUserNameLabel);
                cameraOneSetting.add(firstCameraUserNameTextField);
                cameraOneSetting.add(firstCameraPasswordLabel);
                cameraOneSetting.add(firstCameraPasswordTextField);
                cameraBlock.add(cameraOneSetting);
            }
//            JPanel cameraOneSetting = new JPanel(new FlowLayout());
//            JLabel firstCameraLabel = new JLabel(Storage.getBundle().getString("cameraword") + ((cameraGroup * 2) + 1));
//            firstCameraLabel.setFont(new Font(null, Font.BOLD, 15));
//            JTextField firstCameraIpAddressTextField = new JTextField();
//            firstCameraIpAddressTextField.setPreferredSize(new Dimension(320, 20));
//
//            JLabel firstCameraUserNameLabel = new JLabel(Storage.getBundle().getString("username"));
//            JTextField firstCameraUserNameTextField = new JTextField();
//            firstCameraUserNameTextField.setPreferredSize(new Dimension(100, 20));
//            JLabel firstCameraPasswordLabel = new JLabel(Storage.getBundle().getString("password"));
//            JTextField firstCameraPasswordTextField = new JTextField();
//            firstCameraPasswordTextField.setPreferredSize(new Dimension(100, 20));
//
//            textFields[cameraGroup * 2] = new JTextField[3];
//            textFields[cameraGroup * 2][0] = firstCameraIpAddressTextField;
//            textFields[cameraGroup * 2][1] = firstCameraUserNameTextField;
//            textFields[cameraGroup * 2][2] = firstCameraPasswordTextField;
//
//            cameraOneSetting.add(firstCameraLabel);
//            cameraOneSetting.add(firstCameraIpAddressTextField);
//            cameraOneSetting.add(Box.createRigidArea(new Dimension(5, 20)));
//            cameraOneSetting.add(firstCameraUserNameLabel);
//            cameraOneSetting.add(firstCameraUserNameTextField);
//            cameraOneSetting.add(firstCameraPasswordLabel);
//            cameraOneSetting.add(firstCameraPasswordTextField);
//
//            JPanel cameraTwoSetting = new JPanel(new FlowLayout());
//            JLabel secondCameraLabel = new JLabel(Storage.getBundle().getString("cameraword") + ((cameraGroup * 2) + 2));
//            secondCameraLabel.setFont(new Font(null, Font.BOLD, 15));
//            JTextField secondCameraIpAddressTextField = new JTextField();
//            secondCameraIpAddressTextField.setPreferredSize(new Dimension(320, 20));
//
//            JLabel secondCameraUserNameLabel = new JLabel(Storage.getBundle().getString("username"));
//            JTextField secondCameraUserNameTextField = new JTextField();
//            secondCameraUserNameTextField.setPreferredSize(new Dimension(100, 20));
//
//            JLabel secondCameraPasswordLabel = new JLabel(Storage.getBundle().getString("password"));
//            JTextField secondCameraPasswordTextField = new JTextField();
//            secondCameraPasswordTextField.setPreferredSize(new Dimension(100, 20));
//
//            textFields[cameraGroup * 2 + 1] = new JTextField[3];
//            textFields[cameraGroup * 2 + 1][0] = secondCameraIpAddressTextField;
//            textFields[cameraGroup * 2 + 1][1] = secondCameraUserNameTextField;
//            textFields[cameraGroup * 2 + 1][2] = secondCameraPasswordTextField;
//
//            cameraTwoSetting.add(secondCameraLabel);
//            cameraTwoSetting.add(secondCameraIpAddressTextField);
//            cameraTwoSetting.add(Box.createRigidArea(new Dimension(5, 20)));
//            cameraTwoSetting.add(secondCameraUserNameLabel);
//            cameraTwoSetting.add(secondCameraUserNameTextField);
//            cameraTwoSetting.add(secondCameraPasswordLabel);
//            cameraTwoSetting.add(secondCameraPasswordTextField);
//
//            cameraBlock.add(cameraOneSetting);
//            cameraBlock.add(cameraTwoSetting);

            JLabel addImageLabel = new JLabel(Storage.getBundle().getString("selectimage"));
            labelMap.put(cameraGroup, addImageLabel);
            addImageLabel.setFont(new Font(null, Font.BOLD, 15));
            addImageLabel.setPreferredSize(new Dimension(210, 20));

            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));

            JButton addImageButton = new JButton(Storage.getBundle().getString("selectimagefilebutton"));
            int number = cameraGroup + 1;
            addImageButton.addActionListener((e) -> {
                JFileChooser fileChooser = new JFileChooser(Storage.getBundle().getString("selectimagefilebutton"));
                fileChooser.setFont(new Font(null, Font.BOLD, 12));
                fileChooser.setApproveButtonText(Storage.getBundle().getString("selectimagefilebutton"));
                int ret = fileChooser.showDialog(null, Storage.getBundle().getString("selectimagefilebutton"));
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    File imageFile = new File(Storage.getDefaultPath() + "\\buff\\" + number + ".jpg");
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

                    Storage.getCameraGroups()[number-1].setBackGroundImage(bufferedImage);
                    addImageLabel.setText(Storage.getBundle().getString("imagehadadded"));
                    addImageLabel.setForeground(new Color(46, 139, 87));
                }
            });

            JButton removeButton = new JButton(Storage.getBundle().getString("deleteimagefilebutton"));
            removeButton.addActionListener((e) -> {
                File imageFile = new File("C:\\LIGHTNING_STABLE\\buff\\" + number + ".jpg");
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                Storage.getCameraGroups()[number-1].setBackGroundImage(null);
                addImageLabel.setText(Storage.getBundle().getString("selectimage"));
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

            TitledBorder titleMainSetting = BorderFactory.createTitledBorder(Storage.getBundle().getString("cameragroup") + (cameraGroup + 1));
            titleMainSetting.setTitleJustification(TitledBorder.CENTER);
            titleMainSetting.setTitleFont((new Font(null, Font.BOLD, 14)));
            titleMainSetting.setTitleColor(new Color(46, 139, 87));
            titleMainSetting.setBorder(BorderFactory.createEtchedBorder());
            blockPanel.setBorder(titleMainSetting);

            blockPanel.add(inputPanel);
            blockPanel.setMaximumSize(new Dimension(1110, 110));
            mainCameraSettingPanel.add(blockPanel);

            if (cameraGroup == 3) {
                JPanel audioPane = new JPanel(new FlowLayout());
                audioPane.setMaximumSize(new Dimension(1110, 110));
                TitledBorder titleAudio = BorderFactory.createTitledBorder(Storage.getBundle().getString("soundtitle"));
                titleAudio.setTitleJustification(TitledBorder.CENTER);
                titleAudio.setTitleFont((new Font(null, Font.BOLD, 14)));
                titleAudio.setTitleColor(new Color(46, 139, 87));
                titleAudio.setBorder(BorderFactory.createEtchedBorder());
                audioPane.setBorder(titleAudio);
                JLabel addressAudioLabel = new JLabel(Storage.getBundle().getString("rtspaddress"));
                addressAudioTextField = new JTextField();
                addressAudioTextField.setPreferredSize(new Dimension(250, 25));
                audioPane.add(addressAudioLabel);
                audioPane.add(addressAudioTextField);
                audioPane.add(Box.createRigidArea(new Dimension(220, 10)));
                audioPane.add(saveButton);
                mainCameraSettingPanel.add(audioPane);
            }
        }
    }

    public void setAddressToTextFields(String[][] info) {
        for (int cameraNumber = 0; cameraNumber < 8; cameraNumber++) {
            for (int infoNumber = 0; infoNumber < 3; infoNumber++) {
                textFields[cameraNumber][infoNumber].setText(info[cameraNumber][infoNumber]);
            }
        }
    }

    public void setHaveImage(int integer) {
        if (labelMap.containsKey(integer)) {
            labelMap.get(integer).setText(Storage.getBundle().getString("imagehadadded"));
            labelMap.get(integer).setForeground(new Color(46, 139, 87));
        }
    }

    /**
     * save data from text fields
     */
    private void saveCameraInformationToAddressSaver() {
        for (int i = 0; i < 8; i++) {
            Storage.getAddressSaver().saveCameraData(i, textFields[i][0].getText(), textFields[i][1].getText(), textFields[i][2].getText());
        }
        Storage.getAddressSaver().saveAudioAddress(addressAudioTextField.getText());
    }

    public void setAddressAudioTextField(String addressAudio) {
        if (addressAudio != null) {
            addressAudioTextField.setText(addressAudio);
        }
    }
}
