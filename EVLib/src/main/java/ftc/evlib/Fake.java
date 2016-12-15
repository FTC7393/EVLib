package ftc.evlib;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.TelemetryImpl;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

import ftc.electronvolts.statemachine.State;
import ftc.electronvolts.statemachine.StateMachine;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.TeamColor;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.Distance;
import ftc.electronvolts.util.units.Time;
import ftc.electronvolts.util.units.Velocity;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.mechanisms.Shooter;
import ftc.evlib.hardware.motors.MecanumMotors;
import ftc.evlib.hardware.motors.Motors;
import ftc.evlib.hardware.sensors.DistanceSensor;
import ftc.evlib.hardware.sensors.LineSensorArray;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.statemachine.EVStateMachineBuilder;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/9/16
 *
 * Created fake instances of different interfaces
 */
public class Fake {
    public static final GyroSensor GYRO_SENSOR = new GyroSensor() {
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
    };


    public static final MecanumMotors MECANUM_MOTORS = new MecanumMotors(
            Motors.dummyWithoutEncoder(), Motors.dummyWithoutEncoder(), Motors.dummyWithoutEncoder(), Motors.dummyWithoutEncoder(),
            false, new Velocity(Distance.fromMeters(1), Time.fromSeconds(1))
    );


    public static final MecanumControl MECANUM_CONTROL = new MecanumControl(MECANUM_MOTORS);


    public static final FrameGrabber FRAME_GRABBER = new FrameGrabber() {

        @Override
        public FrameGrabber.CameraOrientation getCameraOrientation() {
            return FrameGrabber.CameraOrientation.PORTRAIT_UP;
        }

        @Override
        public boolean isIgnoreOrientationForDisplay() {
            return false;
        }

        @Override
        public boolean isSaveImages() {
            return false;
        }

        @Override
        public ImageProcessor getImageProcessor() {
            return null;
        }

        @Override
        public Mode getMode() {
            return Mode.STOPPED;
        }

        @Override
        public void setCameraOrientation(FrameGrabber.CameraOrientation cameraOrientation) {

        }

        @Override
        public void setIgnoreOrientationForDisplay(boolean ignoreOrientationForDisplay) {

        }

        @Override
        public void setSaveImages(boolean saveImages) {

        }

        @Override
        public void setImageProcessor(ImageProcessor imageProcessor) {

        }

        @Override
        public void grabSingleFrame() {

        }

        @Override
        public void grabContinuousFrames() {

        }

        @Override
        public void throwAwayFrames() {

        }

        @Override
        public void stopFrameGrabber() {

        }

        @Override
        public boolean isResultReady() {
            return false;
        }

        @Override
        public ImageProcessorResult getResult() {
            return null;
        }

        @Override
        public Mat receiveFrame(Bitmap bitmap) {
            Mat tmp = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, tmp);
            return tmp;
        }

        @Override
        public Mat receiveFrame(Mat inputFrame) {
            return inputFrame;
        }
    };


    public static final Servos SERVOS = new Servos(new HashMap<ServoName, ServoControl>());


    public static final DistanceSensor DISTANCE_SENSOR = new DistanceSensor() {
        @Override
        public Distance getDistance() {
            return Distance.zero();
        }

        @Override
        public Double getValue() {
            return 0.0;
        }
    };


    public static final LineSensorArray LINE_SENSOR_ARRAY = new LineSensorArray() {
        @Override
        public boolean update() {
            return true;
        }

        @Override
        public boolean getValue(int i) {
            return false;
        }

        @Override
        public int getNumSensors() {
            return 0;
        }

        @Override
        public int getNumSensorsActive() {
            return 0;
        }

        @Override
        public double getCentroid() {
            return 0;
        }
    };


    public static final StateName STATE_NAME = new StateName() {
        @Override
        public String name() {
            return "Fake";
        }

        @Override
        public String toString() {
            return "Fake";
        }
    };

    public static final Shooter SHOOTER = new Shooter() {
        @Override
        public void act() {

        }

        @Override
        public void shoot(int shots) {

        }

        @Override
        public void stop() {

        }

        @Override
        public int getShots() {
            return 0;
        }

        @Override
        public String getModeName() {
            return "Fake";
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public void initialize() {

        }
    };

    public static final EVStateMachineBuilder EV_STATE_MACHINE_BUILDER = new EVStateMachineBuilder(
            STATE_NAME, TeamColor.UNKNOWN, Angle.fromDegrees(2), GYRO_SENSOR, FRAME_GRABBER, SERVOS, DISTANCE_SENSOR, MECANUM_CONTROL, SHOOTER
    );

    public static final State STATE = new State() {
        @Override
        public StateName act() {
            return STATE_NAME;
        }

        @Override
        public StateName getName() {
            return STATE_NAME;
        }
    };


    public static final Telemetry TELEMETRY = new TelemetryImpl(null);

    public static final StateMachine STATE_MACHINE;

    static {
        Map<StateName, State> stateMap = new HashMap<>();
        stateMap.put(STATE_NAME, STATE);
        STATE_MACHINE = new StateMachine(stateMap, STATE_NAME);
    }
}
