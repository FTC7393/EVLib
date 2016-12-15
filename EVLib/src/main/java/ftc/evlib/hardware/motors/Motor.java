package ftc.evlib.hardware.motors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Wrapper class for the DcMotor.
 * This represents the functions a motor without an encoder can do.
 *
 * @see MotorEnc
 * @see Motors
 */
public interface Motor {
    /**
     * The different modes the motor can be in
     */
    enum Mode {
        POWER, //directly control the power
        SPEED, //enable a feedback loop to correct speed (requires encoders)
        POSITION //turn the motor to a certain encoder position (requires encoders)
    }

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
    Mode getMode();

    /**
     * Sends motor commands to the motor controller
     */
    void update();
}
