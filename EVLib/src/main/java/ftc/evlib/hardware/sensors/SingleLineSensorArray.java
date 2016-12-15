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
 *
 * @see LineSensorArray
 * @see I2cDevice
 */
public class SingleLineSensorArray implements LineSensorArray {
    /**
     * The number of sensors on the array
     */
    public static final int NUM_SENSORS = 8;

    /**
     * the register that holds the line sensor values
     */
    private static final int I2C_REG_DATA_A = 0x11;

    private final I2cDevice i2cDevice;
    private final I2cDeviceReader i2cDeviceReader;
    private final I2cAddr i2cAddr;

    private final boolean isInverted;

    private double centroid;
    private int numSensorsActive;

    private final boolean[] values = new boolean[NUM_SENSORS];

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
     * update the values, numSensorsActive, and centroid variables
     *
     * @return true if the sensor data was updated, otherwise false
     */
    @Override
    public boolean update() {
        //do nothing if the i2c port is busy
        if (!i2cDevice.isI2cPortReady()) return false;

        //get the sensor reading
        byte[] buffer = i2cDeviceReader.getReadBuffer();

//        for (int i = 0; i < buffer.length; i++) {
//            telemetry.addData(i2cAddr.get7Bit() + " buffer[" + i + "]", buffer[i]);
//        }

        //11111111  -1
        //01111111  -2
        //10111111  -3
        //11011111  -5
        //11101111  -9
        //11110111  -17
        //11111011  -33
        //11111101  -65
        //11111110  127

        numSensorsActive = 0;
        centroid = 0;
        // split the byte of data into 8 boolean values
        for (int i = 0; i < NUM_SENSORS; i++) {
            values[i] = ((buffer[0] & (1 << i)) != 0) ^ isInverted;
            //(1<<i)     is the bit that we want (1, 2, 4, 8, 16, etc.)
            //(buffer[0] & (1 << i)     retrieves that bit
            // ... != 0)     converts the int to a boolean
            // ... ^ isInverted     flips the boolean if isInverted is true

//            telemetry.addData(i2cAddr.get7Bit() + " values[" + i + "]", values[i]);

            //if there is an active value
            if (values[i]) {
                //increment the number of sensors active
                numSensorsActive++;
                //and add a mass of 1 to the center of mass
                centroid += i;
            }
        }

        //check for a divide by 0
        if (numSensorsActive == 0) {
            centroid = 0;
        } else {
            //divide the center of mass by the total mass
            centroid /= numSensorsActive;
            //scale it from [0,7] to [-1,1]
            centroid = 2 / 7 * centroid - 1;
        }

        telemetry.addData(i2cAddr.get7Bit() + " center of mass", centroid);
        telemetry.addData(i2cAddr.get7Bit() + " sensors active", numSensorsActive);

        return true;
    }

    @Override
    public boolean getValue(int i) {
        return values[i];
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
    public double getCentroid() {
        return centroid;
    }
}
