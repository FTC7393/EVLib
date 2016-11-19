package ftc.evlib.hardware.servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * this interface is meant to be implemented by an enum that defines each servo as an enum item
 */
public interface ServoName {
    /**
     * @return the name as defined in the hardwareMap
     * @see com.qualcomm.robotcore.hardware.HardwareMap
     */
    String getHardwareName();

    /**
     * @return the names of the presets for this servo
     */
    Enum[] getPresets();

    /**
     * @return the name for telemetry purposes
     */
    String name();
}
