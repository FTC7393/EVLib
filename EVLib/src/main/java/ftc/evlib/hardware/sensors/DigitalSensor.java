package ftc.evlib.hardware.sensors;

import ftc.electronvolts.util.InputExtractor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * Interface for any type of digital sensor
 * examples: touch sensor, digital line sensor, magnetic reed switch
 *
 * @see InputExtractor
 * @see Sensors
 */
public interface DigitalSensor extends InputExtractor<Boolean> {
}
