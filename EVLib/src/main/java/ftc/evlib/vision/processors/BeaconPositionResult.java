package ftc.evlib.vision.processors;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by vandejd1 on 8/30/16.
 * FTC Team EV 7393
 */
public class BeaconPositionResult {
    private static final double LENGTH_RATIO_EDGE = .75; //ratio of dist. between buttons to beacon width/2
    private static final double LENGTH_RATIO_INNER = .15; //ratio of dist. between buttons to (middle section width)/2
    private static final double LENGTH_RATIO_TOP = .7; //ratio of dist. between buttons to dist. from button to top
    private static final double LENGTH_RATIO_BOTTOM = .3; //ratio of dist. between buttons to dist. from button to bottom

    private final Point leftButton, rightButton, midpoint;
    private final Rect leftRect, rightRect, rect;
    private final double angleDegrees, score, width;

    BeaconPositionResult(Point leftButton, Point rightButton, Size size, double angleDegrees, double score) {
        this.leftButton = leftButton;
        this.rightButton = rightButton;
        this.angleDegrees = angleDegrees;
        this.score = score;

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

    public Mat getRotationMatrix2D() {
        return Imgproc.getRotationMatrix2D(midpoint, angleDegrees, 1);

    }

    public String getScoreString() {
        return (int) (score * 100) + "%";
    }

    @Override
    public String toString() {
        return "(" + leftButton.x + "," + leftButton.y + ")" + "\n" +
                "(" + rightButton.x + "," + rightButton.y + ")" + "\n" +
                getScoreString();
    }
}
