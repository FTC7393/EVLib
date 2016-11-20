package ftc.evlib.vision.processors;

import org.opencv.core.Point;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/29/16.
 *
 * Stores the hue from each side of the beacon
 */
public class HueBeaconResult {
    private final double leftHue, rightHue;
    private final Point leftButtonPoint, rightButtonPoint;
    private final boolean beaconFound;

    public HueBeaconResult(double leftHue, double rightHue) {
        this.leftHue = leftHue;
        this.rightHue = rightHue;
        this.leftButtonPoint = null;
        this.rightButtonPoint = null;
        beaconFound = false;
    }

    public HueBeaconResult(double leftHue, double rightHue, Point buttonPoint1, Point buttonPoint2) {
        this.leftHue = leftHue;
        this.rightHue = rightHue;
        if (buttonPoint1 == null || buttonPoint2 == null) {
            beaconFound = false;
            this.leftButtonPoint = null;
            this.rightButtonPoint = null;
        } else {
            beaconFound = true;
            if (buttonPoint1.x > buttonPoint2.x) {
                this.leftButtonPoint = buttonPoint2;
                this.rightButtonPoint = buttonPoint1;
            } else {
                this.leftButtonPoint = buttonPoint1;
                this.rightButtonPoint = buttonPoint2;
            }
        }
    }

    public double getLeftHue() {
        return leftHue;
    }

    public double getRightHue() {
        return rightHue;
    }

    public Point getLeftButtonPoint() {
        return leftButtonPoint;
    }

    public Point getRightButtonPoint() {
        return rightButtonPoint;
    }

    public boolean isBeaconFound() {
        return beaconFound;
    }

    public String toString() {
        if (leftButtonPoint == null || rightButtonPoint == null) {
            return leftHue + "," + rightHue;
        } else {
            return (int) leftHue + "(" + (int) leftButtonPoint.x + "," + (int) leftButtonPoint.y + ")\n" +
                    (int) rightHue + "(" + (int) rightButtonPoint.x + "," + (int) rightButtonPoint.y + ")";
        }
    }
}
