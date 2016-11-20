package ftc.evlib.hardware.sensors;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/31/16
 *
 * Implementation of LineSensorArray that combines two SingleLineSensorArray objects
 *
 * @see SingleLineSensorArray
 * @see LineSensorArray
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
        for (int i = 0; i < SingleLineSensorArray.NUM_SENSORS; i++) {
            rawValues[i] = leftSensor.getValue(i);
        }
        for (int i = 0; i < SingleLineSensorArray.NUM_SENSORS; i++) {
            rawValues[i + 8] = rightSensor.getValue(i);
        }
//        System.arraycopy(leftSensor.getRawValues(), 0, rawValues, 0, SingleLineSensorArray.NUM_SENSORS);
//        System.arraycopy(rightSensor.getRawValues(), 0, rawValues, SingleLineSensorArray.NUM_SENSORS, SingleLineSensorArray.NUM_SENSORS);

        //compute numSensorsActive and centerOfMass
        numSensorsActive = leftSensor.getNumSensorsActive() + rightSensor.getNumSensorsActive();
        if (numSensorsActive == 0) {
            centerOfMass = 0;
        } else {
            centerOfMass = ((leftSensor.getCenterOfMass() - 1) * leftSensor.getNumSensorsActive() +
                    (rightSensor.getCenterOfMass() + 1) * rightSensor.getNumSensorsActive()
                            / (2 * numSensorsActive));
        }

        telemetry.addData("double center of mass", centerOfMass);
        telemetry.addData("double sensors active", numSensorsActive);
    }

    @Override
    public boolean getValue(int i) {
        return rawValues[i];
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
