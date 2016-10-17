package ftc.evlib.hardware.control;

import ftc.electronvolts.util.Vector2D;
import ftc.electronvolts.util.Velocity;
import ftc.evlib.hardware.motors.MecanumMotors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/19/16
 * <p>
 * Manages what algorithms control the rotation and translation of the mecanum wheels
 */
public class MecanumControl {
    private final MecanumMotors mecanumMotors;
    private RotationControl rotationControl;
    private TranslationControl translationControl;
    private final Velocity maxRobotSpeed;

    public MecanumControl(MecanumMotors mecanumMotors, Velocity maxRobotSpeed) {
        this(mecanumMotors, maxRobotSpeed, RotationControls.zero(), TranslationControls.zero());
    }

    public MecanumControl(MecanumMotors mecanumMotors, Velocity maxRobotSpeed, RotationControl rotationControl, TranslationControl translationControl) {
        this.maxRobotSpeed = maxRobotSpeed.abs();
        this.rotationControl = rotationControl;
        this.mecanumMotors = mecanumMotors;
        this.translationControl = translationControl;
    }

    public Velocity getMaxRobotSpeed() {
        return maxRobotSpeed;
    }

    public void setRotationControl(RotationControl rotationControl) {
        this.rotationControl = rotationControl;
    }

    public void setTranslationControl(TranslationControl translationControl) {
        this.translationControl = translationControl;
    }

    public void stopMotors() {
        translationControl = TranslationControls.zero();
        rotationControl = RotationControls.zero();
        mecanumMotors.stopMotors();
    }

    public void setDriveMode(MecanumMotors.MecanumDriveMode mode) {
        mecanumMotors.setDriveMode(mode);
    }

    private boolean translationWorked, rotationWorked;

    /**
     * updates the motor powers based on the output of the translationControl and rotationControl
     */
    public void act() {
        translationWorked = translationControl.act();
        rotationWorked = rotationControl.act();

        Vector2D translation = translationControl.getTranslation();

        double velocity = translation.getLength();
        double direction = translation.getDirection().radians() +
                rotationControl.getPolarDirectionCorrection().radians();

        double velocityX = velocity * Math.cos(direction);
        double velocityY = velocity * Math.sin(direction);

        double velocityR = rotationControl.getVelocityR();

        mecanumMotors.setVelocityXYR(
                velocityX,
                velocityY,
                velocityR
        );

        mecanumMotors.mecanumDrive();
    }

    public boolean translationWorked() {
        return translationWorked;
    }

    public boolean rotationWorked() {
        return rotationWorked;
    }
}