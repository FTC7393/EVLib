package ftc.evlib.hardware.sensors;

import ftc.electronvolts.util.InputExtractor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Interface for any type of analog sensor
 * examples: light sensor, distance sensor, potentiometer
 *
 * @see InputExtractor
 * @see Sensors
 */
public interface AnalogSensor extends InputExtractor<Double> {
}
