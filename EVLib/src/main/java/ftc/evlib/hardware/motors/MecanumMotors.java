package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import ftc.electronvolts.util.Utility;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.Velocity;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/27/15
 *
 * A subclass of FourMotors that contains algorithms for controlling mecanum wheels.
 * It stores the X, Y, and R velocities and sends them to the motors when it is updated.
 *
 * @see FourMotors
 * @see ftc.evlib.hardware.control.MecanumControl
 */
public class MecanumMotors extends FourMotors {

    public enum MecanumDriveMode {
        NORMALIZED, TRANSLATION_NORMALIZED
    }

    //the stored velocities
    private double velocityX = 0;
    private double velocityY = 0;
    private double velocityR = 0;

    //the drive mode
    private MecanumDriveMode driveMode = MecanumDriveMode.NORMALIZED;

    /**
     * @param frontLeftMotor  the front left motor
     * @param frontRightMotor the front right motor
     * @param backLeftMotor   the back left motor
     * @param backRightMotor  the back right motor
     * @param useSpeedMode    whether or not to use speed control on the motors
     * @param maxRobotSpeed   the measured speed of the robot at 100% power
     */
    public MecanumMotors(Motor frontLeftMotor, Motor frontRightMotor, Motor backLeftMotor, Motor backRightMotor, boolean useSpeedMode, Velocity maxRobotSpeed) {
        super(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor, useSpeedMode, maxRobotSpeed);
    }

    /**
     * @param mode the drive mode
     */
    public void setDriveMode(MecanumDriveMode mode) {
        driveMode = mode;
    }

    /**
     * Update the motor powers
     */
    public void mecanumDrive() {
        switch (driveMode) {
            case NORMALIZED:
                mecanumDriveNormalized();
                break;
            case TRANSLATION_NORMALIZED:
                mecanumDriveTranslationNormalized();
                break;
        }
    }

    /**
     * run the motors based on the xyr velocities
     * normalize if any motor power is too large
     */
    private void mecanumDriveNormalized() {
        //calculate motor powers
        runMotorsNormalized(
                velocityX + velocityY - velocityR,
                velocityX - velocityY + velocityR,
                velocityX - velocityY - velocityR,
                velocityX + velocityY + velocityR
        );
    }

    /**
     * Calculate rotational velocity first, and use remaining headway for translation.
     */
    private void mecanumDriveTranslationNormalized() {
        //calculate motor powers
        List<Double> translationValues = ImmutableList.of(
                velocityX + velocityY,
                velocityX - velocityY,
                velocityX - velocityY,
                velocityX + velocityY);

        List<Double> rotationValues = ImmutableList.of(
                -velocityR,
                velocityR,
                -velocityR,
                velocityR);

        double scaleFactor = 1;
        double tmpScale = 1;

        // Solve this equation backwards:
        // MotorX = TranslationX * scaleFactor + RotationX
        // to find scaleFactor that ensures -1 <= MotorX <= 1 and 0 < scaleFactor <= 1

        for (int i = 0; i < 4; i++) {
            if (Math.abs(translationValues.get(i) + rotationValues.get(i)) > 1) {
                tmpScale = (1 - rotationValues.get(i)) / translationValues.get(i);
            } else if (translationValues.get(i) + rotationValues.get(i) < -1) {
                tmpScale = (rotationValues.get(i) - 1) / translationValues.get(i);
            }
            if (tmpScale < scaleFactor) {
                scaleFactor = tmpScale;
            }
        }

        telemetry.addData("driveMode", driveMode.toString());
        telemetry.addData("scaleFactor", scaleFactor);

        List<Double> valuesScaled = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            valuesScaled.add(translationValues.get(i) * scaleFactor + rotationValues.get(i));
            telemetry.addData("valuesScaled(" + i + ")", valuesScaled.get(i));
        }

        run(valuesScaled);
    }

    /**
     * @param velocityX the x velocity
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = Utility.motorLimit(velocityX);
    }

    /**
     * @param velocityY the y velocity
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = Utility.motorLimit(velocityY);
    }

    /**
     * @param velocityR the rotational velocity
     */
    public void setVelocityR(double velocityR) {
        this.velocityR = Utility.motorLimit(velocityR);
    }

    /**
     * set the x and y velocities at the same time
     *
     * @param velocityX the x velocity
     * @param velocityY the y velocity
     */
    public void setVelocityXY(double velocityX, double velocityY) {
        setVelocityX(velocityX);
        setVelocityY(velocityY);
    }

    /**
     * set the x, y, and rotational velocities at the same time
     *
     * @param velocityX the x velocity
     * @param velocityY the y velocity
     * @param velocityR the rotational velocity
     */
    public void setVelocityXYR(double velocityX, double velocityY, double velocityR) {
        setVelocityX(velocityX);
        setVelocityY(velocityY);
        setVelocityR(velocityR);
    }

    /**
     * set the x and y velocities using polar coordinates
     *
     * @param velocity  the velocity (r of the polar coordinate)
     * @param direction the direction (theta of the polar coordinate)
     */
    public void setVelocityPolar(double velocity, Angle direction) {
        double directionRadians = direction.radians();
        setVelocityX(velocity * Math.cos(directionRadians));
        setVelocityY(velocity * Math.sin(directionRadians));
    }

    /**
     * set the x and y velocities using polar coordinates, and also set the rotational velocity
     *
     * @param velocity  the velocity (r of the polar coordinate)
     * @param direction the direction (theta of the polar coordinate)
     * @param velocityR the rotational velocity
     */
    public void setVelocityPolarR(double velocity, Angle direction, double velocityR) {
        setVelocityPolar(velocity, direction);
        setVelocityR(velocityR);
    }
}
