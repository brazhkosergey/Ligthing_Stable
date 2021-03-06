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
    private double[] distancesToSarcophagus;

    private int hideZoneIdentificationAccuracyComparePixels;
    int countShowFrames;

    private AddressSaver() {
        lightSensitivity = 190;
        countShowFrames = 15;
        camerasPosition = new int[4][];
//        camerasPosition[0] = new int[]{74, 91, 91};
//        camerasPosition[1] = new int[]{91, 137, 100};
//        camerasPosition[2] = new int[]{12, 147, 100};
//        camerasPosition[3] = new int[]{48, 139, 99};
        camerasPosition[0] = new int[3];
        camerasPosition[1] = new int[3];
        camerasPosition[2] = new int[3];
        camerasPosition[3] = new int[3];
        camerasViewAnglesTangens = new double[4][];
        distancesToSarcophagus = new double[4];
        setCamerasViewAnglesTangens();
        setCamerasDistanceToViewArc();
        camerasIdentificationInformation = new String[8][];
        for (int i = 0; i < 8; i++) {
            camerasIdentificationInformation[i] = new String[3];
        }
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
                            int lightSensitivity, int opacity, int port, String path
            , int hideZoneIdentificationAccuracyComparePixels, int countShowFrames) {

        this.changeWhitePercent = changeWhitePercent;
        this.lightSensitivity = lightSensitivity;
        this.opacity = opacity;
        this.timeToSave = timeToSave;
        this.programLightCatchEnable = programLightCatchEnable;
        this.port = port;
        this.path = path;
        this.hideZoneIdentificationAccuracyComparePixels = hideZoneIdentificationAccuracyComparePixels;
        this.countShowFrames = countShowFrames;
        savePasswordSaverToFile();
        setSetting();
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
        } catch (Exception e) {
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
        Storage.setShowFramesPercent(countShowFrames);
    }

    public void setCamerasPosition(int[][] camerasPosition) {
        this.camerasPosition = camerasPosition;
        setCamerasViewAnglesTangens();
        setCamerasDistanceToViewArc();
        savePasswordSaverToFile();
    }

    private void setCamerasViewAnglesTangens() {
        for (int i = 0; i < camerasPosition.length; i++) {
            if (camerasViewAnglesTangens[i] == null) {
                camerasViewAnglesTangens[i] = new double[2];
            }
            camerasViewAnglesTangens[i][0] = (double) camerasPosition[i][0] / (camerasPosition[i][1] + camerasPosition[i][2]);
            camerasViewAnglesTangens[i][1] = (double) (camerasPosition[i][0] + 164) / (camerasPosition[i][1] + camerasPosition[i][2]);
        }
    }

    private void setCamerasDistanceToViewArc() {
        for (int groupNumber = 0; groupNumber < 4; groupNumber++) {
            distancesToSarcophagus[groupNumber] = (double) getCamerasPosition()[groupNumber][0] / Math.sin(Math.atan(getCamerasViewAnglesTangens()[groupNumber][0]));
        }
    }

    public String getAudioAddress() {
        return audioAddress;
    }

    public int getHideZoneIdentificationAccuracyComparePixels() {
        return hideZoneIdentificationAccuracyComparePixels;
    }

    public double[][] getCamerasViewAnglesTangens() {
        return camerasViewAnglesTangens;
    }

    public double[] getDistancesToSarcophagus() {
        return distancesToSarcophagus;
    }
}
