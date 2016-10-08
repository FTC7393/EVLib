package ftc.evlib.driverstation;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.TelemetryImpl;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 */
public class Telem {
    //this will fail silently and move on if you write to it before it is set by the opmode
    public static Telemetry telemetry = new TelemetryImpl(null);

    //this will cause errors if you write to it before it is set by the opmode
//    public static Telemetry telemetry = null;
}
