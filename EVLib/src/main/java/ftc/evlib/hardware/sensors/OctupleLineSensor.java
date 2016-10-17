package ftc.evlib.hardware.sensors;

import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/12/16
 */

public class OctupleLineSensor {
    private final I2cDevice i2cDevice;

    private boolean[] values;

    public OctupleLineSensor(I2cDevice i2cDevice) {
        this.i2cDevice = i2cDevice;
    }

    public boolean[] update() {
        i2cDevice.getCopyOfReadBuffer();
        return values;
    }

    public boolean[] getValues() {
        return values;
    }
}
