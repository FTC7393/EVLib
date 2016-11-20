package ftc.evlib.vision.processors;

import android.support.annotation.NonNull;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/31/16
 *
 * A circle with a point and a radius
 */
public class Circle implements Comparable<Circle> {
    public final RotatedRect ellipse;
    public final Rect c;
    public final Point p;
    public final double area, ratio;

    /**
     * Create a Circle from an ellipse
     *
     * @param ellipse the ellipse as a RotatedRect object
     */
    public Circle(RotatedRect ellipse) {
        this.ellipse = ellipse;
        this.c = ellipse.boundingRect();
        p = ImageUtil.centerOfRect(c);
        area = c.area();
        //find the ratio of the width to the height
        double ratio1 = ellipse.size.width / ellipse.size.height;

        //and inverting it if it is greater than 1
        if (ratio1 > 1) {
            ratio = 1 / ratio1;
        } else {
            ratio = ratio1;
        }

    }

    /**
     * Compare circles by area
     *
     * @param another the other circle
     * @return an integer that represents the comparison result
     */
    @Override
    public int compareTo(@NonNull Circle another) {
        return Double.compare(another.area, this.area);
//    return Double.compare(this.ratio, another.ratio);
    }
}