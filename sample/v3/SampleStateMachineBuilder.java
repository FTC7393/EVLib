package org.firstinspires.ftc.teamcode.sample.v3;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.units.Distance;
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

    /**
     * add a servo init state that sets all the servos to their starting positions
     *
     * @param stateName the name of the state
     * @param nextStateName the state to go to after the servos are initialized
     */
    public void addServoInit(StateName stateName, StateName nextStateName) {
        add(EVStates.servoInit(stateName, nextStateName, sampleRobotCfg.getServos()));
    }

    /**
     * turn a servo to a preset
     *
     * @param stateName the name of the state
     * @param nextStateName the state to go to after the servo is done turning
     * @param servoName the name of the servo as defined in SampleRobotCfg.ServoName
     * @param servoPreset the servo preset defined in SampleRobotCfg.ArmServoPresets and SampleRobotCfg.LegServoPresets
     * @param speed the speed to run the servo at
     * @param waitForDone wether or not to wait for the servo to turn to move to the next state
     */
    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum servoPreset, double speed, boolean waitForDone) {
        //get the servo
        ServoControl servoControl = sampleRobotCfg.getServo(servoName);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, nextStateName, servoControl, servoPreset, speed, waitForDone));
    }

    /**
     * turn a servo to a preset at max speed
     *
     * @param stateName the name of the state
     * @param nextStateName the state to go to after the servo is done turning
     * @param servoName the name of the servo as defined in SampleRobotCfg.ServoName
     * @param servoPreset the servo preset defined in SampleRobotCfg.ArmServoPresets and SampleRobotCfg.LegServoPresets
     * @param waitForDone wether or not to wait for the servo to turn to move to the next state
     */
    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum servoPreset, boolean waitForDone) {
        //get the servo
        ServoControl servoControl = sampleRobotCfg.getServo(servoName);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, nextStateName, servoControl, servoPreset, waitForDone));
    }


}
