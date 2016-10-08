package ftc.evlib.vision.processors;

import android.support.annotation.NonNull;
import android.util.Log;

import org.opencv.core.RotatedRect;
import org.opencv.core.Size;

/**
 * Created by vandejd1 on 8/31/16.
 * FTC Team EV 7393]
 * things to consider (in order of importance):
 * ratio = short side/long side (as close to 1 as possible)
 * diff = circle size difference (as close to equal as possible)
 * angle = angle of joining line (as close to 0 as possible)
 * area = circle size (biggest)
 */
public class CircleMatch implements Comparable<CircleMatch> {
    private final Circle c1, c2;
    private final double angle, absAngle, ratio, area, dist, diff, score, expectedArea, areaError;

    public CircleMatch(RotatedRect c1, RotatedRect c2, Size size) {
        this(new Circle(c1), new Circle(c2), size);
    }

    public CircleMatch(Circle c1, Circle c2, Size size) {
        this.c1 = c1;
        this.c2 = c2;

        double angle1 = Math.toDegrees(Math.atan2(c2.p.y - c1.p.y, c2.p.x - c1.p.x));
        if (angle1 < -90) angle1 += 180;
        if (angle1 > 90) angle1 -= 180;
        angle = angle1;
        absAngle = Math.abs(angle);

//    ratio = Math.min(c1.ratio, c2.ratio);
//    ratio = (c1.ratio + c2.ratio)/2;
        ratio = Math.max(c1.ratio, c2.ratio);

        double area1 = c1.area / size.area();
        double area2 = c2.area / size.area();

//    area = Math.min(area1, area2);
//    area = (area1 + area2)/2;
        area = Math.max(area1, area2);

        diff = Math.abs(area1 - area2) / area;

        dist = Math.sqrt((c1.p.x - c2.p.x) * (c1.p.x - c2.p.x) + (c1.p.y - c2.p.y) * (c1.p.y - c2.p.y));
        expectedArea = (.028 * dist * dist + 2.4 * dist - 12) / size.area(); //(144*176);
        areaError = Math.abs(area - expectedArea) / expectedArea;

        score = Math.sqrt((
                0.9 * limit01(1 - areaError * areaError) +
                        1.0 * limit01(1 - diff * diff) +
                        1.8 * limit01(1 - absAngle / 90) +
                        1.0 * limit01(area / 0.03)
        ) / (0.9 + 1.0 + 1.8 + 1.0));
    }

    private double limit01(double val) {
        return Math.min(1, Math.max(0, val));
    }

    @Override
    public int compareTo(@NonNull CircleMatch another) {
        return Double.compare(this.score, another.score);
    }

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
