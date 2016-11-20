package ftc.evlib.hardware.sensors;

import ftc.electronvolts.util.units.Distance;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/9/16
 *
 * Interface for a distance sensor
 *
 * @see AnalogSensor
 * @see Sensors
 */
public interface DistanceSensor extends AnalogSensor {
    /**
     * @return the distance that the sensor is reading
     */
    Distance getDistance();
}
