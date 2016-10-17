package ftc.evlib.hardware.sensors;

import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/12/16
 */

public class FakeGyroSensor implements GyroSensor {
    @Override
    public void calibrate() {

    }

    @Override
    public boolean isCalibrating() {
        return false;
    }

    @Override
    public int getHeading() {
        return 0;
    }

    @Override
    public double getRotationFraction() {
        return 0;
    }

    @Override
    public int rawX() {
        return 0;
    }

    @Override
    public int rawY() {
        return 0;
    }

    @Override
    public int rawZ() {
        return 0;
    }

    @Override
    public void resetZAxisIntegrator() {

    }

    @Override
    public String status() {
        return null;
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return "Fake Gyro";
    }

    @Override
    public String getConnectionInfo() {
        return "";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
