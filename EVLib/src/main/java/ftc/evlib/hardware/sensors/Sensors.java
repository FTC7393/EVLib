package ftc.evlib.hardware.sensors;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Factory class for analog and digital sensors
 */
public class Sensors {
    /**
     * wrap an AnalogInput in an AnalogSensor
     *
     * @param analogInput the input to wrap
     * @return the created AnalogSensor
     */
    public static AnalogSensor analogSensor(final AnalogInput analogInput) {
        double maxVoltage1 = analogInput.getMaxVoltage();
        if (maxVoltage1 == 0) maxVoltage1 = 1;
        final double maxVoltage = maxVoltage1;

        return new AnalogSensor() {
            @Override
            public Double getValue() {
                return analogInput.getVoltage() / maxVoltage;
            }
        };
    }

    /**
     * wrap a DigitalInput in a DigitalSensor
     *
     * @param digitalInput the input to wrap
     * @return the created DigitalSensor
     */
    public static DigitalSensor digitalSensor(final DigitalChannel digitalInput) {
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
    public static DigitalSensor digitalSensor(final TouchSensor touchSensor) {
        return new DigitalSensor() {
            @Override
            public Boolean getValue() {
                return touchSensor.isPressed();
            }
        };
    }

}
