package ftc.evlib.hardware.sensors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/31/16
 *
 * Implementation of LineSensorArray that combines two SingleLineSensorArray objects
 */
public class DoubleLineSensorArray implements LineSensorArray {
    public static final int NUM_SENSORS = 2 * SingleLineSensorArray.NUM_SENSORS;
    private final SingleLineSensorArray leftSensor, rightSensor;

    private final boolean[] rawValues = new boolean[NUM_SENSORS];

    private double centerOfMass;
    private int numSensorsActive;

    public DoubleLineSensorArray(SingleLineSensorArray leftSensor, SingleLineSensorArray rightSensor) {
        this.leftSensor = leftSensor;
        this.rightSensor = rightSensor;
    }

    @Override
    public void update() {
        //update both line sensor arrays
        leftSensor.update();
        rightSensor.update();

        //copy the raw values from each sensor into this one
        System.arraycopy(leftSensor.getRawValues(), 0, rawValues, 0, SingleLineSensorArray.NUM_SENSORS);
        System.arraycopy(rightSensor.getRawValues(), 0, rawValues, SingleLineSensorArray.NUM_SENSORS, SingleLineSensorArray.NUM_SENSORS);

        //compute numSensorsActive and centerOfMass
        numSensorsActive = leftSensor.getNumSensorsActive() + rightSensor.getNumSensorsActive();
        centerOfMass = (leftSensor.getCenterOfMass() + rightSensor.getCenterOfMass()) / 2;
    }

    @Override
    public boolean[] getRawValues() {
        return rawValues;
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
    public double getCenterOfMass() {
        return centerOfMass;
    }
}
