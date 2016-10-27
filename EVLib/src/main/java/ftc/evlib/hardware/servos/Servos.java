package ftc.evlib.hardware.servos;

import java.util.Map;

import ftc.electronvolts.util.OptionsFile;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/1/16
 * <p>
 * stores multiple servos and coordinates updating them
 */
public class Servos {
    private final Map<ServoName, ServoControl> servoMap;

    public Servos(Map<ServoName, ServoControl> servoMap) {
        this.servoMap = servoMap;
    }

    /**
     * causes the servo positions to advance
     *
     * @return false if any servo is not done
     */
    public boolean servosAct() {
        boolean isDone = true;
        for (Map.Entry<ServoName, ServoControl> entry : servoMap.entrySet()) {
            ServoControl servoControl = entry.getValue();

            if (!servoControl.act()) {
                isDone = false;
            }
        }
        return isDone;
    }

    /**
     * @return false if any servo is not done
     */
    public boolean areServosDone() {
        for (Map.Entry<ServoName, ServoControl> entry : servoMap.entrySet()) {
            ServoControl servoControl = entry.getValue();

            if (!servoControl.isDone()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Store servo positions to a file
     *
     * @param filename the name of the file
     */
    public void storeServoPositions(String filename) {
        OptionsFile servoPos = new OptionsFile();
        for (Map.Entry<ServoName, ServoControl> entry : servoMap.entrySet()) {
            ServoName servoName = entry.getKey();
            ServoControl servoControl = entry.getValue();

            servoPos.add(servoName.getHardwareName(), servoControl.getCurrentPosition());
        }
        //servoPos.add("time", System.currentTimeMillis());
        servoPos.writeToFile(FileUtil.getFile(filename));
    }

    public Map<ServoName, ServoControl> getServoMap() {
        return servoMap;
    }
}
