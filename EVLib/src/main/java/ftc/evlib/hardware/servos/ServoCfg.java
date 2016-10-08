package ftc.evlib.hardware.servos;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;
import java.util.Map;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/14/16
 * <p>
 * static methods that help build the servoMap
 */
public class ServoCfg {

    public static String getServoFilename(ServoName servoName) {
        return "servo_" + servoName + ".txt";
    }

    /**
     * Creates a "servoStartPresetMap" which tells what presets to start each servo at and can be
     * modified by the individual OpModes before being passed to createServoMap
     *
     * @param servoNames then list of servo names to be added
     * @return the created map of servo names and start presets
     */
    public static Map<ServoName, Enum> defaultServoStartPresetMap(ServoName[] servoNames) {
        Map<ServoName, Enum> servoStartPresetMap = new HashMap<>(); //create the map

        //loop through each ServoName
        for (ServoName servoName : servoNames) {
            servoStartPresetMap.put(servoName, servoName.getPresets()[0]); //add the servo and start preset
        }

        return servoStartPresetMap; //return the map
    }

    /**
     * create a map that links ServoName to ServoControl
     *
     * @param hardwareMap         retrieves the servos from here
     * @param servoStartPresetMap the map that assigns a start preset to each ServoName
     * @return the created servoMap
     */
    public static Map<ServoName, ServoControl> createServoMap(HardwareMap hardwareMap, Map<ServoName, Enum> servoStartPresetMap) {
        Map<ServoName, ServoControl> servoMap = new HashMap<>(); //create the map

        //loop through the start preset map
        for (Map.Entry<ServoName, Enum> entry : servoStartPresetMap.entrySet()) {
            ServoName servoName = entry.getKey();
            Enum preset = entry.getValue();

            //get the servo from the hardwareMap
            Servo servo = hardwareMap.servo.get(servoName.getHardwareName());

            //create a ServoControl from that servo
            ServoControl servoControl = new BasicServoControl(servo, servoName, preset);
            servoMap.put(servoName, servoControl); //add it to the map
        }

        return servoMap; //return the map
    }
}
