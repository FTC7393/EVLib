package ftc.evlib.statemachine;

import com.qualcomm.robotcore.hardware.GyroSensor;

import ftc.electronvolts.statemachine.EndCondition;
import ftc.electronvolts.statemachine.EndConditions;
import ftc.electronvolts.util.Angle;
import ftc.electronvolts.util.Vector2D;
import ftc.evlib.hardware.sensors.AnalogSensor;
import ftc.evlib.hardware.sensors.ColorSensor;
import ftc.evlib.hardware.sensors.DoubleLineSensor;
import ftc.evlib.hardware.sensors.LineFinder;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/10/16
 * <p>
 * Factory class for EndCondition
 * extends EndConditions, which has some useful factory methods already
 */
public class EVEndConditions extends EndConditions {

    /**
     * wait for a line of any color
     *
     * @param doubleLineSensor   the two line sensors
     * @param detectionsRequired the number of detections in a row to look for (to avoid false positives)
     * @return the created EndCondition
     */
    public static EndCondition foundLine(final DoubleLineSensor doubleLineSensor, final int detectionsRequired) {
        return new EndCondition() {
            int detections;

            @Override
            public void init() {
                detections = 0;
            }

            @Override
            public boolean isDone() {
                DoubleLineSensor.LinePosition linePosition = doubleLineSensor.getPosition();
                if (linePosition == DoubleLineSensor.LinePosition.LEFT || linePosition == DoubleLineSensor.LinePosition.MIDDLE || linePosition == DoubleLineSensor.LinePosition.RIGHT) {
                    detections++;
                    if (detections > detectionsRequired) {
                        return true;
                    }
                } else {
                    detections = 0;
                }
                return false;
            }
        };
    }

    /**
     * wait for a line of a certain color
     *
     * @param lineFinder         two line sensors and a reflective color sensor
     * @param lineColor          the color of line to look for
     * @param detectionsRequired the number of detections in a row to look for (to avoid false positives)
     * @return the created EndCondition
     */
    public static EndCondition foundColoredLine(final LineFinder lineFinder, final LineFinder.LineColor lineColor, final int detectionsRequired) {
        return new EndCondition() {
            int detections;

            @Override
            public void init() {
                detections = 0;
                lineFinder.startLookingFor(lineColor);
            }

            @Override
            public boolean isDone() {
                if (lineFinder.getValue()) {
                    detections++;
                    if (detections > detectionsRequired) {
                        return true;
                    }
                } else {
                    detections = 0;
                }
                return false;
            }
        };
    }

    /**
     * look for a value from a sensor that is greater/less than a target value
     *
     * @param analogSensor the sensor
     * @param target       the target sensor value
     * @param greater      true if the sensor value needs to be greater than the target value
     * @return the created EndCondition
     */
    public static EndCondition analogSensor(final AnalogSensor analogSensor, final double target, final boolean greater) {
        return new EndCondition() {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                if (greater) {
                    return (analogSensor.getValue() >= target);
                } else {
                    return (analogSensor.getValue() <= target);
                }
            }
        };
    }

    /**
     * look for a value from a sensor that is greater than a target value
     *
     * @param analogSensor the sensor
     * @param value        the target value
     * @return the created EndCondition
     */
    public static EndCondition analogSensorGreater(AnalogSensor analogSensor, double value) {
        return analogSensor(analogSensor, value, true);
    }

    /**
     * look for a value from a sensor that is less than a target value
     *
     * @param analogSensor the sensor
     * @param value        the target value
     * @return the created EndCondition
     */
    public static EndCondition analogSensorLess(AnalogSensor analogSensor, double value) {
        return analogSensor(analogSensor, value, false);
    }

    /**
     * wait until the gyro heading is close to a target value
     *
     * @param gyro             the gyro sensor
     * @param targetDegrees    the target value (in degrees)
     * @param toleranceDegrees the accepted tolerance to be considered "close to" (in degrees)
     * @return the created EndCondition
     */
    public static EndCondition gyroCloseTo(GyroSensor gyro, double targetDegrees, double toleranceDegrees) {
        return gyroCloseTo(gyro, Angle.fromDegrees(targetDegrees), Angle.fromDegrees(toleranceDegrees));
    }

    /**
     * wait until the gyro heading is close to a target value
     *
     * @param gyro             the gyro sensor
     * @param target           the target value
     * @param toleranceDegrees the accepted tolerance to be considered "close to" (in degrees)
     * @return the created EndCondition
     */
    public static EndCondition gyroCloseTo(GyroSensor gyro, Angle target, double toleranceDegrees) {
        return gyroCloseTo(gyro, target, Angle.fromDegrees(toleranceDegrees));
    }

    /**
     * wait until the gyro heading is close to a target value
     *
     * @param gyro          the gyro sensor
     * @param targetDegrees the target value (in degrees)
     * @param tolerance     the accepted tolerance to be considered "close to"
     * @return the created EndCondition
     */
    public static EndCondition gyroCloseTo(GyroSensor gyro, double targetDegrees, Angle tolerance) {
        return gyroCloseTo(gyro, Angle.fromDegrees(targetDegrees), tolerance);
    }

    /**
     * wait until the gyro heading is close to a target value
     *
     * @param gyro      the gyro sensor
     * @param target    the target value
     * @param tolerance the accepted tolerance to be considered "close to"
     * @return the created EndCondition
     */
    public static EndCondition gyroCloseTo(final GyroSensor gyro, Angle target, final Angle tolerance) {
        final Vector2D targetVector = new Vector2D(1, target);
        return new EndCondition() {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                Vector2D gyroVector = new Vector2D(1, Angle.fromDegrees(gyro.getHeading()));
                Angle separation = Vector2D.signedAngularSeparation(targetVector, gyroVector);
                return Math.abs(separation.radians()) <= tolerance.radians();
            }
        };
    }

    public static EndCondition gyroCloseToRelative(final GyroSensor gyro, double targetDegrees, final double toleranceDegrees) {
        final Vector2D targetVector = new Vector2D(1, Angle.fromDegrees(targetDegrees));
        return new EndCondition() {
            double gyroInit = 0;

            @Override
            public void init() {
                gyroInit = gyro.getHeading();
            }

            @Override
            public boolean isDone() {
                Vector2D gyroVector = new Vector2D(1, Angle.fromDegrees(gyro.getHeading() - gyroInit));
                Angle separation = Vector2D.signedAngularSeparation(targetVector, gyroVector);
                return Math.abs(separation.degrees()) <= toleranceDegrees;
            }
        };
    }

    /**
     * wait for a reflection of red light with a color sensor
     *
     * @param colorSensor the sensor
     * @return the created EndCondition
     */
    public static EndCondition colorSensorRedLight(final ColorSensor colorSensor) {
        return new EndCondition() {
            @Override
            public void init() {
                colorSensor.setColorRed();
            }

            @Override
            public boolean isDone() {
                return colorSensor.getValue();
            }
        };
    }

    /**
     * wait for a reflection of blue light with a color sensor
     *
     * @param colorSensor the sensor
     * @return the created EndCondition
     */
    public static EndCondition colorSensorBlueLight(final ColorSensor colorSensor) {
        return new EndCondition() {
            @Override
            public void init() {
                colorSensor.setColorBlue();
            }

            @Override
            public boolean isDone() {
                return colorSensor.getValue();
            }
        };
    }

}
