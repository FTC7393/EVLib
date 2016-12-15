package ftc.evlib.hardware.sensors;

import java.util.List;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/19/16
 *
 * Combines any number of line sensor arrays into one
 *
 * @see LineSensorArray
 */
public class NLineSensorArray implements LineSensorArray {
    private final List<LineSensorArray> lineSensorArrays;
    private final boolean[] values;
    private final int numSensors;
    private final double scaling;
    private int numSensorsActive;
    private double centroid;
    private String string;

    public NLineSensorArray(List<LineSensorArray> lineSensorArrays) {
        this.lineSensorArrays = lineSensorArrays;
        int numSensors1 = 0;

        for (LineSensorArray lineSensorArray : lineSensorArrays) {
            numSensors1 += lineSensorArray.getNumSensors();
        }

        numSensors = numSensors1;
        scaling = 2.0 / (numSensors - 1);
        values = new boolean[numSensors];
    }

    @Override
    public boolean update() {
        boolean wasUpdated = false;
        for (LineSensorArray lineSensorArray : lineSensorArrays) {
            if (lineSensorArray.update()) {
                wasUpdated = true;
            }
        }
        if (!wasUpdated) return false;

        StringBuilder sb = new StringBuilder();

        numSensorsActive = 0;
        centroid = 0;
        int i = 0;
        for (LineSensorArray lineSensorArray : lineSensorArrays) {
            numSensorsActive += lineSensorArray.getNumSensorsActive();
            int length = lineSensorArray.getNumSensors();
            for (int j = 0; j < length; j++) {
                values[i] = lineSensorArray.getValue(j);
                if (values[i]) {
                    centroid += i;
                }

                sb.append(values[i] ? '1' : '0');

                i++;
            }
        }
        if (numSensorsActive == 0) {
            centroid = 0;
        } else {
            centroid = scaling / numSensorsActive * centroid - 1;
        }

        telemetry.addData("combined center of mass", centroid);
        telemetry.addData("combined sensors active", numSensorsActive);
        string = sb.toString();
        telemetry.addData("combined values", string);
        return true;
    }

    @Override
    public boolean getValue(int i) {
        return values[i];
    }

    @Override
    public int getNumSensors() {
        return numSensors;
    }

    @Override
    public int getNumSensorsActive() {
        return numSensorsActive;
    }

    @Override
    public double getCentroid() {
        return centroid;
    }

    @Override
    public String toString() {
        return string;
    }
}
