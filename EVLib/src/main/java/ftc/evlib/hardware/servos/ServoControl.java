package ftc.evlib.hardware.servos;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;
import java.util.Map;

import ftc.electronvolts.util.files.OptionsFile;
import ftc.evlib.util.EVConverters;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * Controls the speed of a servo and stores preset values in a file
 *
 * @see Servo
 * @see ServoName
 * @see ServoCfg
 */
public class ServoControl {
    /**
     * The speed to set the servo to if you want it to move as fast as possible
     */
    public static final double MAX_SPEED = 1e10;

    private static final int MAX_DELTA_TIME_MILLIS = 50;
    private final Servo servo;
    private long lastTime;
    private double currentPosition, targetPosition, speed;
    private final ServoName name;
    private final Map<Enum, Double> presets;
    private boolean done = false;

    /**
     * Create a ServoControl to wrap a Servo
     *
     * @param servo       the servo to control
     * @param name        the name of the servo (also contains servo info)
     * @param startPreset the preset to start at
     */
    public ServoControl(Servo servo, ServoName name, Enum startPreset) {
        this.servo = servo;
        this.name = name;

        OptionsFile optionsFile = new OptionsFile(EVConverters.getInstance(), ServoCfg.getServoFile(name));

        presets = new HashMap<>();

        for (Enum preset : name.getPresets()) {
            double servoPosition = optionsFile.get(preset.name(), 0.5);
            presets.put(preset, servoPosition);
        }
        targetPosition = presets.get(startPreset);
        currentPosition = targetPosition;

        servo.setPosition(targetPosition);
        speed = MAX_SPEED;
    }

    /**
     * @return the map of presets and their corresponding values
     */
    public Map<Enum, Double> getPresets() {
        return presets;
    }

    /**
     * go to a preset at maximum speed
     *
     * @param preset the preset to go to
     */
    public void goToPreset(Enum preset) {
        this.targetPosition = presets.get(preset);
        this.speed = MAX_SPEED;
        done = false;
    }

    /**
     * go to a preset at any speed
     *
     * @param preset the preset to go to
     * @param speed  the speed to go at
     */
    public void goToPreset(Enum preset, double speed) {
        this.targetPosition = presets.get(preset);
        this.speed = speed;
        done = false;
    }

    /**
     * go to a position at max speed
     *
     * @param position the position to go to
     */
    public void setPosition(double position) {
        this.targetPosition = position;
        this.speed = MAX_SPEED;
        done = false;
    }

    /**
     * go to a position at any speed
     *
     * @param position the position to go to
     * @param speed    the speed to go at
     */
    public void setPosition(double position, double speed) {
        this.targetPosition = position;
        this.speed = speed;
        done = false;
    }

    /**
     * @return the current position of the servo
     */
    public double getCurrentPosition() {
        return currentPosition;
    }

    /**
     * update the servo's position based on the speed
     *
     * @return whether or not the servo's movement is completed
     */
    public boolean act() {
        long now = System.currentTimeMillis(); //record the current time
        long deltaTime = now - lastTime; //calculate the time since the last update
        if (deltaTime > MAX_DELTA_TIME_MILLIS) {
            deltaTime = MAX_DELTA_TIME_MILLIS; //limit the delta time
        }

        //record telemetry
//        telemetry.addData("ServoControl " + name + " deltaTime", deltaTime);
//        telemetry.addData("ServoControl " + name + " targetPosition", targetPosition);

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

    /**
     * @return whether or not the servo's movement is completed
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return the name of the servo
     * @see ServoName
     */
    public ServoName getName() {
        return name;
    }
}
