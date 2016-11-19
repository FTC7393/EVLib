package ftc.evlib;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.TelemetryImpl;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.HashMap;

import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.util.TeamColor;
import ftc.electronvolts.util.units.Distance;
import ftc.electronvolts.util.units.Time;
import ftc.electronvolts.util.units.Velocity;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.motors.MecanumMotors;
import ftc.evlib.hardware.motors.Motor;
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
    public static GyroSensor gyro() {
        return new GyroSensor() {
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
    }

    public static MecanumMotors mecanumMotors() {
        return new MecanumMotors(
                Motors.dummyMotor(), Motors.dummyMotor(), Motors.dummyMotor(), Motors.dummyMotor(),
                false, Motor.StopBehavior.BRAKE,
                new Velocity(Distance.fromMeters(1), Time.fromSeconds(1))
        );
    }

    public static MecanumControl mecanumControl() {
        return new MecanumControl(mecanumMotors());
    }

    public static FrameGrabber frameGrabber() {
        return new FrameGrabber() {

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
            public FrameGrabber.FrameGrabberMode getMode() {
                return FrameGrabber.FrameGrabberMode.STOPPED;
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
    }


    public static Servos servos() {
        return new Servos(new HashMap<ServoName, ServoControl>());
    }

    public static DistanceSensor distanceSensor() {
        return new DistanceSensor() {
            @Override
            public Distance getDistance() {
                return Distance.zero();
            }

            @Override
            public Double getValue() {
                return 0.0;
            }
        };
    }


    public static LineSensorArray lineSensorArray() {
        return new LineSensorArray() {
            @Override
            public void update() {

            }

            @Override
            public boolean[] getRawValues() {
                return new boolean[16];
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
            public double getCenterOfMass() {
                return 0;
            }
        };
    }

    private static StateName stateName() {
        return new StateName() {
            @Override
            public String name() {
                return "";
            }
        };
    }

    public static EVStateMachineBuilder evStateMachineBuilder() {
        return new EVStateMachineBuilder(
                stateName(),
                mecanumControl(),
                gyro(),
                frameGrabber(),
                servos(),
                distanceSensor(),
                lineSensorArray(),
                TeamColor.UNKNOWN
        );
    }

    public static Telemetry telemetry() {
        return new TelemetryImpl(null);
    }
}
