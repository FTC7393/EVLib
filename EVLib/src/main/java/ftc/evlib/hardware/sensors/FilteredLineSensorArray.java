package ftc.evlib.hardware.sensors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/25/16
 *
 * Filters out "blips" where all the sensors go high for one cycle.
 * When there is a blip, then the FilteredLineSensor reports no update and uses the previous values
 */
public class FilteredLineSensorArray implements LineSensorArray {
    private final LineSensorArray lineSensorArray;

    private final boolean[] values;

    private double centroid;
    private int numSensorsActive;

    private static final int MAX_ZERO_CYCLES = 3;
    private int zeroCycles = 0;

    public FilteredLineSensorArray(LineSensorArray lineSensorArray) {
        this.lineSensorArray = lineSensorArray;
        values = new boolean[lineSensorArray.getNumSensors()];
    }

    @Override
    public boolean update() {
        boolean wasUpdated = lineSensorArray.update();
        if (!wasUpdated) return false;

        boolean areAllActive = true;
        boolean areAllInactive = true;

        for (int i = 0; i < lineSensorArray.getNumSensors(); i++) {
            if (!lineSensorArray.getValue(i)) {
                areAllActive = false;
                break;
            }
        }


        for (int i = 0; i < lineSensorArray.getNumSensors(); i++) {
            if (lineSensorArray.getValue(i)) {
                areAllInactive = false;
                break;
            }
        }

        if (areAllActive) {
            zeroCycles = 0;
            return false;
        } else {
            if (areAllInactive) {
                zeroCycles++;
                if (zeroCycles < MAX_ZERO_CYCLES) {
                    return false;
                }
            }
            zeroCycles = 0;
            for (int i = 0; i < lineSensorArray.getNumSensors(); i++) {
                values[i] = lineSensorArray.getValue(i);
            }
            numSensorsActive = lineSensorArray.getNumSensorsActive();
            centroid = lineSensorArray.getCentroid();
            return true;
        }
    }

    @Override
    public boolean getValue(int i) {
        return values[i];
    }

    @Override
    public int getNumSensors() {
        return lineSensorArray.getNumSensors();
    }

    @Override
    public int getNumSensorsActive() {
        return numSensorsActive;
    }

    @Override
    public double getCentroid() {
        return centroid;
    }
}
