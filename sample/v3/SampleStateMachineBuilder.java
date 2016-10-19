package org.firstinspires.ftc.teamcode.sample.v3;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.Distance;
import ftc.evlib.hardware.servos.ServoCommand;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.statemachine.EVStates;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample extension of the StateMachineBuilder.
 * This subclass adds convenience methods for any opmodes that use the SampleRobotConfig.
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

    //add a servo init state that sets all the servos to their starting positions
    public void addServoInit(StateName stateName, StateName nextStateName) {
        add(EVStates.servoInit(stateName, sampleRobotCfg.getServos(), nextStateName));
    }

    //use a ServoCommand
    public void addServo(StateName stateName, ServoCommand servoCommand, boolean waitForDone, StateName nextStateName) {
        add(EVStates.servoTurn(stateName, servoCommand, waitForDone, nextStateName));
    }

    //turn a servo to a preset
    public void addServo(StateName stateName, ServoName servoName, Enum servoPreset, double speed, boolean waitForDone, StateName nextStateName) {
        //get the servo
        ServoControl servoControl = sampleRobotCfg.getServo(servoName);
        //create the command
        ServoCommand servoCommand = new ServoCommand(servoPreset, servoControl, speed);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, servoCommand, waitForDone, nextStateName));
    }

    //turn a servo to a preset at max speed
    public void addServo(StateName stateName, ServoName servoName, Enum servoPreset, boolean waitForDone, StateName nextStateName) {
        //get the servo
        ServoControl servoControl = sampleRobotCfg.getServo(servoName);
        //create the command
        ServoCommand servoCommand = new ServoCommand(servoPreset, servoControl);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, servoCommand, waitForDone, nextStateName));
    }


}
