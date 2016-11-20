package ftc.evlib.hardware.sensors;


import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 4/10/16
 * <p>
 * Combines two line sensors and an LED-powered color sensor to detect the color of lines
 *
 * @see ColorSensor
 * @see DoubleLineSensor
 */
public class LineFinder implements DigitalSensor {
    public enum LineColor {
        RED,
        BLUE,
        WHITE_RED_TEAM,
        WHITE_BLUE_TEAM
    }

    private final ColorSensor colorSensor;
    private final DoubleLineSensor doubleLineSensor;
    private LineColor lookingFor = LineColor.WHITE_RED_TEAM;
    private boolean justStarted = true;


    public LineFinder(ColorSensor colorSensor, DoubleLineSensor doubleLineSensor) {
        this.colorSensor = colorSensor;
        this.doubleLineSensor = doubleLineSensor;
    }

    /**
     * start looking for a line of a certain color by turning the LEDs to a certain color
     *
     * @param lineColor the color of the line to look for
     */
    public void startLookingFor(LineColor lineColor) {
        lookingFor = lineColor;
        justStarted = true;
        if (lookingFor == LineColor.RED) {
            colorSensor.setColorBlue();
        } else if (lookingFor == LineColor.BLUE) {
            colorSensor.setColorRed();
        } else if (lookingFor == LineColor.WHITE_RED_TEAM) {
            colorSensor.setColorBlue();
        } else if (lookingFor == LineColor.WHITE_BLUE_TEAM) {
            colorSensor.setColorRed();
        }
    }

    /**
     * note: it skips the first loop because the LEDs take 1 loop to turn on
     *
     * @return true if the line of the requested color was found
     */
    @Override
    public Boolean getValue() {
        if (justStarted) {
            justStarted = false;
            telemetry.addData("Just started line finder", "");
        } else {
            if (lookingFor == LineColor.RED) {
                return doubleLineSensor.getValue() && !colorSensor.getValue();
            } else if (lookingFor == LineColor.BLUE) {
                return doubleLineSensor.getValue() && !colorSensor.getValue();
            } else if (lookingFor == LineColor.WHITE_RED_TEAM) {
                return doubleLineSensor.getValue() && colorSensor.getValue();
            } else if (lookingFor == LineColor.WHITE_BLUE_TEAM) {
                return doubleLineSensor.getValue() && colorSensor.getValue();
            }
        }
        return false;
    }

    public void act() {
        doubleLineSensor.act();
        colorSensor.act();
    }
}
