package ftc.evlib.hardware.config;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

import ftc.evlib.hardware.sensors.Accelerometer;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * Minimum hardware that every robot has.
 * This class can be extended to add more hardware devices for each specific robot.
 * Then each subclass can be used by multiple OpModes.
 */
public abstract class RobotCfg {
    private final Context phoneContext;
    private final SensorManager phoneSensorManager;
    private final Sensor phoneAccelerometer;
    private final PowerManager phonePowerManager;
    private final PowerManager.WakeLock phoneWakeLock;
    private final Accelerometer accelerometer;

    public RobotCfg(HardwareMap hardwareMap) {
        //get the phone accelerometer and wakelock
        phoneContext = hardwareMap.appContext;
        phoneSensorManager = (SensorManager) phoneContext.getSystemService(Context.SENSOR_SERVICE);
        phoneAccelerometer = phoneSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = new Accelerometer(phoneSensorManager, phoneAccelerometer);

        phonePowerManager = (PowerManager) phoneContext.getSystemService(Context.POWER_SERVICE);
        phoneWakeLock = phonePowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FTC_APP_WAKELOCK");
    }

    public Context getPhoneContext() {
        return phoneContext;
    }

    public SensorManager getPhoneSensorManager() {
        return phoneSensorManager;
    }

    public Sensor getPhoneAccelerometer() {
        return phoneAccelerometer;
    }

    public PowerManager getPhonePowerManager() {
        return phonePowerManager;
    }

    public PowerManager.WakeLock getPhoneWakeLock() {
        return phoneWakeLock;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    //this does not need to be overridden
    public ServoControl getServo(ServoName servoName) {
        return getServos().getServoMap().get(servoName);
    }

    //this should be overridden to return your robot's servos
    public Servos getServos() {
        return new Servos(new HashMap<ServoName, ServoControl>());
    }

    //act() and stop() will be called during the opmode

    //act can be used to update sensors, motors, display telemetry, etc.
    public abstract void act();

    //stop can be used to stop motors, close files, etc.
    public abstract void stop();
}
