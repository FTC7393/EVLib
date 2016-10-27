package ftc.evlib.hardware.servos;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;
import java.util.Map;

import ftc.electronvolts.util.OptionsFile;
import ftc.evlib.util.FileUtil;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 */
public class BasicServoControl implements ServoControl {
    private static final int MAX_DELTA_TIME_MILLIS = 50;
    private final Servo servo;
    private long lastTime;
    private double currentPosition, targetPosition, speed;
    private final ServoName name;
    private final Map<Enum, Double> presets;
    private boolean done = false;

    public BasicServoControl(Servo servo, ServoName name, Enum startPreset) {
        this.servo = servo;
        this.name = name;

        OptionsFile optionsFile = new OptionsFile(FileUtil.getFile(ServoCfg.getServoFilename(name)));

        presets = new HashMap<>();

        for (Enum preset : name.getPresets()) {

            double servoPosition = 0.5;
            try {
                servoPosition = optionsFile.getAsDouble(preset.name(), 0.5);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            presets.put(preset, servoPosition);
        }
        targetPosition = presets.get(startPreset);
        currentPosition = targetPosition;

        servo.setPosition(targetPosition);
        speed = MAX_SPEED;
    }

    @Override
    public Map<Enum, Double> getPresets() {
        return presets;
    }

    @Override
    public void go(ServoCommand servoCommand) {
        this.targetPosition = servoCommand.getPosition();
        this.speed = servoCommand.getSpeed();
        done = false;
    }

    @Override
    public void goToPreset(Enum preset) {
        this.targetPosition = presets.get(preset);
        this.speed = MAX_SPEED;
        done = false;
    }

    @Override
    public void goToPreset(Enum preset, double speed) {
        this.targetPosition = presets.get(preset);
        this.speed = speed;
        done = false;
    }

    @Override
    public void setPosition(double position) {
        this.targetPosition = position;
        this.speed = MAX_SPEED;
        done = false;
    }

    @Override
    public void setPosition(double position, double speed) {
        this.targetPosition = position;
        this.speed = speed;
        done = false;
    }

    @Override
    public double getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public boolean act() {
        long now = System.currentTimeMillis(); //record the current time
        long deltaTime = now - lastTime; //calculate the time since the last update
        if (deltaTime > MAX_DELTA_TIME_MILLIS) {
            deltaTime = MAX_DELTA_TIME_MILLIS; //limit the delta time
        }

        //record telemetry
        telemetry.addData("ServoControl " + name + " deltaTime", deltaTime);
        telemetry.addData("ServoControl " + name + " targetPosition", targetPosition);

        double positionError = targetPosition - currentPosition; //calculate the distance left to go
        double increment = speed * deltaTime / 1000.0; //calculate the amount to increment

        done = Math.abs(positionError) <= increment; //determine if the increment would meet or exceed the target

        if (done) {
            currentPosition = targetPosition; //if so, go to the target
        } else {
            currentPosition += Math.signum(positionError) * increment; //otherwise, increment the position
        }

        servo.setPosition(currentPosition); //set the servo to the position
        lastTime = now;
        return done;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public ServoName getName() {
        return name;
    }
}
