package ftc.evlib.vision.processors;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/30/16
 *
 * Stores the position and the color of the beacon
 */
public class BeaconResult {
    private final BeaconColorResult colorResult;
    private final BeaconPositionResult positionResult;

    /**
     * @param colorResult    the beacon's color
     * @param positionResult the beacon's position
     */
    public BeaconResult(BeaconColorResult colorResult, BeaconPositionResult positionResult) {
        this.colorResult = colorResult;
        this.positionResult = positionResult;
    }

    public BeaconColorResult getColorResult() {
        return colorResult;
    }

    public BeaconPositionResult getPositionResult() {
        return positionResult;
    }

    /**
     * @return the result formatted as a String with 3 lines
     */
    @Override
    public String toString() {
        if (positionResult != null) {
            return colorResult.getLeftColor().letter + "(" + positionResult.getLeftButton().x + "," + positionResult.getLeftButton().y + ")" + "\n" +
                    colorResult.getRightColor().letter + "(" + positionResult.getRightButton().x + "," + positionResult.getRightButton().y + ")" + "\n" +
                    positionResult.getScoreString();
        } else {
            return colorResult.toString();
        }
    }
}
