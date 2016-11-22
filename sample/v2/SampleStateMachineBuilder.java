package org.firstinspires.ftc.teamcode.sample.v2;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.statemachine.EVStates;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample extension of the StateMachineBuilder.
 * This subclass adds one convenience method for any opmodes that use the SampleRobotConfig.
 */

public class SampleStateMachineBuilder extends StateMachineBuilder {
    /**
     * The SampleRobotCfg to get the drive motors from
     */
    private final SampleRobotCfg sampleRobotCfg;

    /**
     * Create a SampleStateMachineBuilder, passing it the SampleRobotCfg
     * The SampleRobotCfg's drive motors will be passed to the drive state every time
     *
     * @param firstStateName the state to start with
     * @param sampleRobotCfg the robot's configuration
     */
    public SampleStateMachineBuilder(StateName firstStateName, SampleRobotCfg sampleRobotCfg) {
        super(firstStateName);
        this.sampleRobotCfg = sampleRobotCfg;
    }

    /**
     * convenience method for adding a drive state
     *
     * @param stateName the name of the state
     * @param nextStateName the name of the state to go to after the drive is complete
     * @param distance the distance to drive
     * @param velocity the velocity to drive at
     */
    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity) {
        //add the drive state with the motors and speed from sampleRobotCfg
        add(EVStates.drive(stateName, nextStateName, distance, SampleRobotCfg.MAX_SPEED, sampleRobotCfg.getTwoMotors(), velocity));
    }


}
