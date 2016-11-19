package ftc.evlib.vision.processors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/26/16
 *
 * A location on the camera field of view for tracking
 */
public interface Location {
    double getX();

    double getY();

    double getWidth();

    double getHeight();
}
