package org.firstinspires.ftc.teamcode.sample.v3;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.hardware.motors.TwoMotors;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.statemachine.EVStates;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample extension of the StateMachineBuilder.
 * This subclass adds convenience methods for autonomous opmodes.
 */

public class SampleStateMachineBuilder extends StateMachineBuilder {
    /**
     * The drive motors
     */
    private final TwoMotors twoMotors;

    /**
     * The robot's servos
     */
    private final Servos servos;

    /**
     * Create a SampleStateMachineBuilder, passing it the drive motors and servos
     * The drive motors will be passed to the drive state every time
     *
     * @param firstStateName the state to start with
     * @param twoMotors      the robot's drive motors
     * @param servos         the robot's servos
     */
    public SampleStateMachineBuilder(StateName firstStateName, TwoMotors twoMotors, Servos servos) {
        super(firstStateName);
        this.twoMotors = twoMotors;
        this.servos = servos;
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

    /**
     * add a servo init state that sets all the servos to their starting positions
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to after the servos are initialized
     */
    public void addServoInit(StateName stateName, StateName nextStateName) {
        add(EVStates.servoInit(stateName, nextStateName, servos));
    }

    /**
     * turn a servo to a preset
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to after the servo is done turning
     * @param servoName     the name of the servo as defined in SampleRobotCfg.ServoName
     * @param servoPreset   the servo preset defined in SampleRobotCfg.ArmServoPresets and SampleRobotCfg.LegServoPresets
     * @param speed         the speed to run the servo at
     * @param waitForDone   whether or not to wait for the servo to turn to move to the next state
     */
    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum servoPreset, double speed, boolean waitForDone) {
        //get the servo
        ServoControl servoControl = servos.getServoMap().get(servoName);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, nextStateName, servoControl, servoPreset, speed, waitForDone));
    }

    /**
     * turn a servo to a preset at max speed
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to after the servo is done turning
     * @param servoName     the name of the servo as defined in SampleRobotCfg.ServoName
     * @param servoPreset   the servo preset defined in SampleRobotCfg.ArmServoPresets and SampleRobotCfg.LegServoPresets
     * @param waitForDone   whether or not to wait for the servo to turn to move to the next state
     */
    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum servoPreset, boolean waitForDone) {
        //get the servo
        ServoControl servoControl = servos.getServoMap().get(servoName);
        //add the state that will run the command
        add(EVStates.servoTurn(stateName, nextStateName, servoControl, servoPreset, waitForDone));
    }


}
