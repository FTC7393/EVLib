package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

import ftc.electronvolts.util.units.Velocity;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/27/15
 * <p>
 * A subclass of NMotors that provides convenience methods for passing in 6 motor powers.
 *
 * @see NMotors
 */
public class SixMotors extends NMotors {
    public SixMotors(Motor frontLeftMotor, Motor frontRightMotor, Motor middleLeftMotor, Motor middleRightMotor, Motor backLeftMotor, Motor backRightMotor, boolean useSpeedMode, Velocity maxRobotSpeed) {
        super(ImmutableList.of(frontLeftMotor, frontRightMotor, middleLeftMotor, middleRightMotor, backLeftMotor, backRightMotor), useSpeedMode, maxRobotSpeed);
    }

    public void runMotorsNormalized(double flValue, double frValue, double mlValue, double mrValue, double blValue, double brValue) {
        runNormalized(ImmutableList.of(flValue, frValue, mlValue, mrValue, blValue, brValue));
    }

    public void runMotors(double flValue, double frValue, double mlValue, double mrValue, double blValue, double brValue) {
        run(ImmutableList.of(flValue, frValue, mlValue, mrValue, blValue, brValue));
    }
}
