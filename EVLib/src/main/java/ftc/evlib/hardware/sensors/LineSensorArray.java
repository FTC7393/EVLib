package ftc.evlib.hardware.sensors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/31/16
 *
 * Interface for an array of line sensors
 */
public interface LineSensorArray {
    /**
     * Should be called every time in the loop
     */
    void update();

    /**
     * @return the array of values from the sensor
     */
    boolean[] getRawValues();

    /**
     * @return the number of sensors in the array
     */
    int getNumSensors();

    /**
     * @return the number of sensors seeing the line
     */
    int getNumSensorsActive();

    /**
     * @return the position of the line relative to the middle of the array by averaging all the sensors
     *
     * the values should range from -1 to 1
     */
    double getCenterOfMass();
}
