package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/27/15
 * <p>
 * A subclass of NMotors that provides convenience methods for passing in 4 values.
 */
public class SixMotors extends NMotors {
    public SixMotors(Motor frontLeftMotor, Motor frontRightMotor, Motor middleLeftMotor, Motor middleRightMotor, Motor backLeftMotor, Motor backRightMotor, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
        super(ImmutableList.of(frontLeftMotor, frontRightMotor, middleLeftMotor, middleRightMotor, backLeftMotor, backRightMotor), useSpeedMode, stopBehavior);
    }

    public void runMotorsNormalized(double flValue, double frValue, double mlValue, double mrValue, double blValue, double brValue) {
        runMotorsNormalized(ImmutableList.of(flValue, frValue, mlValue, mrValue, blValue, brValue));
    }

    public void runMotors(double flValue, double frValue, double mlValue, double mrValue, double blValue, double brValue) {
        runMotors(ImmutableList.of(flValue, frValue, mlValue, mrValue, blValue, brValue));
    }
}
