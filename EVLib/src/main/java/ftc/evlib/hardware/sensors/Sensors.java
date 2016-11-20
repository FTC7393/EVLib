package ftc.evlib.hardware.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.TouchSensor;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.units.Distance;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Factory class for different types of sensors
 *
 * @see DigitalSensor
 * @see AnalogSensor
 */
public class Sensors {
    /**
     * wrap an AnalogInput in an AnalogSensor
     *
     * @param analogInput the input to wrap
     * @return the created AnalogSensor
     */
    public static AnalogSensor analog(final AnalogInput analogInput) {
        final double maxVoltage = analogInput.getMaxVoltage();

        return new AnalogSensor() {
            @Override
            public Double getValue() {
                double voltage = analogInput.getVoltage();
                if (voltage == 0) {
                    return Double.MAX_VALUE;
                } else {
                    return maxVoltage / voltage;
                }
            }
        };
    }

    /**
     * Scale an AnalogSensor by a function to get actual distance
     *
     * @param analogSensor          the AnalogSensor to scale
     * @param scalingFunctionInches the function to scale by
     * @return the created DistanceSensor
     */
    public static DistanceSensor scaledDistanceInches(final AnalogSensor analogSensor, final Function scalingFunctionInches) {
        return new DistanceSensor() {
            @Override
            public Distance getDistance() {
                return Distance.fromInches(scalingFunctionInches.f(getValue()));
            }

            @Override
            public Double getValue() {
                return analogSensor.getValue();
            }
        };
    }

    /**
     * Wrap an AnalogInput and scale it by a function to get actual distance
     *
     * @param analogInput           the AnalogInput to scale
     * @param scalingFunctionInches the function to scale by
     * @return the created DistanceSensor
     */
    public static DistanceSensor scaledDistanceInches(final AnalogInput analogInput, final Function scalingFunctionInches) {
        return scaledDistanceInches(analog(analogInput), scalingFunctionInches);
    }

    /**
     * DistanceSensor factory class specific to a Pololu distance sensor:
     *
     * Pololu Carrier with Sharp GP2Y0A60SZLF Analog Distance Sensor 10-150cm, 5V
     * https://www.pololu.com/product/2474
     *
     * @param analogInput the AnalogInput to scale
     * @return the created DistanceSensor
     */
    public static DistanceSensor pololuScaledDistance(AnalogInput analogInput) {
        return scaledDistanceInches(analogInput, new Function() {
            @Override
            public double f(double x) {
                return .6455 * x * x + 1.726 * x + .5826;
            }
        });
    }

    /**
     * wrap a DigitalInput in a DigitalSensor
     *
     * @param digitalInput the input to wrap
     * @return the created DigitalSensor
     */
    public static DigitalSensor digital(final DigitalChannel digitalInput) {
        digitalInput.setMode(DigitalChannelController.Mode.INPUT);

        return new DigitalSensor() {
            @Override
            public Boolean getValue() {
                return digitalInput.getState();
            }
        };
    }

    /**
     * wrap a TouchSensor in a DigitalSensor
     *
     * @param touchSensor the input to wrap
     * @return the created DigitalSensor
     */
    public static DigitalSensor digital(final TouchSensor touchSensor) {
        return new DigitalSensor() {
            @Override
            public Boolean getValue() {
                return touchSensor.isPressed();
            }
        };
    }

    /**
     * create an Accelerometer from an android Sensor class
     *
     * @param sensorManager the SensorManager from the phone
     * @param accelerometer the phone accelerometer
     * @return the created Accelerometer
     */
    public static Accelerometer accelerometer(SensorManager sensorManager, Sensor accelerometer) {
        return new Accelerometer(sensorManager, accelerometer);
    }

    /**
     * Average the last numReadings of a sensor
     *
     * @param sensor      the sensor to be averaged
     * @param numReadings the number of readings to average
     * @return the created AveragedSensor
     */
    public static AveragedSensor averaged(AnalogSensor sensor, int numReadings) {
        return new AveragedSensor(sensor, numReadings);
    }

    /**
     * a SingleLineSensorArray that is not inverted
     *
     * @param i2cDevice the i2c device from the hardwareMap
     * @param i2cAddr   the i2c address (specific to the device)
     * @return the created SingleLineSensorArray
     */
    public static SingleLineSensorArray singleLineSensorArrayNotInverted(I2cDevice i2cDevice, I2cAddr i2cAddr) {
        return singleLineSensorArray(i2cDevice, i2cAddr, false);
    }

    /**
     * a SingleLineSensorArray that is inverted
     *
     * @param i2cDevice the i2c device from the hardwareMap
     * @param i2cAddr   the i2c address (specific to the device)
     * @return the created SingleLineSensorArray
     */
    public static SingleLineSensorArray singleLineSensorArrayInverted(I2cDevice i2cDevice, I2cAddr i2cAddr) {
        return singleLineSensorArray(i2cDevice, i2cAddr, true);
    }

    /**
     * a SingleLineSensorArray
     *
     * @param i2cDevice  the i2c device from the hardwareMap
     * @param i2cAddr    the i2c address (specific to the device)
     * @param isInverted whether or not to invert all the sensor readings
     * @return the created SingleLineSensorArray
     */
    public static SingleLineSensorArray singleLineSensorArray(I2cDevice i2cDevice, I2cAddr i2cAddr, boolean isInverted) {
        return new SingleLineSensorArray(i2cDevice, i2cAddr, isInverted);
    }

    /**
     * a DoubleLineSensorArray that is not inverted
     *
     * @param leftI2cDevice  the left array i2c device from the hardwareMap
     * @param leftI2cAddr    the i2c address of the left array
     * @param rightI2cDevice the right array i2c device fro mthe hardwareMap
     * @param rightI2cAddr   the i2c address of the right array
     * @return the created DoubleLineSensor
     */
    public static DoubleLineSensorArray doubleLineSensorArrayNotInverted(I2cDevice leftI2cDevice, I2cAddr leftI2cAddr, I2cDevice rightI2cDevice, I2cAddr rightI2cAddr) {
        return doubleLineSensorArray(leftI2cDevice, leftI2cAddr, rightI2cDevice, rightI2cAddr, false);
    }

    /**
     * a DoubleLineSensorArray that is inverted
     *
     * @param leftI2cDevice  the left array i2c device from the hardwareMap
     * @param leftI2cAddr    the i2c address of the left array
     * @param rightI2cDevice the right array i2c device fro mthe hardwareMap
     * @param rightI2cAddr   the i2c address of the right array
     * @return the created DoubleLineSensor
     */
    public static DoubleLineSensorArray doubleLineSensorArrayInverted(I2cDevice leftI2cDevice, I2cAddr leftI2cAddr, I2cDevice rightI2cDevice, I2cAddr rightI2cAddr) {
        return doubleLineSensorArray(leftI2cDevice, leftI2cAddr, rightI2cDevice, rightI2cAddr, true);
    }


    /**
     * a DoubleLineSensorArray that is inverted
     *
     * @param leftI2cDevice  the left array i2c device from the hardwareMap
     * @param leftI2cAddr    the i2c address of the left array
     * @param rightI2cDevice the right array i2c device fro mthe hardwareMap
     * @param rightI2cAddr   the i2c address of the right array
     * @param isInverted     whether or not to invert all the sensor readings
     * @return the created DoubleLineSensor
     */
    public static DoubleLineSensorArray doubleLineSensorArray(I2cDevice leftI2cDevice, I2cAddr leftI2cAddr, I2cDevice rightI2cDevice, I2cAddr rightI2cAddr, boolean isInverted) {
        return doubleLineSensorArray(leftI2cDevice, leftI2cAddr, isInverted, rightI2cDevice, rightI2cAddr, isInverted);
    }


    /**
     * a DoubleLineSensorArray that is inverted
     *
     * @param leftI2cDevice   the left array i2c device from the hardwareMap
     * @param leftI2cAddr     the i2c address of the left array
     * @param rightI2cDevice  the right array i2c device fro mthe hardwareMap
     * @param rightI2cAddr    the i2c address of the right array
     * @param isLeftInverted  whether or not to invert all the left array's sensor readings
     * @param isRightInverted whether or not to invert all the right array's sensor readings
     * @return the created DoubleLineSensor
     */
    public static DoubleLineSensorArray doubleLineSensorArray(I2cDevice leftI2cDevice, I2cAddr leftI2cAddr, boolean isLeftInverted, I2cDevice rightI2cDevice, I2cAddr rightI2cAddr, boolean isRightInverted) {
        return new DoubleLineSensorArray(singleLineSensorArray(leftI2cDevice, leftI2cAddr, isLeftInverted), singleLineSensorArray(rightI2cDevice, rightI2cAddr, isRightInverted));
    }
}
