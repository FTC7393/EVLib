package org.firstinspires.ftc.teamcode.sample.v1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import ftc.electronvolts.util.Distance;
import ftc.electronvolts.util.Time;
import ftc.electronvolts.util.Velocity;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.motors.Motor;
import ftc.evlib.hardware.motors.Motors;
import ftc.evlib.hardware.motors.TwoMotors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A basic configuration of a robot with 2 motors, and nothing else.
 * Its purpose is to make the same configuration available to multiple opmodes.
 */

public class SampleRobotCfg extends RobotCfg {
    //the speed of the robot at 100% power
    public static final Velocity MAX_SPEED = new Velocity(Distance.fromInches(50), Time.fromSeconds(5));

    // this will store the motors of the robot
    private final TwoMotors twoMotors;

    public SampleRobotCfg(HardwareMap hardwareMap) {
        super(hardwareMap);

        // create the twoMotors object
        twoMotors = new TwoMotors(
                //get the left and right motors and wrap it with the EVLib Motor interface
                Motors.motorWithoutEncoderForward(hardwareMap.dcMotor.get("leftMotor")),
                Motors.motorWithoutEncoderReversed(hardwareMap.dcMotor.get("rightMotor")),
                //true for speed mode, false for power mode
                false,
                //brake the motors when they are stopped
                Motor.StopBehavior.BRAKE
        );
    }

    // a getter for the motors
    public TwoMotors getTwoMotors() {
        return twoMotors;
    }

    @Override
    public void act() {

    }
}
