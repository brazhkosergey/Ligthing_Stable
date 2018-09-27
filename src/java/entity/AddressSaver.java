package entity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;

import entity.Camera.CameraGroup;
import entity.Storage.Storage;
import ui.setting.CameraAddressSetting;

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
    private String[][] camerasIdentificationInformation;

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
    private double[][] camerasViewAnglesTangens;

    private int hideZoneIdentificationAccuracyCountOfFramesToAnalise;
    private int hideZoneIdentificationAccuracyComparePixels;

    private AddressSaver() {
        lightSensitivity = 140;
        camerasPosition = new int[4][];
        camerasPosition[0] = new int[]{70, 90};
        camerasPosition[1] = new int[]{90, 140};
        camerasPosition[2] = new int[]{180, 150};
        camerasPosition[3] = new int[]{30, 140};

        camerasViewAnglesTangens = new double[4][];
        for (int i = 0; i < camerasPosition.length; i++) {
            camerasViewAnglesTangens[i] = new double[]{(double) camerasPosition[i][0] / camerasPosition[i][1],
                    (double) (camerasPosition[i][0] + 160) / camerasPosition[i][1]};
        }

        camerasIdentificationInformation = new String[8][];
        for (int i = 0; i < 8; i++) {
            camerasIdentificationInformation[i] = new String[3];
        }

        hideZoneIdentificationAccuracyCountOfFramesToAnalise = 1;
        hideZoneIdentificationAccuracyComparePixels = 1;
        PASS = "PASS";
    }

    public int[][] getCamerasPosition() {
        return camerasPosition;
    }

    public String[][] getCamerasIdentificationInformation() {
        return camerasIdentificationInformation;
    }

    public void saveAudioAddress(String audioAddress) {
        this.audioAddress = audioAddress;
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
        camerasIdentificationInformation[numberOfCamera][0] = ipAddress;
        camerasIdentificationInformation[numberOfCamera][1] = username;
        camerasIdentificationInformation[numberOfCamera][2] = password;
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
                            int lightSensitivity, int opacity, int port, String path, int hideZoneIdentificationAccuracyCountOfFramesToAnalise
            , int hideZoneIdentificationAccuracyComparePixels) {
        this.changeWhitePercent = changeWhitePercent;
        this.lightSensitivity = lightSensitivity;
        this.opacity = opacity;
        this.timeToSave = timeToSave;
        this.programLightCatchEnable = programLightCatchEnable;
        this.port = port;
        this.path = path;
        this.hideZoneIdentificationAccuracyCountOfFramesToAnalise = hideZoneIdentificationAccuracyCountOfFramesToAnalise;
        this.hideZoneIdentificationAccuracyComparePixels = hideZoneIdentificationAccuracyComparePixels;
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
        CameraAddressSetting.getCameraAddressSetting().setAddressToTextFields(camerasIdentificationInformation);
        CameraAddressSetting.getCameraAddressSetting().setAddressAudioTextField(audioAddress);
        for (CameraGroup cameraGroup : Storage.getCameraGroups()) {
            if (cameraGroup.getBackGroundImage() != null) {
                CameraAddressSetting.getCameraAddressSetting().setHaveImage(cameraGroup.getGroupNumber() - 1);
            }
        }
    }

    /**
     * set all setting to program
     */
    public void setSetting() {
        Storage.setProgramLightCatchEnable(programLightCatchEnable);
        Storage.setPercentDiffWhite(changeWhitePercent);
        Storage.setColorLightNumber(lightSensitivity);
        Storage.setOpacitySetting(opacity);
        Storage.setSecondsToSave(timeToSave);
        Thread addLinePointThread = new Thread(() -> {
            Storage.addLinePoint(1, firstGroup, false);
            Storage.addLinePoint(2, secondGroup, false);
            Storage.addLinePoint(3, thirdGroup, false);
            Storage.addLinePoint(4, fourthGroup, false);
        });
        addLinePointThread.setPriority(Thread.MIN_PRIORITY);
        addLinePointThread.start();
        if (path != null) {
            Storage.setPath(path);
        }
        Storage.setPort(port);
        Storage.setPassword(PASS);
    }

    public void setCamerasPosition(int[][] camerasPosition) {
        this.camerasPosition = camerasPosition;
        for (int i = 0; i < camerasPosition.length; i++) {
            camerasViewAnglesTangens[i][0] = (double) camerasPosition[i][0] / camerasPosition[i][1];
            camerasViewAnglesTangens[i][1] = (double) (camerasPosition[i][0] + 160) / camerasPosition[i][1];
        }
        savePasswordSaverToFile();
    }

    public String getAudioAddress() {
        return audioAddress;
    }

    public int getHideZoneIdentificationAccuracyCountOfFramesToAnalise() {
        return hideZoneIdentificationAccuracyCountOfFramesToAnalise;
    }

    public int getHideZoneIdentificationAccuracyComparePixels() {
        return hideZoneIdentificationAccuracyComparePixels;
    }

    public double[][] getCamerasViewAnglesTangens() {
        return camerasViewAnglesTangens;
    }
}
