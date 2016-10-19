package org.firstinspires.ftc.teamcode.sample.v3;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.opmodes.AbstractServoTuneOp;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * This opmode is very short since the superclass, AbstractServoTuneOp does most of the work. It
 * allows you to change your servo presets without changing the code and re-deploying it to the
 * phone. This means that you can swap out a servo and re-tune it without having to go into the
 * program and fix magic numbers. Note:  It only works if you use presets everywhere instead of
 * hardcoded values.
 *
 * How to use:
 * Select this opmode from the TeleOp menu and run it.
 * Use the dpad up and down to cycle through all the servos
 * Use the dpad left and right to move through the presets for that servo.
 * Press start to save the current preset of the current servo to the current value.
 *
 * The presets are saved in files that are retrieved when you run other opmodes to find the value of each preset.
 *
 */

@TeleOp(name="SampleServoTuneOp")
public class SampleServoTuneOp extends AbstractServoTuneOp {
    @Override
    protected RobotCfg createHardwareCfg() {
        //create a new SampleRobotConfig and return it.
        //the superclass will extract the servos and do the rest.
        return new SampleRobotCfg(hardwareMap);
    }
}
