package org.firstinspires.ftc.teamcode.sample.v2;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.Distance;
import ftc.evlib.statemachine.EVStates;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample extension of the StateMachineBuilder.
 * This subclass adds one convenience method for any opmodes that use the SampleRobotConfig.
 */

public class SampleStateMachineBuilder extends StateMachineBuilder {
    private final SampleRobotCfg sampleRobotCfg;

    public SampleStateMachineBuilder(StateName firstStateName, SampleRobotCfg sampleRobotCfg) {
        super(firstStateName);
        this.sampleRobotCfg = sampleRobotCfg;
    }

    //convenience method for adding a drive state
    public void addDrive(StateName stateName, Distance distance, StateName nextStateName, double velocity) {
        //add the drive state with the motors and speed from sampleRobotCfg
        add(EVStates.drive(stateName, distance, nextStateName, SampleRobotCfg.MAX_SPEED, sampleRobotCfg.getTwoMotors(), velocity));
    }


}
