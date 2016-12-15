package org.firstinspires.ftc.teamcode.sample.v2;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.hardware.motors.TwoMotors;
import ftc.evlib.statemachine.EVStates;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample extension of the StateMachineBuilder.
 * This subclass adds one convenience method for autonomous opmodes.
 */

public class SampleStateMachineBuilder extends StateMachineBuilder {
    /**
     * The drive motors
     */
    private final TwoMotors twoMotors;

    /**
     * Create a SampleStateMachineBuilder, passing it the drive motors
     * The drive motors will be passed to the drive state every time
     *
     * @param firstStateName the state to start with
     * @param twoMotors      the robot's drive motors
     */
    public SampleStateMachineBuilder(StateName firstStateName, TwoMotors twoMotors) {
        super(firstStateName);
        this.twoMotors = twoMotors;
    }

    /**
     * convenience method for adding a drive state
     *
     * @param stateName     the name of the state
     * @param nextStateName the name of the state to go to after the drive is complete
     * @param distance      the distance to drive
     * @param velocity      the velocity to drive at
     */
    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity) {
        //add the drive state with the motors and speed from sampleRobotCfg
        add(EVStates.drive(stateName, nextStateName, distance, twoMotors, velocity));
    }
}
