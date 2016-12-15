package ftc.evlib.vision.processors;

import org.opencv.core.Scalar;

import ftc.electronvolts.util.TeamColor;
import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/17/16.
 *
 * Storage class for the color of the beacon
 */
public class BeaconColorResult {
    /**
     * Storage class for the color of one beacon
     */
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

        /**
         * Converts a TeamColor to a BeaconColor
         *
         * @param teamColor the TeamColor to convert
         * @return the corresponding BeaconColor
         */
        public static BeaconColor fromTeamColor(TeamColor teamColor) {
            switch (teamColor) {
                case RED:
                    return RED;
                case BLUE:
                    return BLUE;
                default:
                    return UNKNOWN;
            }
        }

        /**
         * @return the TeamColor corresponding to this BeaconColor
         */
        public BeaconColor toTeamColor() {
            return toTeamColor(this);
        }

        /**
         * Converts a BeaconColor to a TeamColor
         *
         * @param beaconColor the BeaconColor to convert
         * @return the corresponding TeamColor
         */
        public static BeaconColor toTeamColor(BeaconColor beaconColor) {
            switch (beaconColor) {
                case RED:
                    return RED;
                case BLUE:
                    return BLUE;
                default:
                    return UNKNOWN;
            }
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
        return leftColor + "|" + rightColor;
    }

    public BeaconColor getLeftColor() {
        return leftColor;
    }

    public BeaconColor getRightColor() {
        return rightColor;
    }
}
