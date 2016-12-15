package ftc.evlib.opmodes;

import java.util.ArrayList;
import java.util.List;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;
import ftc.electronvolts.util.Utility;
import ftc.electronvolts.util.files.Logger;
import ftc.electronvolts.util.files.OptionsFile;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.ServoCfg;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.util.EVConverters;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/30/16
 *
 * extends AbstractTeleOp and adds tuning of servo presets with the joysticks.
 *
 * Subclasses of this are very simple since this does most of the work.
 *
 * It allows you to change your servo presets without changing the code and re-deploying it to the
 * phone. This means that you can swap out a servo and re-tune it without having to go into the
 * program and fix magic numbers. Note:  It only works if you use presets everywhere instead of
 * hardcoded values.
 *
 * How to use for your robot:
 * Create a subclass of this (AbstractServoTuneOp).
 * return a new instance of your RobotCfg (it has the servos) in createRobotCfg().
 *
 * Subclass example:
 *
 * <code>
 *
 * \@TeleOp(name = "MyRobot ServoTuneOp")
 * public class MyRobotServoTuneOp extends AbstractServoTuneOp {
 * \@Override protected RobotCfg createRobotCfg() {
 * return new MyRobotCfg(hardwareMap);
 * }
 * }
 * </code>
 *
 * How to operate:
 * Use the dpad up and down to cycle through all the servos
 * Use the dpad left and right to move through the presets for that servo.
 * Use the left and right joystick y values to change the servo position.
 * Press start to save the current preset of the current servo to the current value.
 *
 * The presets are saved in files that are retrieved when you run other opmodes to find the value of each preset.
 *
 * @see ServoControl
 * @see ServoCfg
 */
public abstract class AbstractServoTuneOp extends AbstractTeleOp<RobotCfg> {
    /**
     * The index of the servo in the list
     */
    private int servoIndex = 0;

    /**
     * The index of the preset for the current servo
     */
    private int presetIndex = 0;

    /**
     * records whether or not a new servo has been selected
     */
    private boolean servoIndexChanged = true;

    /**
     * records whether or not a new servo preset has been selected
     */
    private boolean servoPresetIndexChanged = true;

    /**
     * The list of current positions for each servo
     */
    private final List<Double> servoPositions = new ArrayList<>();

    /**
     * The list of servo names
     */
    private List<ServoName> servoNames;

    /**
     * The list of preset names for the current servo
     */
    private List<Enum> presetNames;

    /**
     * The list of preset values for the current servo
     */
    private List<Double> presetValues;

    /**
     * The current servo
     */
    private ServoControl servo;

    /**
     * @return no joystick scaling
     */
    @Override
    protected Function getJoystickScalingFunction() {
        return Functions.none();
    }

    /**
     * @return no match timer
     */
    @Override
    public Time getMatchTime() {
        return null;
    }

    /**
     * @return no logging
     */
    @Override
    protected Logger createLogger() {
        return null;
    }

    @Override
    protected void setup() {
        //get a list of servo names from the RobotCfg
        servoNames = robotCfg.getServos().getServoNames();

        //add servo positions to be the same length as servoNames
        for (ServoName ignored : servoNames) {
            servoPositions.add(0.5);
        }
    }

    @Override
    protected void setup_act() {

    }

    @Override
    protected void go() {

    }

    @Override
    protected void act() {


        //if dpad up is pressed
        if (driver1.dpad_up.justPressed() || driver2.dpad_up.justPressed()) {
            servoIndex += 1; //move to the next servo
            //wrap around if the index is too large
            if (servoIndex > servoNames.size() - 1) servoIndex = 0;
            servoIndexChanged = true; //signal that the index changed
        }

        //if dpad down is pressed
        if (driver1.dpad_down.justPressed() || driver2.dpad_down.justPressed()) {
            servoIndex -= 1; //move to the previous servo
            //wrap around if the index is too small
            if (servoIndex < 0) servoIndex = servoNames.size() - 1;
            servoIndexChanged = true; //signal that the index changed
        }

        //if a different servo was selected
        if (servoIndexChanged) {
            servoIndexChanged = false;

            servo = robotCfg.getServo(servoNames.get(servoIndex));//get the servo
            presetNames = new ArrayList<>(servo.getPresets().keySet()); //get the preset names from the servo
            presetValues = new ArrayList<>(servo.getPresets().values()); //get the presets from the servo

            presetIndex = 0; //start at the first preset for the new servo
            servoPresetIndexChanged = true; //signal to reload the servo preset
        }

        //get the servo position
        double servoPosition = servoPositions.get(servoIndex);

        //if the dpad left was just pressed
        if (driver1.dpad_left.justPressed() || driver2.dpad_left.justPressed()) {
            presetIndex -= 1; //select the previous servo preset
            //wrap around if the index is too small
            if (presetIndex < 0) presetIndex = presetValues.size() - 1;
            servoPresetIndexChanged = true; //signal that the index changed
        }

        //if the dpad right was just pressed
        if (driver1.dpad_right.justPressed() || driver2.dpad_right.justPressed()) {
            presetIndex += 1; //select the next servo preset
            //wrap around if the index is too large
            if (presetIndex > presetValues.size() - 1) presetIndex = 0;
            servoPresetIndexChanged = true; //signal that the index changed
        }

        //is the servo preset index changed
        if (servoPresetIndexChanged) {
            servoPresetIndexChanged = false;
            servoPosition = presetValues.get(presetIndex); //set the servo to the preset position
        }

        telemetry.addData("Press start to set the current preset to the current value", "");
        //if start is pressed, save the current preset to a file
        if (driver1.start.justPressed() || driver2.start.justPressed()) {
            //set the current selected preset to the current servo position
            servo.getPresets().put(presetNames.get(presetIndex), servoPosition);
            presetValues.set(presetIndex, servoPosition);

            OptionsFile optionsFile = new OptionsFile(EVConverters.getInstance()); //create an OptionsFile

            //put the preset names and presets into the OptionsFile
            for (int i = 0; i < presetNames.size(); i++) {
                optionsFile.set(presetNames.get(i).name(), presetValues.get(i).toString());
            }

            optionsFile.writeToFile(ServoCfg.getServoFile(servoNames.get(servoIndex))); //store the OptionsFile to a file
        }

        //modify the servo position using the joysticks
        servoPosition += 2e-4 * matchTimer.getDeltaTime() * (driver1.left_stick_y.getValue() + 0.1 * driver1.right_stick_y.getValue() + driver2.left_stick_y.getValue() + 0.1 * driver2.right_stick_y.getValue());

        //limit the position
        servoPosition = Utility.servoLimit(servoPosition);

        //set the servo to the position
        servo.setPosition(servoPosition);

        //store the position
        servoPositions.set(servoIndex, servoPosition);

        //display telemetry about the servo
        telemetry.addData("Servo Name", servoNames.get(servoIndex));
        telemetry.addData("Servo Preset Name", presetNames.get(presetIndex));
        telemetry.addData("Servo Preset Value", servoPosition);
    }

    @Override
    protected void end() {

    }
}
