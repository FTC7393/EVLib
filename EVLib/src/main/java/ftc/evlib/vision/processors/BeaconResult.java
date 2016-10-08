package ftc.evlib.vision.processors;

/**
 * Created by vandejd1 on 8/30/16.
 * FTC Team EV 7393
 */
public class BeaconResult {
    private final BeaconColorResult colorResult;
    private final BeaconPositionResult positionResult;

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
