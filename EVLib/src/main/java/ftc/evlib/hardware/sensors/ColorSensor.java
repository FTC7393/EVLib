package ftc.evlib.hardware.sensors;

import com.qualcomm.robotcore.hardware.LED;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 3/22/16
 * <p>
 * Uses LEDs and a photoresistor to detect the color of a line on the mat
 * <p>
 * --- light red blue
 * mat    35 250 250
 * red
 * blue
 * white 145 640 550
 *
 * @see DigitalSensor
 */
public class ColorSensor implements DigitalSensor {
    private static final int THRESHOLD = 500;
    private final LED redLight, blueLight;
    private final AveragedSensor lightSensor;
    private String ledColorString = "unknown";

    /**
     * @param redLight    the red LED
     * @param blueLight   the blue LED
     * @param lightSensor the photoresistor
     */
    public ColorSensor(LED redLight, LED blueLight, AveragedSensor lightSensor) {
        this.redLight = redLight;
        this.blueLight = blueLight;
        this.lightSensor = lightSensor;
    }

    /**
     * turn off both LEDs
     */
    public void turnOff() {
        redLight.enable(true);
        blueLight.enable(true);
        ledColorString = "blank";
    }

    /**
     * turn on the red LED and turn off the blue LED
     */
    public void setColorRed() {
        redLight.enable(false);
        blueLight.enable(true);
        ledColorString = "red";
    }

    /**
     * turn on the blue LED and turn off the red LED
     */
    public void setColorBlue() {
        redLight.enable(true);
        blueLight.enable(false);
        ledColorString = "blue";
    }

    /**
     * turn on both LEDs
     */
    public void setColorMagenta() {
        redLight.enable(false);
        blueLight.enable(false);
        ledColorString = "magenta";
    }

    /**
     * @return the name of the current color for telemetry purposes
     */
    public String getLedColorString() {
        return ledColorString;
    }

    /**
     * update the light sensor
     */
    public void act() {
        lightSensor.act();
    }

    /**
     * @return true if it is seeing the color requested
     */
    @Override
    public Boolean getValue() {
        return lightSensor.getValue() > THRESHOLD;
    }

    /**
     * @return the raw value of the light sensor
     */
    public double getRawValue() {
        return lightSensor.getValue();
    }
}
