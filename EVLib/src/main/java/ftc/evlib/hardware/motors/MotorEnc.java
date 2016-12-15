package ftc.evlib.hardware.motors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * Wrapper class for DcMotor if the motor has an encoder
 * This interface has all the non-encoder methods from the Motor interface plus the ones shown here.
 * It can be passed in where a non-encoder Motor interface is needed.
 *
 * @see Motor
 * @see Motors
 */
public interface MotorEnc extends Motor {
    /**
     * A PID on the motor controller uses the encoder to regulate the speed of the motor.
     *
     * @param speed value to set the speed to
     */
    void setSpeed(double speed);

    /**
     * A PID on the motor controller uses the encoder to turn the motor to any encoder position.
     *
     * @param encoderTarget      position in encoder ticks to rotate to
     * @param maxCorrectionPower the max power to run the motor at when turning to the position
     */
    void setPosition(int encoderTarget, double maxCorrectionPower);

    /**
     * Set the encoder zero point to the current encoder value
     */
    void resetEncoder();

    /**
     * Get the encoder position relative to the zero value
     *
     * @return the encoder position
     */
    int getEncoderPosition();

}
