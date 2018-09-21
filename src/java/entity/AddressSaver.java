package entity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import ui.setting.CameraAddressSetting;
import ui.main.MainFrame;

/**
 * Class save data from setting to .txt file. xml format
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "address")
public class AddressSaver {

    private String PASS;
    /**
     * all data will save in array
     */
    private String[] arr = new String[24];

    /**
     * audio module address
     */
    private String audioAddress;
    /**
     * count, how many seconds video program will save
     */
    private int timeToSave;
    /**
     * enable program light catch
     */
    private boolean programLightCatchEnable;

    /**
     * set how many percent should change the percent of white on image to
     * switch on the starting saving video, used for setting of program lightning catch
     */
    private int changeWhitePercent;
    /**
     * sec
     */
    private int lightSensitivity;
    /**
     * opacity of background
     */
    private int opacity;
    /**
     * port for program server, for waiting signal to start save video
     */
    private int port;
    /**
     * path to folder, to save video bytes, and files
     */
    private String path;

    private int[][] firstGroup;
    private int[][] secondGroup;
    private int[][] thirdGroup;
    private int[][] fourthGroup;
    private int[][] camerasPosition;

    private AddressSaver() {
        camerasPosition = new int[4][];
        camerasPosition[0] = new int[]{70, 90};
        camerasPosition[1] = new int[]{90, 140};
        camerasPosition[2] = new int[]{30, 140};
        camerasPosition[3] = new int[]{180, 150};
        PASS = "PASS";
    }



    /**
     * save data from camera setting
     *
     * @param numberOfCamera - numberOfCamera
     * @param ipAddress      - ipAddress
     * @param username       - username
     * @param password       - password
     */
    public void saveCameraData(int numberOfCamera, String ipAddress, String username, String password) {
        if (numberOfCamera == 0) {
            audioAddress = ipAddress;
        } else {
            int ipAddressInt = numberOfCamera - 1;
            int userNameInt = numberOfCamera + 7;
            int passwordInt = numberOfCamera + 15;
            arr[ipAddressInt] = ipAddress;
            arr[userNameInt] = username;
            arr[passwordInt] = password;
        }
        savePasswordSaverToFile();
    }

    /**
     * save data from common setting
     *
     * @param timeToSave              -
     * @param programLightCatchEnable - programLightCatchEnable
     * @param changeWhitePercent      - changeWhitePercent
     * @param lightSensitivity        - lightSensitivity
     * @param opacity                 - opacity
     * @param port                    - port
     * @param path                    - path
     */

    public void saveSetting(int timeToSave, boolean programLightCatchEnable, int changeWhitePercent,
                            int lightSensitivity, int opacity, int port, String path) {
        this.changeWhitePercent = changeWhitePercent;
        this.lightSensitivity = lightSensitivity;
        this.opacity = opacity;
        this.timeToSave = timeToSave;
        this.programLightCatchEnable = programLightCatchEnable;
        this.port = port;
        this.path = path;

        savePasswordSaverToFile();
    }

    public void saveLinePoints(int groupNumber, int[][] list) {
        switch (groupNumber) {
            case 1:
                firstGroup = list;
                break;
            case 2:
                secondGroup = list;
                break;
            case 3:
                thirdGroup = list;
                break;
            case 4:
                fourthGroup = list;
                break;
            default:
        }
        savePasswordSaverToFile();
    }

    /**
     * remove all data from saver
     */
    public void cleanSaver() {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = "";
            audioAddress = null;
        }
    }

    /**
     * save data to file
     */
    private void savePasswordSaverToFile() {
        String pathFile = "C:\\LIGHTNING_STABLE\\data\\address.txt";
        File file = new File(pathFile);
        try {
            boolean ok = file.exists();
            if (!ok) {
                ok = file.createNewFile();
            }

            if (ok) {
                JAXBContext context = JAXBContext.newInstance(AddressSaver.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.marshal(this, file);
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return - the address saver, restored from file
     */
    public static AddressSaver restorePasswords() {
        String pathFile = "C:\\LIGHTNING_STABLE\\data\\address.txt";
        File file = new File(pathFile);
        Object passwordsSaverObject = null;
        if (file.canRead()) {
            try {
                JAXBContext context = JAXBContext.newInstance(AddressSaver.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                passwordsSaverObject = unmarshaller.unmarshal(file);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        AddressSaver passwordSaver = null;
        if (passwordsSaverObject != null) {
            try {
                passwordSaver = (AddressSaver) passwordsSaverObject;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }

        for (int i = 1; i < 5; i++) {
            File imageFile = new File("C:\\LIGHTNING_STABLE\\buff\\" + i + ".jpg");
            if (imageFile.exists()) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(imageFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (bufferedImage != null) {
                    ui.main.MainFrame.addBackgroundForBlock(bufferedImage, i);
                }
            }
        }

        if (passwordSaver != null) {
            return passwordSaver;
        } else {
            passwordSaver = new AddressSaver();
            return passwordSaver;
        }
    }

    /**
     * set all data about common setting and camera setting to fields in each panels
     */

    public void setPasswordsToFields() {
        Map<Integer, JTextField> textFieldsIpAddressMap = ui.setting.CameraAddressSetting.getCameraAddressSetting().getTextFieldsIpAddressMap();
        for (Integer integer : textFieldsIpAddressMap.keySet()) {
            if (integer != null) {
                textFieldsIpAddressMap.get(integer).setText(arr[integer - 1]);
            } else {
                textFieldsIpAddressMap.get(integer).setText(audioAddress);
            }
        }

        Map<Integer, JTextField> textFieldsUsernameMap = CameraAddressSetting.getCameraAddressSetting().getTextFieldsUsernameMap();
        for (Integer integer : textFieldsUsernameMap.keySet()) {
            if (integer != null) {
                textFieldsUsernameMap.get(integer).setText(arr[integer + 7]);
            }
        }

        Map<Integer, JTextField> textFieldsPasswordMap = CameraAddressSetting.getCameraAddressSetting().getTextFieldsPasswordMap();
        for (Integer integer : textFieldsPasswordMap.keySet()) {
            if (integer != null) {
                textFieldsPasswordMap.get(integer).setText(arr[integer + 15]);
            }
        }


        for (Integer integer : MainFrame.imagesForBlock.keySet()) {
            if (integer != null) {
                CameraAddressSetting.getCameraAddressSetting().setHaveImage(integer);
            }
        }
    }

    /**
     * set all setting to program
     */
    public void setSetting() {
        MainFrame.setProgramLightCatchEnable(programLightCatchEnable);
        MainFrame.setPercentDiffWhite(changeWhitePercent);
        MainFrame.getMainFrame().setColorLightNumber(lightSensitivity);
        MainFrame.setOpacitySetting(opacity);
        MainFrame.setCountSecondsToSaveVideo(timeToSave);

        MainFrame.addLinePoint(1, firstGroup,false);
        MainFrame.addLinePoint(2, secondGroup,false);
        MainFrame.addLinePoint(3, thirdGroup,false);
        MainFrame.addLinePoint(4, fourthGroup,false);
        MainFrame.setCamerasPosition(camerasPosition);
        if (path != null) {
            MainFrame.setPath(path);
        }
        MainFrame.setPort(port);
        /*
      password to enter setting, will be changed manual
     */ /**
         * password to enter setting, will be changed manual
         * */
//        String password = "PASS";
        MainFrame.setPassword(PASS);
    }

    public void setCamerasPosition(int[][] camerasPosition) {
        this.camerasPosition = camerasPosition;
        savePasswordSaverToFile();
    }
}
