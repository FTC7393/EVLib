package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

import ftc.electronvolts.util.units.Velocity;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * A subclass of NMotors that provides convenience methods for passing in 1 motor power.
 *
 * @see NMotors
 */
public class OneMotors extends NMotors {
    public OneMotors(Motor motor, boolean useSpeedMode, Velocity maxRobotSpeed) {
        super(ImmutableList.of(motor), useSpeedMode, maxRobotSpeed);
    }

    public void runMotorsNormalized(double value) {
        runNormalized(ImmutableList.of(value));
    }

    public void runMotors(double value) {
        run(ImmutableList.of(value));
    }
}
