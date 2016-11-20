package ftc.evlib.hardware.motors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ftc.electronvolts.util.Utility;
import ftc.electronvolts.util.units.Velocity;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p/>
 * A general controller for a collection of N motors.
 * Knows how to run the motors given a list of Doubles.
 * Can normalize the powers/speeds before running if requested.
 * Subclasses can have fixed numbers of motors
 *
 * @see Motor
 * @see MotorEnc
 * @see TwoMotors
 * @see FourMotors
 */
public class NMotors {
    /**
     * the list of motors that are grouped
     */
    private final List<Motor> motors;

    /**
     * whether or not to use speed mode on the motors
     */
    private final boolean useSpeedMode;

    /**
     * the measured maximum speed of the robot
     */
    private final Velocity maxRobotSpeed;

    /**
     * @param motors        the list of motors
     * @param useSpeedMode  true if encoder-regulated speed mode is desired
     * @param stopBehavior  what to do when the power/speed is 0
     * @param maxRobotSpeed the measured maximum speed of the robot
     */
    public NMotors(List<Motor> motors, boolean useSpeedMode, Motor.StopBehavior stopBehavior, Velocity maxRobotSpeed) {
        //if using speed mode, the motors need to have encoders
        if (useSpeedMode) {
            for (int i = 0; i < motors.size(); i++) {
                if (!(motors.get(i) instanceof MotorEnc)) {
                    throw new IllegalArgumentException("Argument 'motors' must be of type List<MotorEnc> if speed mode is to be used.");
                }
            }
        }
        this.motors = motors;
        this.useSpeedMode = useSpeedMode;
        this.maxRobotSpeed = maxRobotSpeed.abs();
        setStopBehavior(stopBehavior);
    }

    /**
     * @return the measured maximum speed of the robot
     */
    public Velocity getMaxRobotSpeed() {
        return maxRobotSpeed;
    }

    /**
     * Allows different stop behaviors for different operations
     *
     * @param stopBehavior what to do when the power/speed is 0
     */
    public void setStopBehavior(Motor.StopBehavior stopBehavior) {
        //loop through and set the stop behavior of each motor
        for (Motor motor : motors) {
            motor.setStopBehavior(stopBehavior);
        }
    }

    /**
     * scale all the motor powers if any one power is above the maximum of 1
     *
     * @param values the powers/speeds to be scaled and then run
     */
    public void runMotorsNormalized(List<Double> values) {
        if (values.size() != motors.size()) {
            throw new IllegalArgumentException("Argument 'values' must have the same length as the number of motors.");
        }

        //if the inputs are too high, scale them
        double highest = 0;

        //find the magnitude of the number with the highest magnitude
        for (double n : values) {
            if (Math.abs(n) > highest) {
                highest = Math.abs(n);
            }
        }

        if (highest < 1) { //only normalize if the values are too high
            highest = 1;
        }

        //rescale the powers by the highest value
        List<Double> valuesScaled = new ArrayList<>();
        for (double power : values) {
            valuesScaled.add(power / highest);
        }

        runMotors(valuesScaled);
    }

    /**
     * run the motors with raw power/speed
     *
     * @param values the raw values to send to the motors
     */
    public void runMotors(List<Double> values) {
        if (values.size() != motors.size()) {
            throw new IllegalArgumentException("Argument 'values' must have the same length as the number of motors.");
        }

        //set the motor powers/speeds of each motor
        for (int i = 0; i < motors.size(); i++) {
            if (useSpeedMode) {
                ((MotorEnc) motors.get(i)).setSpeed(Utility.motorLimit(values.get(i)));
            } else {
                motors.get(i).setPower(Utility.motorLimit(values.get(i)));
            }
        }
    }

    /**
     * stop all the motors
     */
    public void stopMotors() {
        //create a list of the same length as motors filled with zeroes
        runMotors(new ArrayList<>(Collections.nCopies(motors.size(), 0.0)));
    }
}
