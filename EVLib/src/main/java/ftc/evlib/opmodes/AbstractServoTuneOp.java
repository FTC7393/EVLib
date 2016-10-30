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
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.ServoCfg;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/30/16
 */

public abstract class AbstractServoTuneOp extends AbstractTeleOp<RobotCfg> {
    private int servoIndex = 0, presetIndex = 0;
    private final List<Double> servoPositions = new ArrayList<>();
    private ServoName[] servoNames;

    private Map<Enum, Double> presetMap;
    private List<Enum> presetNames;
    private List<Double> presets;

    private ServoName servoName;
    private ServoControl servo;

    @Override
    protected Function getJoystickScalingFunction() {
        return Functions.none();
    }

    @Override
    public int getMatchTime() {
        return -1;
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

    boolean servoIndexJustChanged = true;

    @Override
    protected void act() {

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
//            servoConfig = Hardware.defaultServoCfgMap().get(servoName);
//            presets = (List<Double>) servoConfig.getPresets().values();
//            presetNames = (List<ServoPreset>) servoConfig.getPresets().keySet();
//            presetIndex = presetNames.indexOf(servoConfig.getStartName());
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
