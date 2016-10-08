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
 */
public class ColorSensor implements DigitalSensor {
    private static final int THRESHOLD = 500;
    private final LED redLight, blueLight;
    private final AveragedSensor lightSensor;
    private String ledColorString = "unknown";

    public ColorSensor(LED redLight, LED blueLight, AveragedSensor lightSensor) {
        this.redLight = redLight;
        this.blueLight = blueLight;
        this.lightSensor = lightSensor;
    }

    public void turnOff() {
        redLight.enable(true);
        blueLight.enable(true);
        ledColorString = "blank";
    }

    public void setColorRed() {
        redLight.enable(false);
        blueLight.enable(true);
        ledColorString = "red";
    }

    public void setColorBlue() {
        redLight.enable(true);
        blueLight.enable(false);
        ledColorString = "blue";
    }

    public void setColorMagenta() {
        redLight.enable(false);
        blueLight.enable(false);
        ledColorString = "magenta";
    }

    public String getLedColorString() {
        return ledColorString;
    }

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

    public double getRawValue() {
        return lightSensor.getValue();
    }
}
