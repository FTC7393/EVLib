package org.firstinspires.ftc.teamcode.sample.v3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ftc.electronvolts.statemachine.StateMachine;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.opmodes.AbstractAutoOp;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample autonomous that:
 * 1. Initializes the servos
 * 2. Drives forward 2 feet
 * 3. Turns a servo arm
 */

@Autonomous(name="SampleAuto V3")
public class SampleAuto extends AbstractAutoOp<SampleRobotCfg> {

    /**
     * defines all the possible states for the state machine
     * modify this to have whatever states you want
     */
    private enum S implements StateName {
        SERVO_INIT,
        DRIVE,
        SERVO_ARM,
        STOP
    }

    @Override
    public StateMachine buildStates() {
        //create a new builder for the states, starting with the SERVO_INIT state
        //we are using the custom builder and passing it the robotCfg
        SampleStateMachineBuilder b = new SampleStateMachineBuilder(S.SERVO_INIT, robotCfg);

        //add the servo initialization state
        b.addServoInit(S.SERVO_INIT, S.DRIVE);

        //define the DRIVE state to drive for 2 feet and move to the STOP state
        b.addDrive(S.DRIVE, S.SERVO_ARM, Distance.fromFeet(2), 0.5);

        //add the servo turn state
        b.addServo(S.SERVO_ARM, S.STOP, SampleRobotCfg.SampleServoName.ARM_SERVO, SampleRobotCfg.ArmServoPresets.RIGHT, true);

        //define the STOP state to be empty (and never exit) so the state machine will stop
        b.addStop(S.STOP);

        //build and return the StateMachine
        return b.build();
    }

    @Override
    protected SampleRobotCfg createRobotCfg() {
        return new SampleRobotCfg(hardwareMap);
    }

    @Override
    protected void setup_act() {

    }

    @Override
    protected void go() {

    }

    @Override
    protected void act() {

    }

    @Override
    protected void end() {

    }
}
