package ftc.evlib.hardware.control;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

import ftc.electronvolts.util.DeadZone;
import ftc.electronvolts.util.DeadZones;
import ftc.electronvolts.util.Vector2D;
import ftc.electronvolts.util.Vector3D;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.AngularVelocity;
import ftc.electronvolts.util.units.Distance;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.hardware.sensors.DistanceSensor;
import ftc.evlib.hardware.sensors.LineSensorArray;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/30/16
 *
 * This class implements both RotationControl and TranslationControl
 */

public class LineUpControl extends XYRControl {
    private static final double LINE_GAIN = 0.1;
    private static final double LINE_MAX_SPEED = 0.1;
    private static final double LINE_DEADZONE = 0.01;
    private static final double LINE_MIN_SPEED = 0.05;

    private static final double DIST_GAIN = 0.3;
    private static final double DIST_MAX_SPEED = 0.5;
    private static final double DIST_DEADZONE = 0.01;
    private static final double DIST_MIN_SPEED = 0.05;

    private static final double GYRO_GAIN = 0.2;
    private static final double GYRO_MAX_ANGULAR_SPEED = RotationControl.DEFAULT_MAX_ANGULAR_SPEED;
    private static final double GYRO_DEADZONE = 0.01;
    private static final double GYRO_MIN_ANGULAR_SPEED = 0.05;

    private static final DeadZone LINE_POS_CANNOT_SEE_BEACON = DeadZones.not(DeadZones.minMaxDeadzone(-0.667, 0.9334));
    private static final DeadZone ANGLE_DEG_CANNOT_SEE_BEACON = DeadZones.not(DeadZones.minMaxDeadzone(-3, 3));

    private static final Distance ROBOT_WIDTH = Distance.fromInches(17); //measured
    private static final Distance ROBOT_LENGTH = Distance.fromInches(16); //measured

    private static final Distance WHEEL_BASE_WIDTH = Distance.fromInches(14.5); //measured
    private static final Distance WHEEL_BASE_LENGTH = Distance.fromInches(13); //measured

    private static final Distance LINE_SENSOR_Y = Distance.fromInches(8.5); //measured
    private static final Distance LINE_SENSOR_LEFT_X = Distance.fromInches(-4); //measured
    private static final Distance LINE_SENSOR_RIGHT_X = Distance.fromInches(4); //measured

    private static final AngularVelocity maxRotationSpeed = new AngularVelocity(Angle.fromDegrees(188), Time.fromSeconds(1)); //measured

    public enum Button {
        LEFT(-.133333, -.666667), //center -.244444
        RIGHT(.666666, 1.0); //center .644444, 1.0 means only the rightmost sensor is on

        public final DeadZone targetDeadZone;
        public final double targetPosition;

        Button(double minTargetPosition, double maxTargetPosition) {
            targetDeadZone = DeadZones.minMaxDeadzone(minTargetPosition, maxTargetPosition);
            targetPosition = (minTargetPosition + maxTargetPosition) / 2;
        }
    }


    private final LineSensorArray lineSensorArray;
    private final Button buttonToLineUpWith;
    private final DistanceSensor distanceSensor;
    private final Distance distanceTarget;
    private final GyroSensor gyro;
    private final Vector2D targetHeadingVector;

    private Vector2D translation = new Vector2D(0, 0);
    private double rotationCorrection;

    public LineUpControl(LineSensorArray lineSensorArray, Button buttonToLineUpWith, DistanceSensor distanceSensor, Distance distanceTarget, GyroSensor gyro, Angle targetHeading) {
        this.lineSensorArray = lineSensorArray;
        this.buttonToLineUpWith = buttonToLineUpWith;
        this.distanceSensor = distanceSensor;
        this.distanceTarget = distanceTarget;
        this.gyro = gyro;
        this.targetHeadingVector = new Vector2D(1, targetHeading);
    }

    @Override
    public boolean act() {
        //-------- LINE SENSOR --------//
        lineSensorArray.update();
        if (lineSensorArray.getNumSensorsActive() == 0) {
            translation = new Vector2D(0, 0);
            return false; //line up failed because the sensor lost the line
        }

        double lineSensorArrayCentroid = lineSensorArray.getCentroid();
        double lineErrorX = buttonToLineUpWith.targetPosition - lineSensorArrayCentroid;
        double lineCorrectionX = lineErrorX * LINE_GAIN;

        if (Math.abs(lineCorrectionX) > LINE_MAX_SPEED) {
            //cap the lineCorrectionX at +/- maxAngularSpeed
            lineCorrectionX = Math.signum(lineCorrectionX) * LINE_MAX_SPEED;
        } else if (Math.abs(lineCorrectionX) < LINE_DEADZONE) {
            //set it to 0 if it is in the deadzone
            lineCorrectionX = 0;
        } else if (Math.abs(lineCorrectionX) < LINE_MIN_SPEED) {
            //set it to the minimum if it is below
            lineCorrectionX = Math.signum(lineCorrectionX) * LINE_MIN_SPEED;
        }

        //if the line sensor reading is within the deadzone, set the correction to 0
        if (buttonToLineUpWith.targetDeadZone.isInside(lineSensorArrayCentroid)) {
            lineCorrectionX = 0;
        }

//        lineCorrectionX = 0;

        Vector3D linePointMeters = new Vector3D(
                Range.scale(lineSensorArray.getCentroid(), -1, 1, LINE_SENSOR_LEFT_X.meters(), LINE_SENSOR_RIGHT_X.meters()),
                LINE_SENSOR_Y.meters(),
                0
        );

        //-------- GYRO --------//
        //get the gyro heading and convert it to a vector
        double gyroHeadingDegrees = gyro.getHeading();
        Vector2D gyroVector = new Vector2D(1, Angle.fromDegrees(gyroHeadingDegrees));

        //find the "signed angular separation", the magnitude and direction of the error
        double angleRadians = Vector2D.signedAngularSeparation(targetHeadingVector, gyroVector).radians();
        telemetry.addData("signed angular separation", angleRadians);

//              This graph shows angle error vs. rotation correction
//              ____________________________
//              | correction.       ____   |
//              |           .      /       |
//              |           .   __/        |
//              | ........__.__|.......... |
//              |      __|  .     error    |
//              |     /     .              |
//              | ___/      .              |
//              |__________________________|
//
//              The following code creates this graph:

        rotationCorrection = GYRO_GAIN * angleRadians;

        if (Math.abs(rotationCorrection) > GYRO_MAX_ANGULAR_SPEED) {
            //cap the rotationCorrection at +/- maxAngularSpeed
            rotationCorrection = Math.signum(rotationCorrection) * GYRO_MAX_ANGULAR_SPEED;
        } else if (Math.abs(rotationCorrection) < GYRO_DEADZONE) {
            //set it to 0 if it is in the deadzone
            rotationCorrection = 0;
        } else if (Math.abs(rotationCorrection) < GYRO_MIN_ANGULAR_SPEED) {
            //set it to the minimum if it is below
            rotationCorrection = Math.signum(rotationCorrection) * GYRO_MIN_ANGULAR_SPEED;
        }
        Vector3D rotation = new Vector3D(0, 0, rotationCorrection * maxRotationSpeed.radiansPerSecond());
        Vector3D gyroTranslationCorrection = Vector3D.crossProduct(linePointMeters, rotation);

        //-------- DISTANCE SENSOR --------//


//        double distErrorY = distanceTarget.meters() - distanceSensor.getDistance().meters();
        double distErrorY = -distanceSensor.getDistance().meters();
        double distCorrectionY = distErrorY * DIST_GAIN;

        if (Math.abs(distCorrectionY) > DIST_MAX_SPEED) {
            //cap the lineCorrectionX at +/- maxAngularSpeed
            distCorrectionY = Math.signum(distCorrectionY) * DIST_MAX_SPEED;
        } else if (Math.abs(distCorrectionY) < DIST_DEADZONE) {
            //set it to 0 if it is in the deadzone
            distCorrectionY = 0;
        } else if (Math.abs(distCorrectionY) < DIST_MIN_SPEED) {
            //set it to the minimum if it is below
            distCorrectionY = Math.signum(distCorrectionY) * DIST_MIN_SPEED;
        }

//        double distCorrectionY = distanceControl.computeCorrection(distanceTarget.meters(), distanceSensor.getDistance().meters());
        if (LINE_POS_CANNOT_SEE_BEACON.isInside(lineSensorArray.getCentroid()) || ANGLE_DEG_CANNOT_SEE_BEACON.isInside(gyroHeadingDegrees)) {
            distCorrectionY = 0;
        }

        //-------- COMBINE TRANSLATION CORRECTIONS --------//
        translation = new Vector2D(
                lineCorrectionX + gyroTranslationCorrection.getX(),
                distCorrectionY + gyroTranslationCorrection.getY()
        );

        lineX = lineCorrectionX;
        distY = distCorrectionY;
        gyroXYZ = gyroTranslationCorrection;
        gyroErrorR = angleRadians;

        telemetry.addData("LineUp X", translation.getX());
        telemetry.addData("LineUp Y", translation.getY());
        telemetry.addData("LineUp R", rotationCorrection);

//        translation = new Vector2D(0, 0);

//        translation = new Vector2D(0, 0);
//        rotationCorrection = 0;

        return true; //line up succeeded

    }

    public static double lineX, distY, gyroErrorR;
    public static Vector3D gyroXYZ = new Vector3D(0, 0, 0);

    @Override
    public Vector2D getTranslation() {
        return translation;
    }

    @Override
    public double getVelocityR() {
        return rotationCorrection;
    }
}
