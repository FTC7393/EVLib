package ftc.evlib.hardware.control;

import ftc.electronvolts.util.units.Angle;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/19/16
 * <p>
 * Controls the rotation of a mecanum robot
 *
 * @see RotationControls
 * @see Angle
 */
public interface RotationControl {
    double DEFAULT_MAX_ANGULAR_SPEED = 0.5;
//    double DEFAULT_MAX_ANGULAR_SPEED = 0.8;

    /**
     * update the rotational velocity
     *
     * @return true if there were no problems
     */
    boolean act();

    /**
     * @return the rotational velocity
     */
    double getVelocityR();

    /**
     * accounts for the robot's rotation being off when translating
     *
     * @return correction to the translation direction
     */
    Angle getPolarDirectionCorrection();
}
