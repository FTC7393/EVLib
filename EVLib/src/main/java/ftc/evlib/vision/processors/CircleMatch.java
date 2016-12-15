package ftc.evlib.vision.processors;

import android.support.annotation.NonNull;
import android.util.Log;

import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/31/16
 *
 * things to consider (in order of importance):
 * ratio = short side/long side (as close to 1 as possible)
 * diff = circle size difference (as close to equal as possible)
 * angle = angle of joining line (as close to 0 as possible)
 * area = circle size (biggest)
 */
public class CircleMatch implements Comparable<CircleMatch> {
    private final Circle c1, c2;
    private final double angle;
    private final double ratio;
    private final double area;
    private final double dist;
    private final double diff;
    private final double score;
    private final double expectedArea;
    private final double areaError;

    /**
     * Create a CircleMatch from two RotatedRect objects
     *
     * @param c1        one RotatedRect
     * @param c2        the other RotatedRect
     * @param imageSize the size of the image to scale the circle sizes
     */
    public CircleMatch(RotatedRect c1, RotatedRect c2, Size imageSize) {
        this(new Circle(c1), new Circle(c2), imageSize);
    }

    /**
     * Create a CircleMatch from two Circle objects
     *
     * @param c1        one Circle
     * @param c2        the other Circle
     * @param imageSize the size of the image to scale the circle sizes
     */
    public CircleMatch(Circle c1, Circle c2, Size imageSize) {
        this.c1 = c1;
        this.c2 = c2;

        //calculate the angle from the horizontal
        double angle1 = Math.toDegrees(Math.atan2(c2.p.y - c1.p.y, c2.p.x - c1.p.x));
        if (angle1 < -90) angle1 += 180;
        if (angle1 > 90) angle1 -= 180;
        angle = angle1;

        //get the maximum ratio of the sides
//    ratio = Math.min(c1.ratio, c2.ratio);
//    ratio = (c1.ratio + c2.ratio)/2;
        ratio = Math.max(c1.ratio, c2.ratio);

        //scale the areas by the image size
        double area1 = c1.area / imageSize.area();
        double area2 = c2.area / imageSize.area();

        //calculate the area
//    area = Math.min(area1, area2);
//    area = (area1 + area2)/2;
        area = Math.max(area1, area2);

        //calculate the difference in sizes
        diff = Math.abs(area1 - area2) / area;

        //calculate the distance between circles
        dist = Math.sqrt((c1.p.x - c2.p.x) * (c1.p.x - c2.p.x) + (c1.p.y - c2.p.y) * (c1.p.y - c2.p.y));

        //calculate the expected area of each circle based on the distance between the circles
        expectedArea = (.028 * dist * dist + 2.4 * dist - 12) / imageSize.area(); //(144*176);

        //calculate the error from the expected value
        areaError = Math.abs(area - expectedArea) / expectedArea;

        //calculate the overall score based on all the factors
        score = Math.sqrt((
                0.9 * limit01(1 - areaError * areaError) +
                        1.0 * limit01(1 - diff * diff) +
                        1.8 * limit01(1 - Math.abs(angle) / 90) +
                        1.0 * limit01(area / 0.03)
        ) / (0.9 + 1.0 + 1.8 + 1.0));
    }

    private double limit01(double val) {
        return Math.min(1, Math.max(0, val));
    }

    /**
     * Compare matches by match score
     *
     * @param another the match to compare to
     * @return the comparison result
     */
    @Override
    public int compareTo(@NonNull CircleMatch another) {
        return Double.compare(this.score, another.score);
    }

    //getters for the different properties
    public double getScore() {
        return score;
    }

    public Circle getC1() {
        return c1;
    }

    public Circle getC2() {
        return c2;
    }

    public double getAngle() {
        return angle;
    }

    public double getRatio() {
        return ratio;
    }

    public double getArea() {
        return area;
    }

    public double getDiff() {
        return diff;
    }

    /**
     * Log all the properties in the Android Monitor
     */
    public void log() {
        Log.i("CircleMatch", "-=-=-=-=-=-=-=-=-=-=-=-=-");
        Log.i("CircleMatch", "dist: " + dist);
        Log.i("CircleMatch", "expectedArea: " + expectedArea);
        Log.i("CircleMatch", "area: " + area);
        Log.i("CircleMatch", "areaError: " + areaError);
        Log.i("CircleMatch", "ratio: " + ratio);
        Log.i("CircleMatch", "diff: " + diff);
        Log.i("CircleMatch", "angle: " + angle);
        Log.i("CircleMatch", "score: " + score);
    }
}
