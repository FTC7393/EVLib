package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * A subclass of NMotors that provides convenience methods for passing in 3 values.
 */
public class ThreeMotors extends NMotors {
    public ThreeMotors(Motor motor1, Motor motor2, Motor motor3, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
        super(ImmutableList.of(motor1, motor2, motor3), useSpeedMode, stopBehavior);
    }

    public void runMotorsNormalized(double value1, double value2, double value3) {
        runMotorsNormalized(ImmutableList.of(value1, value2, value3));
    }

    public void runMotors(double value1, double value2, double value3) {
        runMotors(ImmutableList.of(value1, value2, value3));
    }
}
