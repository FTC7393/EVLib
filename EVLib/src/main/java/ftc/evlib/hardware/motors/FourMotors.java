package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/27/15
 * <p>
 * An subclass of NMotorRobot that provides convenience methods for passing in 4 values.
 */
public class FourMotors extends NMotors {

    //This constructor is private so you cannot pass in a list of motors with an unknown length
//    private FourMotors(List<Motor> motors, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
//        super(motors, useSpeedMode, stopBehavior);
//    }

    public FourMotors(Motor frontLeftMotor, Motor frontRightMotor, Motor backLeftMotor, Motor backRightMotor, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
        super(ImmutableList.of(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor), useSpeedMode, stopBehavior);
    }

    public void runMotorsNormalized(double flValue, double frValue, double blValue, double brValue) {
        runMotorsNormalized(ImmutableList.of(flValue, frValue, blValue, brValue));
    }

    public void runMotors(double flValue, double frValue, double blValue, double brValue) {
        runMotors(ImmutableList.of(flValue, frValue, blValue, brValue));
    }
}
