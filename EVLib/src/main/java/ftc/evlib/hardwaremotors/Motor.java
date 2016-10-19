package ftc.evlib.hardware.motors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Wrapper class for the DcMotor.
 * This represents the functions a motor without an encoder can do.
 */
public interface Motor {
    /**
     * Control the motor's raw voltage
     *
     * @param power value to set the power to
     */
    void setPower(double power);

    /**
     * Tells the mode the motor is in which is determined by the last command to the motor.
     *
     * @return the current mode
     */
    MotorMode getMode();

    /**
     * Tell the motor to brake or float when it stops
     *
     * @param stopBehavior what to do when the motor is stopped
     */
    void setStopBehavior(StopBehavior stopBehavior);

    enum MotorMode {
        POWER,
        SPEED,
        POSITION
    }

    enum StopBehavior {
        BRAKE,
        FLOAT
    }
}
