package ftc.evlib.hardware.sensors;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/12/16
 *
 * An implementation of LineSensorArray for the sparkfun line sensor array:
 * https://www.sparkfun.com/products/13582
 *
 * The array has 8 sensors and uses the i2c interface
 */
public class SingleLineSensorArray implements LineSensorArray {
    /**
     * The number of sensors on the array
     */
    public static final int NUM_SENSORS = 8;

    /**
     * The middle of the center of mass
     */
    private static final double CM_MIDDLE = (NUM_SENSORS - 1) / 2.0;

    /**
     * the register that holds the line sensor values
     */
    private static final int I2C_REG_DATA_A = 0x11;

    private final I2cDevice i2cDevice;
    private final I2cDeviceReader i2cDeviceReader;
    private final I2cAddr i2cAddr;

    private final boolean isInverted;

    private double centerOfMass;
    private int numSensorsActive;

    private final boolean[] rawValues = new boolean[NUM_SENSORS];

    /**
     * @param i2cDevice  the i2c device from the hardwareMap
     * @param i2cAddr    the address of the i2c device
     * @param isInverted whether or not to invert all the sensor readings
     */
    public SingleLineSensorArray(I2cDevice i2cDevice, I2cAddr i2cAddr, boolean isInverted) {
        this.i2cDevice = i2cDevice;
        this.i2cAddr = i2cAddr;
        this.isInverted = isInverted;

        //create an i2c reader to read the register we want
        i2cDeviceReader = new I2cDeviceReader(i2cDevice, i2cAddr, I2C_REG_DATA_A, 1);
    }

    /**
     * update the rawValues, numSensorsActive, and centerOfMass variables
     */
    @Override
    public void update() {
        //do nothing if the i2c port is busy
        if (!i2cDevice.isI2cPortReady()) return;

        //get the sensor reading
        byte[] buffer = i2cDeviceReader.getReadBuffer();

        for (int i = 0; i < buffer.length; i++) {
            telemetry.addData(i2cAddr.get7Bit() + " buffer[" + i + "]", buffer[i]);
        }

        //11111111  -1
        //01111111  -2
        //10111111  -3
        //11011111  -5
        //11101111  -9
        //11110111  -17
        //11111011  -33
        //11111101  -65
        //11111110  127

//
        // split the byte of data into 8 boolean values
        for (int i = 0; i < NUM_SENSORS; i++) {
            rawValues[i] = ((buffer[0] & (1 << i)) != 0) ^ isInverted;
            //(1<<i)     is the bit that we want (1, 2, 4, 8, 16, etc.)
            //(buffer[0] & (1 << i)     retrieves that bit
            // ... != 0)     converts the int to a boolean
            // ... ^ isInverted     flips the boolean if isInverted is true

//            telemetry.addData(i2cAddr.get7Bit() + " rawValues[" + i + "]", rawValues[i]);
        }

        //the loop above is equivalent to the following old code:
//        rawValues[0] = ((buffer[0] & 0x01) != 0) ^ isInverted;
//        rawValues[1] = ((buffer[0] & 0x02) != 0) ^ isInverted;
//        rawValues[2] = ((buffer[0] & 0x04) != 0) ^ isInverted;
//        rawValues[3] = ((buffer[0] & 0x08) != 0) ^ isInverted;
//        rawValues[4] = ((buffer[0] & 0x10) != 0) ^ isInverted;
//        rawValues[5] = ((buffer[0] & 0x20) != 0) ^ isInverted;
//        rawValues[6] = ((buffer[0] & 0x40) != 0) ^ isInverted;
//        rawValues[7] = ((buffer[0] & 0x80) != 0) ^ isInverted;

        //count the number of sensors active and the center of mass
        numSensorsActive = 0;
        centerOfMass = 0;
        for (int i = 0; i < NUM_SENSORS; i++) {
            if (rawValues[i]) {
                numSensorsActive++;
                centerOfMass += i + CM_MIDDLE;
            }
        }
        if (numSensorsActive == 0) {
            centerOfMass = 0;
        } else {
            centerOfMass /= numSensorsActive;
        }

        telemetry.addData(i2cAddr.get7Bit() + " center of mass", centerOfMass);
        telemetry.addData(i2cAddr.get7Bit() + " sensors active", numSensorsActive);
    }

    @Override
    public boolean[] getRawValues() {
        return rawValues;
    }

    @Override
    public int getNumSensors() {
        return NUM_SENSORS;
    }

    @Override
    public int getNumSensorsActive() {
        return numSensorsActive;
    }

    @Override
    public double getCenterOfMass() {
        return centerOfMass;
    }
}
