package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * A subclass of NMotors that provides convenience methods for passing in 2 values.
 */
public class TwoMotors extends NMotors {
    public TwoMotors(Motor leftMotor, Motor rightMotor, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
        super(ImmutableList.of(leftMotor, rightMotor), useSpeedMode, stopBehavior);
    }

    public void runMotorsNormalized(double leftValue, double rightValue) {
        runMotorsNormalized(ImmutableList.of(leftValue, rightValue));
    }

    public void runMotors(double leftValue, double rightValue) {
        runMotors(ImmutableList.of(leftValue, rightValue));
    }
}
