package ftc.evlib.vision.processors;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/30/16
 *
 * Stores the position of the beacon in the field of view
 */
public class BeaconPositionResult {
    private static final double LENGTH_RATIO_EDGE = .75; //ratio of dist. between buttons to beacon width/2
    private static final double LENGTH_RATIO_INNER = .15; //ratio of dist. between buttons to (middle section width)/2
    private static final double LENGTH_RATIO_TOP = .7; //ratio of dist. between buttons to dist. from button to top
    private static final double LENGTH_RATIO_BOTTOM = .3; //ratio of dist. between buttons to dist. from button to bottom

    /**
     * the beacon button points
     */
    private final Point leftButton, rightButton, midpoint;

    /**
     * the rectangles for the left and right sides, and entire beacon
     */
    private final Rect leftRect, rightRect, rect;

    /**
     * the angle from the horizontal that the beacon is at
     */
    private final double angleDegrees;

    /**
     * The width of the beacon
     */
    private final double width;

    /**
     * the overall "score" of the result
     * a higher score means it is a better match for a beacon
     */
    private final double score;

    /**
     * @param leftButton   the position of the left beacon button
     * @param rightButton  the position of the right beacon button
     * @param size         the size of the beacon
     * @param angleDegrees the angle from the horizontal
     * @param score        the overall score of the match
     */
    BeaconPositionResult(Point leftButton, Point rightButton, Size size, double angleDegrees, double score) {
        this.leftButton = leftButton;
        this.rightButton = rightButton;
        this.angleDegrees = angleDegrees;
        this.score = score;

        //calculate the midpoint of the two buttons
        midpoint = new Point((leftButton.x + rightButton.x) / 2, (leftButton.y + rightButton.y) / 2);

        //find the left and right regions of the beacon
        width = Math.sqrt(
                (this.leftButton.x - this.rightButton.x) * (this.leftButton.x - this.rightButton.x) +
                        (this.leftButton.y - this.rightButton.y) * (this.leftButton.y - this.rightButton.y)
        );

        double edge = width * LENGTH_RATIO_EDGE;
        double inner = width * LENGTH_RATIO_INNER;
        double top = width * LENGTH_RATIO_TOP;
        double bottom = width * LENGTH_RATIO_BOTTOM;

        double topY = Math.max(0, midpoint.y - top);
        double bottomY = Math.min(size.height, midpoint.y + bottom);

        double leftLeftX = Math.max(0, midpoint.x - edge);
        double leftRightX = Math.max(0, midpoint.x - inner);
        double rightLeftX = Math.min(size.width, midpoint.x + edge);
        double rightRightX = Math.min(size.width, midpoint.x + inner);

        leftRect = new Rect(
                new Point(leftLeftX, topY),
                new Point(leftRightX, bottomY)
        );
        rightRect = new Rect(
                new Point(rightLeftX, topY),
                new Point(rightRightX, bottomY)
        );
        rect = new Rect(
                new Point(leftLeftX, topY),
                new Point(rightRightX, bottomY)
        );
    }

    public Point getLeftButton() {
        return leftButton;
    }

    public Point getRightButton() {
        return rightButton;
    }

    public Point getMidpoint() {
        return midpoint;
    }

    public Rect getLeftRect() {
        return leftRect;
    }

    public Rect getRightRect() {
        return rightRect;
    }

    public Rect getRect() {
        return rect;
    }

    public double getAngleDegrees() {
        return angleDegrees;
    }

    public double getScore() {
        return score;
    }

    public double getWidth() {
        return width;
    }

    /**
     * @return the rotation matrix to rotate the beacon around the midpoint
     */
    public Mat getRotationMatrix2D() {
        return Imgproc.getRotationMatrix2D(midpoint, angleDegrees, 1);
    }

    /**
     * @return the score as a percentage string with 2 decimal places
     */
    public String getScoreString() {
        return (int) (score * 100 * 100) / 100 + "%";
    }

    /**
     * @return the result formatted as a String
     */
    @Override
    public String toString() {
        return "(" + leftButton.x + "," + leftButton.y + ")" + "\n" +
                "(" + rightButton.x + "," + rightButton.y + ")" + "\n" +
                getScoreString();
    }
}
