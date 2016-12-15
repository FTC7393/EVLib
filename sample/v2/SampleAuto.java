package org.firstinspires.ftc.teamcode.sample.v2;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import ftc.electronvolts.statemachine.StateMachine;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.files.Logger;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.opmodes.AbstractAutoOp;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample autonomous that drives forward for 2 feet.
 * This one uses a custom builder
 */

@Autonomous(name = "SampleAuto V2")
public class SampleAuto extends AbstractAutoOp<SampleRobotCfg> {

    /**
     * defines all the possible states for the state machine
     * modify this to have whatever states you want
     */
    private enum S implements StateName {
        DRIVE,
        STOP
    }

    @Override
    public StateMachine buildStates() {
        //create a new builder for the states, starting with the DRIVE state
        //this time we are using the custom builder and passing it the drive motors from the robotCfg
        SampleStateMachineBuilder b = new SampleStateMachineBuilder(S.DRIVE, robotCfg.getTwoMotors());

        //define the DRIVE state to drive for 2 feet and move to the STOP state

        //the old code without the custom builder:
//        b.add(EVStates.drive(S.DRIVE, S.STOP, Distance.fromFeet(2), robotCfg.getTwoMotors(), 0.5));

        //the new code (more readable and less tedious to type):
        b.addDrive(S.DRIVE, S.STOP, Distance.fromFeet(2), 0.5);

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
    protected Logger createLogger() {
        //it will save logs in /FTC/logs/autonomous[....].csv on the robot controller phone
        return new Logger("autonomous", ".csv", ImmutableList.of(
                //each one of these is a column in the log file
                new Logger.Column("state", new InputExtractor<StateName>() {
                    @Override
                    public StateName getValue() {
                        return stateMachine.getCurrentStateName();
                    }
                }),
                new Logger.Column("leftMotor power", new InputExtractor<Double>() {
                    @Override
                    public Double getValue() {
                        return robotCfg.getTwoMotors().getValue(0);
                    }
                }),
                new Logger.Column("rightMotor power", new InputExtractor<Double>() {
                    @Override
                    public Double getValue() {
                        return robotCfg.getTwoMotors().getValue(1);
                    }
                })
        ));
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
