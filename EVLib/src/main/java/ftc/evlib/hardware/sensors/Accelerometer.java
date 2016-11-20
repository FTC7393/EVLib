package ftc.evlib.hardware.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 1/26/16
 * <p>
 * Takes the values from the phone accelerometer and stores them for our code to retrieve.
 *
 * @see ftc.evlib.hardware.config.RobotCfg
 */
public class Accelerometer implements SensorEventListener {
    private double x = 0, y = 0, z = 0;
    private boolean ready = false;

    public Accelerometer(SensorManager sensorManager, Sensor accelerometer) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * @return true if the sensor has gotten its first reading
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * This is called when the phone gets a sensor update
     *
     * @param sensorEvent the event with the accelerometer data
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //if there was an accelerometer event, store the values
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
            ready = true;
        }
    }

    /**
     * @return the accelerometer x value
     */
    public double getX() {
        return x;
    }

    /**
     * @return the accelerometer y value
     */
    public double getY() {
        return y;
    }

    /**
     * @return the accelerometer z value
     */
    public double getZ() {
        return z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
