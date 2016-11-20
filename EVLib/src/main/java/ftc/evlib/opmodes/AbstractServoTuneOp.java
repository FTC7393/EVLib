package ftc.evlib.opmodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;
import ftc.electronvolts.util.OptionsFile;
import ftc.electronvolts.util.Utility;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.ServoCfg;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.util.FileUtil;

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
 * @TeleOp(name = "MyRobot ServoTuneOp")
 * public class MyRobotServoTuneOp extends AbstractServoTuneOp {
 * @Override protected RobotCfg createRobotCfg() {
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
    private boolean servoIndexJustChanged = true;

    /**
     * The list of current positions for each servo
     */
    private final List<Double> servoPositions = new ArrayList<>();

    /**
     * The list of servo names
     */
    private ServoName[] servoNames;

    /**
     * The map that connects the presets to the values
     */
    private Map<Enum, Double> presetMap;

    /**
     * The list of preset names for the current servo
     */
    private List<Enum> presetNames;

    /**
     * The list of preset values for the current servo
     */
    private List<Double> presets;

    /**
     * The current servo's name
     */
    private ServoName servoName;

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

    @Override
    protected void setup() {
//        servoNames = (ServoName[]) robotCfg.getServos().getServoMap().keySet().toArray();
        Set<ServoName> servoNameSet = robotCfg.getServos().getServoMap().keySet();
        servoNames = servoNameSet.toArray(new ServoName[servoNameSet.size()]);

//        servoNames = TestRobotCfg.TestBotServoName.values();
//        Map<ServoName, Enum> servoStartPresetMap = ServoCfg.defaultServoStartPresetMap(servoNames);
        for (ServoName servoName : servoNames) {
//            Enum startPreset = servoStartPresetMap.get(servoName);
//
//            OptionsFile optionsFile = OptionsFile.fromFile(ServoCfg.getServoFilename(servoName));
//            double servoPosition = optionsFile.getAsDouble(startPreset.name(), 0.5);
//
//            servoPositions.add(servoPosition);
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
        //TODO add comments to AbstractServoTuneOp.act()
        if (driver1.dpad_up.justPressed() || driver2.dpad_up.justPressed()) {
            servoIndex += 1;
            servoIndexJustChanged = true;
        }
        if (driver1.dpad_down.justPressed() || driver2.dpad_down.justPressed()) {
            servoIndex -= 1;
            servoIndexJustChanged = !servoIndexJustChanged;
        }

        int numServos = robotCfg.getServos().getServoMap().size();
        if (servoIndex > numServos - 1) servoIndex = 0;
        if (servoIndex < 0) servoIndex = servoNames.length - 1;

        if (servoIndexJustChanged) {
            servoIndexJustChanged = false;

            servoName = servoNames[servoIndex];
            servo = robotCfg.getServo(servoName);

            presetMap = servo.getPresets();
            presetNames = new ArrayList<>(presetMap.keySet());
            presets = new ArrayList<>(presetMap.values());
            servoPositions.set(servoIndex, presets.get(presetIndex));
        }

        if (driver1.dpad_left.justPressed() || driver2.dpad_left.justPressed()) {
            presetIndex -= 1;
            if (presetIndex < 0) presetIndex = presets.size() - 1;
            servoPositions.set(servoIndex, presets.get(presetIndex));
        }
        if (driver1.dpad_right.justPressed() || driver2.dpad_right.justPressed()) {
            presetIndex += 1;
            if (presetIndex > presets.size() - 1) presetIndex = 0;
            servoPositions.set(servoIndex, presets.get(presetIndex));
        }

        telemetry.addData("Press start to set the current preset to the current value", "");
        if (driver1.start.justPressed() || driver2.start.justPressed()) {
            presets.set(presetIndex, servoPositions.get(servoIndex));
            Map<String, String> presetStringMap = new HashMap<>();
            int i = 0;
            for (Map.Entry<Enum, Double> entry : presetMap.entrySet()) {
//                presetStringMap.put(entry.getKey().toString(), entry.getValue().toString());
                presetStringMap.put(entry.getKey().toString(), presets.get(i).toString());
                presetMap.put(entry.getKey(), presets.get(i));
                i++;
            }
            OptionsFile optionsFile = new OptionsFile(presetStringMap);
            optionsFile.writeToFile(FileUtil.getFile(ServoCfg.getServoFilename(servoName)));
        }

        telemetry.addData("Servo Name", servoName);

        double servoPos = servoPositions.get(servoIndex);

        servoPos += 0.01 * (
                driver1.left_stick_y.getValue() + 0.1 * driver1.right_stick_y.getValue() +
                        driver2.left_stick_y.getValue() + 0.1 * driver2.right_stick_y.getValue()
        );
        servoPos = Utility.servoLimit(servoPos);
        servo.setPosition(servoPos);

        servoPositions.set(servoIndex, servoPos);

        telemetry.addData("servo preset name", presetNames.get(presetIndex));
        telemetry.addData("servo preset value", servoPos);
    }

    @Override
    protected void end() {

    }
}
