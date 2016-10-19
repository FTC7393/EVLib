package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import ftc.electronvolts.util.Angle;
import ftc.electronvolts.util.Utility;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/27/15
 * <p>
 * A subclass of FourMotors that contains algorithms for controlling mecanum wheels.
 * It stores the X, Y, and R velocities and sends them to the motors when it is updated.
 */
public class MecanumMotors extends FourMotors {

    private double velocityX = 0;
    private double velocityY = 0;
    private double velocityR = 0;

    private MecanumDriveMode driveMode = MecanumDriveMode.NORMALIZED;

//    public MecanumRobot(List<Motor> motors, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
//        super(motors, useSpeedMode, stopBehavior);
//    }

    public MecanumMotors(Motor frontLeftMotor, Motor frontRightMotor, Motor backLeftMotor, Motor backRightMotor, boolean useSpeedMode, Motor.StopBehavior stopBehavior) {
        super(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor, useSpeedMode, stopBehavior);
    }

    public enum MecanumDriveMode {
        NORMALIZED, TRANSLATION_NORMALIZED
    }

    public void setDriveMode(MecanumDriveMode mode) {
        driveMode = mode;
    }

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

    private void mecanumDriveNormalized() {
        //calculate motor powers
        runMotorsNormalized(
                velocityX + velocityY - velocityR,
                velocityX - velocityY + velocityR,
                velocityX - velocityY - velocityR,
                velocityX + velocityY + velocityR
        );
    }

    // Calculate rotational velocity first, and use remaining headway for translation.
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

        runMotors(valuesScaled);
    }


    public void setVelocityX(double velocityX) {
        this.velocityX = Utility.motorLimit(velocityX);
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = Utility.motorLimit(velocityY);
    }

    public void setVelocityR(double velocityR) {
        this.velocityR = Utility.motorLimit(velocityR);
    }

    public void setVelocityXY(double velocityX, double velocityY) {
        setVelocityX(velocityX);
        setVelocityY(velocityY);
    }

    public void setVelocityXYR(double velocityX, double velocityY, double velocityR) {
        setVelocityX(velocityX);
        setVelocityY(velocityY);
        setVelocityR(velocityR);
    }

    public void setVelocityPolar(double velocity, Angle direction) {
        double directionRadians = direction.radians();
        setVelocityX(velocity * Math.cos(directionRadians));
        setVelocityY(velocity * Math.sin(directionRadians));
    }

    public void setVelocityPolarR(double velocity, Angle direction, double velocityR) {
        setVelocityPolar(velocity, direction);
        setVelocityR(velocityR);
    }

/*    currentX = Hardware.encoderToDistance(
            motorA.getCurrentPosition() + motorB.getCurrentPosition() +
            motorC.getCurrentPosition() + motorD.getCurrentPosition());

    //Y = A+D-B-C
    currentY = Hardware.encoderToDistance(
            motorA.getCurrentPosition() + motorD.getCurrentPosition() -
            motorB.getCurrentPosition() - motorC.getCurrentPosition());*/
}
