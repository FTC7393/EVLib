package ftc.evlib.vision.processors;

import android.support.annotation.NonNull;

import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/26/16
 *
 * A particle (field object) that can be tracked
 */
public class Particle implements Location, Comparable<Particle> {
    public enum ParticleColor {
        RED(ImageUtil.RED),
        BLUE(ImageUtil.BLUE),
        UNKNOWN(ImageUtil.WHITE);

        ParticleColor(Scalar color) {
            this.color = color;
        }

        public final Scalar color;
    }

    private final double x, y, radius;
    private final ParticleColor color;

    public Particle(RotatedRect ellipse, ParticleColor color) {
        this(ImageUtil.centerOfRect(ellipse), (ellipse.size.width + ellipse.size.height) / 2, color);
    }

    public Particle(Point p, double radius, ParticleColor color) {
        this(p.x, p.y, radius, color);
    }

    public Particle(double x, double y, double radius, ParticleColor color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return radius * 2;
    }

    @Override
    public double getHeight() {
        return radius * 2;
    }

    public double getRadius() {
        return radius;
    }

    public ParticleColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "(" + (int) (x + 0.5) + "," + (int) (y + 0.5) + ")" + (int) (radius + 0.5) + "," + color;
    }

    @Override
    public int compareTo(@NonNull Particle another) {
        return Double.compare(another.radius, this.radius); //largest first (descending)
//        return Double.compare(this.radius, another.radius); //smallest first (ascending)
    }
}
