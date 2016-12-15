package ftc.evlib.driverstation;

import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import ftc.electronvolts.util.ResultReceiver;
import ftc.evlib.Fake;
import ftc.evlib.vision.framegrabber.VuforiaFrameFeeder;
import ftc.evlib.vision.processors.BeaconColorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * This class gives everything access to the telemetry
 * Put this in your opmode:
 *
 * Telem.telemetry = telemetry;
 *
 * To set the telemetry variable for anything to use.
 *
 * @see Telemetry
 */
public class Telem {
    //this will fail silently and move on if you write to it before it is set by the opmode
    public static Telemetry telemetry = Fake.TELEMETRY;

    //this will cause errors if you write to it before it is set by the opmode
//    public static Telemetry telemetry = null;

    /**
     * Display whether or not a ResultReceiver is ready on the driver station telemetry
     *
     * @param receiver the ResultReceiver
     * @param caption  the caption of the telemetry item
     * @param ready    the value of the telemetry item if ready
     * @param notReady the value of the telemetry item if not ready
     */
    public static void displayReceiverReadiness(ResultReceiver receiver, String caption, String ready, String notReady) {
        if (receiver.isReady()) {
            telemetry.addData(caption, ready);
        } else {
            telemetry.addData(caption, notReady);
        }
    }

    /**
     * Display whether or not a Vuforia ResultReceiver is ready on the driver station telemetry
     *
     * @param receiver the ResultReceiver of the VuforiaFrameFeeder type
     */
    public static void displayVuforiaReadiness(ResultReceiver<VuforiaFrameFeeder> receiver) {
        displayReceiverReadiness(receiver, "vuforia", "ready", "NOT INITIALIZED!!!");
    }

    /**
     * Display a BeaconColorResult on the telemetry
     *
     * @param receiver the ResultReceiver that contains the BeaconColorResult
     */
    public static void displayBeaconColorResult(ResultReceiver<BeaconColorResult> receiver) {
        if (receiver.isReady()) {
            BeaconColorResult result = receiver.getValue();
            telemetry.addData("leftColor", result.getLeftColor());
            telemetry.addData("rightColor", result.getRightColor());
        } else {
            telemetry.addData("receiver not ready", "");
        }
    }


    /**
     * Display a BeaconColor on the telemetry
     *
     * @param receiver the ResultReceiver that contains the BeaconColor
     */
    public static void displayBeaconColor(ResultReceiver<BeaconColorResult.BeaconColor> receiver) {
        if (receiver.isReady()) {
            telemetry.addData("color", receiver.getValue());
        } else {
            telemetry.addData("receiver not ready", "");
        }
    }

    /**
     * Display whether or not a gyro sensor is calibrated on the telemetry screen
     *
     * @param gyro the gyro sensor
     */
    public static void displayGyroIsCalibrated(GyroSensor gyro) {
        if (gyro.isCalibrating()) {
            telemetry.addData("Gyro state", "CALIBRATING");
        } else {
            telemetry.addData("Gyro state", "ready");
        }
    }
}
