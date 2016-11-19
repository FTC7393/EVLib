package ftc.evlib.hardware.control;

import ftc.electronvolts.util.Vector2D;
import ftc.electronvolts.util.units.Velocity;
import ftc.evlib.hardware.motors.MecanumMotors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/19/16
 * <p>
 * Manages what algorithms control the rotation and translation of the mecanum wheels
 * This allows you to mix and match rotation and translation (x and y) controllers and change them whenever you want
 */
public class MecanumControl {
    /**
     * the motors to be controlled
     */
    private final MecanumMotors mecanumMotors;

    //the controllers for the rotation and translation
    private RotationControl rotationControl;
    private TranslationControl translationControl;

    /**
     * the velocity in the x, y, and rotation directions
     */
    private double velocityX, velocityY, velocityR;

    /**
     * stores whether or not the translation/rotation worked
     * for example, translationWorked will be false if the line following lost the line
     */
    private boolean translationWorked = true, rotationWorked = false;

    /**
     * create a MecanumControl that is not moving
     *
     * @param mecanumMotors the motors to control
     */
    public MecanumControl(MecanumMotors mecanumMotors) {
        this(mecanumMotors, RotationControls.zero(), TranslationControls.zero());
    }

    /**
     * create a MecanumControl and immediately start using TranslationControl and RotationControl
     *
     * @param mecanumMotors      the motors to control
     * @param rotationControl    how to command the motors' rotation
     * @param translationControl how to command the motors' translation
     */
    public MecanumControl(MecanumMotors mecanumMotors, RotationControl rotationControl, TranslationControl translationControl) {
        this.rotationControl = rotationControl;
        this.mecanumMotors = mecanumMotors;
        this.translationControl = translationControl;
    }

    /**
     * @return the robot's speed when the power is set to 100%
     */
    public Velocity getMaxRobotSpeed() {
        return mecanumMotors.getMaxRobotSpeed();
    }

    /**
     * @param rotationControl the controller that determines the robot's rotation
     */
    public void setRotationControl(RotationControl rotationControl) {
        this.rotationControl = rotationControl;
    }

    /**
     * @param translationControl the controller that determines the robot's translation (x and y)
     */
    public void setTranslationControl(TranslationControl translationControl) {
        this.translationControl = translationControl;
    }

    /**
     * set both translation and rotation controls at once
     *
     * @param translationControl the controller that determines the robot's translation (x and y)
     * @param rotationControl    the controller that determines the robot's rotation
     */
    public void setControl(TranslationControl translationControl, RotationControl rotationControl) {
        this.translationControl = translationControl;
        this.rotationControl = rotationControl;
    }

    /**
     * set both translation and rotation controls at once
     *
     * @param rotationControl    the controller that determines the robot's rotation
     * @param translationControl the controller that determines the robot's translation (x and y)
     */
    public void setControl(RotationControl rotationControl, TranslationControl translationControl) {
        this.translationControl = translationControl;
        this.rotationControl = rotationControl;
    }

    /**
     * stop the motors
     */
    public void stopMotors() {
        translationControl = TranslationControls.zero();
        rotationControl = RotationControls.zero();
        mecanumMotors.stopMotors();
    }

    /**
     * set the drive mode to turn on or off translation normalizing
     *
     * @param mode the drive mode
     */
    public void setDriveMode(MecanumMotors.MecanumDriveMode mode) {
        mecanumMotors.setDriveMode(mode);
    }

    /**
     * update the motor powers based on the output of the translationControl and rotationControl
     */
    public void act() {
        translationWorked = translationControl.act();
        rotationWorked = rotationControl.act();

        Vector2D translation = translationControl.getTranslation();

        double velocity = translation.getLength();
        double direction = translation.getDirection().radians() +
                rotationControl.getPolarDirectionCorrection().radians();

        velocityX = velocity * Math.cos(direction);
        velocityY = velocity * Math.sin(direction);

        velocityR = rotationControl.getVelocityR();

        mecanumMotors.setVelocityXYR(
                velocityX,
                velocityY,
                velocityR
        );

        mecanumMotors.mecanumDrive();
    }

    /**
     * @return whether or not the translation worked
     */
    public boolean translationWorked() {
        return translationWorked;
    }

    /**
     * @return whether or not the rotation worked
     */
    public boolean rotationWorked() {
        return rotationWorked;
    }

    /**
     * @return the robot's x velocity
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * @return the robot's y velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * @return the robot's rotational velocity
     */
    public double getVelocityR() {
        return velocityR;
    }
}