package ftc.evlib.hardware.sensors;

import com.google.common.collect.ImmutableList;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/31/16
 *
 * Implementation of LineSensorArray that combines two SingleLineSensorArray objects
 *
 * @see SingleLineSensorArray
 * @see LineSensorArray
 */
public class DoubleLineSensorArray extends NLineSensorArray {
    public DoubleLineSensorArray(LineSensorArray leftLineSensorArray, LineSensorArray rightLineSensorArray) {
        super(ImmutableList.of(leftLineSensorArray, rightLineSensorArray));
    }
}
