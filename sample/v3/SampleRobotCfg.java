package org.firstinspires.ftc.teamcode.sample.v3;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Map;

import ftc.electronvolts.util.units.Distance;
import ftc.electronvolts.util.units.Time;
import ftc.electronvolts.util.units.Velocity;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.motors.Motor;
import ftc.evlib.hardware.motors.Motors;
import ftc.evlib.hardware.motors.TwoMotors;
import ftc.evlib.hardware.servos.ServoCfg;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/18/16
 *
 * A more complicated configuration of a robot with 2 motors and 2 servos.
 */

public class SampleRobotCfg extends RobotCfg {
    /**
     * the speed of the robot at 100% power
     * you should replace this with a measured value
     */
    public static final Velocity MAX_SPEED = new Velocity(Distance.fromInches(50), Time.fromSeconds(5));

    /**
     * the drive motors of the robot
     */
    private final TwoMotors twoMotors;

    /**
     * stores all the servos of the robot
     */
    private final Servos servos;

    /**
     * all the possible values for the arm servo
     */
    public enum ArmServoPresets {
        LEFT,
        MIDDLE,
        RIGHT
    }

    /**
     * all the possible values for the leg servo
     */
    public enum LegServoPresets {
        DOWN,
        MIDDLE,
        UP
    }

    /**
     * defines all the servos on the robot
     */
    public enum SampleServoName implements ServoName {
        //enum name("hardware name", preset enum.values()),
        ARM_SERVO("armServo", ArmServoPresets.values()),
        LEG_SERVO("legServo", LegServoPresets.values());

        private final String hardwareName;
        private final Enum[] presets;

        SampleServoName(String hardwareName, Enum[] presets) {
            this.hardwareName = hardwareName;
            this.presets = presets;
        }

        @Override
        public String getHardwareName() {
            return hardwareName;
        }

        @Override
        public Enum[] getPresets() {
            return presets;
        }
    }

    /**
     * Create the SampleRobotCfg with the default servo starting positions
     *
     * @param hardwareMap the hardwareMap from the opmode
     */
    public SampleRobotCfg(HardwareMap hardwareMap) {
        this(hardwareMap, ServoCfg.defaultServoStartPresetMap(SampleServoName.values()));
    }

    /**
     * Create the SampleRobotCfg with custom servo starting positions
     *
     * @param hardwareMap the hardwareMap from the opmode
     * @param servoStartPresetMap the custom servo starting positions
     */
    public SampleRobotCfg(HardwareMap hardwareMap, Map<ServoName, Enum> servoStartPresetMap) {
        super(hardwareMap);

        // create the twoMotors object
        twoMotors = new TwoMotors(
                //get the left and right motors and wrap it with the EVLib Motor interface
                Motors.motorWithoutEncoderForward(hardwareMap.dcMotor.get("leftMotor")),
                Motors.motorWithoutEncoderReversed(hardwareMap.dcMotor.get("rightMotor")),
                //true for speed mode, false for power mode
                false,
                //brake the motors when they are stopped
                Motor.StopBehavior.BRAKE,
                MAX_SPEED
        );

        servos = new Servos(ServoCfg.createServoMap(hardwareMap, servoStartPresetMap));
    }

    /**
     * gives the opmodes access to the drive motors
     * @return the drive motors
     */
    public TwoMotors getTwoMotors() {
        return twoMotors;
    }

    /**
     * gives the opmodes access to the servos
     * @return the servos
     */
    @Override
    public Servos getServos() {
        return servos;
    }

    @Override
    public void act() {

    }

    @Override
    public void stop() {

    }
}
