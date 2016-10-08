package ftc.evlib.vision.processors;

import org.opencv.core.Scalar;

import ftc.evlib.vision.ImageUtil;

/**
 * Storage class for the position and color of the beacon
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/17/16.
 */
public class BeaconColorResult {

    public enum BeaconColor {
        RED(ImageUtil.RED, "R"),
        GREEN(ImageUtil.GREEN, "G"),
        BLUE(ImageUtil.BLUE, "B"),
        UNKNOWN(ImageUtil.BLACK, "?");

        public final Scalar color;
        public final String letter;

        BeaconColor(Scalar scalar, String letter) {
            this.color = scalar;
            this.letter = letter;
        }
    }

    private final BeaconColor leftColor, rightColor;

    public BeaconColorResult() {
        this.leftColor = BeaconColor.UNKNOWN;
        this.rightColor = BeaconColor.UNKNOWN;
    }

    public BeaconColorResult(BeaconColor leftColor, BeaconColor rightColor) {
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }

    public String toString() {
        return leftColor + ", " + rightColor;
    }

    public BeaconColor getLeftColor() {
        return leftColor;
    }

    public BeaconColor getRightColor() {
        return rightColor;
    }
}
