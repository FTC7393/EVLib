package ftc.evlib.driverstation;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import ftc.evlib.Fake;

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
 */
public class Telem {
    //this will fail silently and move on if you write to it before it is set by the opmode
    public static Telemetry telemetry = Fake.telemetry();

    //this will cause errors if you write to it before it is set by the opmode
//    public static Telemetry telemetry = null;
}
