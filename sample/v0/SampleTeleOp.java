package org.firstinspires.ftc.teamcode.sample.v0;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;
import ftc.evlib.driverstation.GamepadManager;
import ftc.evlib.hardware.motors.Motor;
import ftc.evlib.hardware.motors.Motors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * Sample TeleOp that uses some of the EVLib functionality
 */

public class SampleTeleOp extends OpMode {
    //GamepadManager adds edge detection to the gamepad buttons
    //and scaling to the joysticks
    private GamepadManager driver1, driver2;
    private Motor leftMotor, rightMotor;
    private Servo servo;

    @Override
    public void init() {
        leftMotor = Motors.motorWithoutEncoderForward(
                hardwareMap.dcMotor.get("leftMotor")
        );

        rightMotor = Motors.motorWithoutEncoderReversed(
                hardwareMap.dcMotor.get("rightMotor")
        );

        servo = hardwareMap.servo.get("servo");
    }

    @Override
    public void start() {
        Function scalingFunction = Functions.squared();
        driver1 = new GamepadManager(gamepad1, scalingFunction);
        driver2 = new GamepadManager(gamepad2, scalingFunction);
    }

    @Override
    public void loop() {
        leftMotor.setPower(driver1.left_stick_y.getValue());
        rightMotor.setPower(driver1.right_stick_y.getValue());

        //making use of the edge detection from the GamepadManager
        if (driver1.a.justPressed()) {
            servo.setPosition(1);
        }

        if (driver1.b.justPressed()) {
            servo.setPosition(0);
        }

    }
}
