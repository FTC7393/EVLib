package ftc.evlib.hardware.sensors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 3/8/16
 * <p>
 * Manages two line sensors to tell where the robot is relative to the line
 *
 * @see CalibratedLineSensor
 * @see DigitalSensor
 */
public class DoubleLineSensor implements DigitalSensor {

    public enum LinePosition {
        LEFT,
        MIDDLE,
        RIGHT,
        OFF_UNKNOWN,
        OFF_LEFT,
        OFF_RIGHT
    }

    private LinePosition position = LinePosition.OFF_UNKNOWN;

    private final CalibratedLineSensor leftSensor, rightSensor;

    public DoubleLineSensor(AnalogSensor leftSensor, AnalogSensor rightSensor) {
        this.leftSensor = new CalibratedLineSensor(leftSensor);
        this.rightSensor = new CalibratedLineSensor(rightSensor);
    }

    public void calibrate() {
        leftSensor.calibrate();
        rightSensor.calibrate();
    }

    public boolean isReady() {
        return leftSensor.isReady() && rightSensor.isReady();
    }

    public LinePosition getPosition() {
        return position;
    }

    public void reset() {
        position = LinePosition.MIDDLE;
    }

    /**
     * @return true if either sensor is on the line
     */
    @Override
    public Boolean getValue() {
        return leftSensor.getValue() || rightSensor.getValue();
    }

    /**
     * determine where the robot is relative to the line
     */
    public void act() {
        leftSensor.act();
        rightSensor.act();

        if (leftSensor.getValue()) {
            if (rightSensor.getValue()) {
                position = LinePosition.MIDDLE;
            } else {
                position = LinePosition.LEFT;
            }
        } else {
            if (rightSensor.getValue()) {
                position = LinePosition.RIGHT;
            } else {
                //if neither sensor detects the line, the position is determined by the previous position
                if (position == LinePosition.LEFT) {
                    position = LinePosition.OFF_LEFT;
                } else if (position == LinePosition.RIGHT) {
                    position = LinePosition.OFF_RIGHT;
                } else {
                    position = LinePosition.OFF_UNKNOWN;
                }
            }
        }
    }
}
