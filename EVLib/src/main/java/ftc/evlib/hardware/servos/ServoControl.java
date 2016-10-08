package ftc.evlib.hardware.servos;

import java.util.Map;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * defines the interface for controlling the speed of a servo and using presets
 */
public interface ServoControl {
    double MAX_SPEED = 10000000;

    /**
     * @return the map of presets and their corresponding values
     */
    Map<Enum, Double> getPresets();

    /**
     * go using the position and speed stored in a servoCommand
     *
     * @param servoCommand the position and speed
     */
    void go(ServoCommand servoCommand);

    /**
     * go to a preset at maximum speed
     *
     * @param preset the preset to go to
     */
    void goToPreset(Enum preset);

    /**
     * go to a preset at any speed
     *
     * @param preset the preset to go to
     * @param speed  the speed to go at
     */
    void goToPreset(Enum preset, double speed);

    /**
     * go to a position at max speed
     *
     * @param position the position to go to
     */
    void setPosition(double position);

    /**
     * go to a position at any speed
     *
     * @param position the position to go to
     * @param speed    the speed to go at
     */
    void setPosition(double position, double speed);

    /**
     * @return the current position
     */
    double getCurrentPosition();

    /**
     * update the servo's position based on the speed
     *
     * @return whether or not the servo's movement is completed
     */
    boolean act();

    /**
     * @return whether or not the servo's movement is completed
     */
    boolean isDone();

    /**
     * @return the name of the servo
     */
    ServoName getName();
}
