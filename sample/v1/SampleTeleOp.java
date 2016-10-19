package org.firstinspires.ftc.teamcode.sample.v1;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;
import ftc.evlib.opmodes.AbstractTeleOp;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A sample TeleOp program that will allow you to control 2 motors with the left and right joysticks
 */

@TeleOp(name="SampleTeleOp V1")
public class SampleTeleOp extends AbstractTeleOp<SampleRobotCfg> {
    @Override
    protected Function getJoystickScalingFunction() {
        //use a squared function for the joystick scaling to allow fine control
        return Functions.squared();
//        return Functions.cubed();
//        return Functions.none();
    }

    @Override
    protected SampleRobotCfg createHardwareCfg() {
        //create a SampleRobotCfg
        return new SampleRobotCfg(hardwareMap);
    }

    @Override
    protected void setup() {

    }

    @Override
    protected void setup_loop() {

    }

    @Override
    protected void go() {

    }

    @Override
    protected void act() {
        //set the motor powers to the joystick values
        robotCfg.getTwoMotors().runMotors(
                driver1.left_stick_y.getValue(),
                driver1.right_stick_y.getValue()
        );
    }

    @Override
    protected void end() {

    }
}
