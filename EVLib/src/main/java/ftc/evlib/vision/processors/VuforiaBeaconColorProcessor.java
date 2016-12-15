package ftc.evlib.vision.processors;

import android.util.Log;

import com.vuforia.Matrix34F;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ftc.electronvolts.util.Utility;
import ftc.electronvolts.util.Vector2D;
import ftc.evlib.vision.ImageUtil;
import ftc.evlib.vision.framegrabber.VuforiaFrameFeeder;

import static ftc.evlib.vision.framegrabber.VuforiaFrameFeeder.beacons;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/7/16
 */

public class VuforiaBeaconColorProcessor implements ImageProcessor<BeaconColorResult> {
    private static final String TAG = "VuforiaBeaconColor";
    private final VuforiaFrameFeeder vuforia;
    private final RedBlueBeaconProcessor beaconProcessor = new RedBlueBeaconProcessor(50, 100); //(5, 25); //(15, 15); //(51, 76);
    private int resultsFound = 0;
    private final List<BeaconColorResult> results = new ArrayList<>();

    //the units are "beacon picture widths"         tl     tr     bl     br
    private static final double[] beaconXOffset = {+0.00, +0.00, +0.00, +0.00};
    //    private static final double[] beaconXOffset = {+0.10, -0.10, +0.10, -0.10};
    private static final double[] beaconYOffset = {-1.00, -1.00, -1.14, -1.14};

    private BeaconName beaconName = BeaconName.WHEELS;

    public void setBeaconName(BeaconName beaconName) {
        this.beaconName = beaconName;
    }

    public void reset() {
        resultsFound = 0;
        results.clear();
    }

    public VuforiaBeaconColorProcessor(VuforiaFrameFeeder vuforia) {
        this.vuforia = vuforia;
    }

    public int getResultsFound() {
        return resultsFound;
    }

    public BeaconColorResult getAverageResult() {
        //TODO simplify VuforiaBeaconColorProcessor.getAverageResult()
        Map<BeaconColorResult.BeaconColor, Integer> leftColors = new HashMap<>();
        Map<BeaconColorResult.BeaconColor, Integer> rightColors = new HashMap<>();

        for (BeaconColorResult result : results) {
            BeaconColorResult.BeaconColor leftColor = result.getLeftColor();
            BeaconColorResult.BeaconColor rightColor = result.getRightColor();
            int num = 0;
            if (leftColors.containsKey(leftColor)) {
                num = leftColors.get(leftColor);
            }
            leftColors.put(leftColor, num + 1);

            num = 0;
            if (rightColors.containsKey(rightColor)) {
                num = rightColors.get(rightColor);
            }
            rightColors.put(rightColor, num + 1);
        }

        int leftMax = 0;
        BeaconColorResult.BeaconColor leftMaxColor = BeaconColorResult.BeaconColor.UNKNOWN;
        for (Map.Entry<BeaconColorResult.BeaconColor, Integer> entry : leftColors.entrySet()) {
            BeaconColorResult.BeaconColor color = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0) {
                if (leftMax * 1.0 / quantity < 0.9) {
                    leftMax = quantity;
                    leftMaxColor = color;
                }
            }
        }
        int rightMax = 0;
        BeaconColorResult.BeaconColor rightMaxColor = BeaconColorResult.BeaconColor.UNKNOWN;
        for (Map.Entry<BeaconColorResult.BeaconColor, Integer> entry : rightColors.entrySet()) {
            BeaconColorResult.BeaconColor color = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0) {
                if (rightMax * 1.0 / quantity < 0.9) {
                    rightMax = quantity;
                    rightMaxColor = color;
                }
            }
        }
        return new BeaconColorResult(leftMaxColor, rightMaxColor);
    }

    @Override
    public ImageProcessorResult<BeaconColorResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        VuforiaTrackable beacon = beacons.get(beaconName);

        Log.i(TAG, beaconName.name());
//        Log.i(TAG, beacon.getName());

        OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beacon.getListener()).getRawPose();

        if (pose == null) {
            return new ImageProcessorResult<>(startTime, rgbaFrame, new BeaconColorResult());
        }

        resultsFound++;

        Matrix34F rawPose = new Matrix34F();
        float[] poseData = Arrays.copyOfRange(pose.transposed().getData(), 0, 12);
        rawPose.setData(poseData);

        List<Vector2D> imageCorners = vuforia.getImageCorners(rawPose);

        Log.i(TAG, "tl: " + imageCorners.get(0));
        Log.i(TAG, "tr: " + imageCorners.get(1));
        Log.i(TAG, "bl: " + imageCorners.get(2));
        Log.i(TAG, "br: " + imageCorners.get(3));

        //get average width from the top width and bottom width
        double w = ((imageCorners.get(1).getX() - imageCorners.get(0).getX()) + (imageCorners.get(3).getX() - imageCorners.get(2).getX())) / 2;
        //same for height
        double h = ((imageCorners.get(2).getY() - imageCorners.get(0).getY()) + (imageCorners.get(3).getY() - imageCorners.get(1).getY())) / 2;

        Log.i(TAG, "beacon picture scaled size: " + new Vector2D(w, h));
        List<Vector2D> beaconCorners = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            beaconCorners.add(new Vector2D(
                    imageCorners.get(i).getX() + beaconXOffset[i] * w,
                    imageCorners.get(i).getY() + beaconYOffset[i] * h
            ));
        }


        Log.i(TAG, "beacon tl: " + beaconCorners.get(0));
        Log.i(TAG, "beacon tr: " + beaconCorners.get(1));
        Log.i(TAG, "beacon bl: " + beaconCorners.get(2));
        Log.i(TAG, "beacon br: " + beaconCorners.get(3));

//                r = 0;
//
//                for (Vector2D corner : beaconCorners) {
//                    r += 2;
//                    Imgproc.circle(rgbaFrame, new Point((int) corner.getX(), (int) corner.getY()), r, ImageUtil.GREEN, 10);
//                }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Vector2D corner : beaconCorners) {
            int x = (int) corner.getX();
            int y = (int) corner.getY();

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;

            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        minX = (int) Utility.limit(minX, 0, rgbaFrame.width());
        maxX = (int) Utility.limit(maxX, 0, rgbaFrame.width());

        minY = (int) Utility.limit(minY, 0, rgbaFrame.height());
        maxY = (int) Utility.limit(maxY, 0, rgbaFrame.height());

        Log.i(TAG, "minX: " + minX);
        Log.i(TAG, "maxX: " + maxX);
        Log.i(TAG, "minY: " + minY);
        Log.i(TAG, "maxY: " + maxY);


        Rect rectCrop = new Rect(minX, minY, maxX - minX, maxY - minY);
        Log.i(TAG, beacon.getName() + " crop: " + rectCrop.x + "," + rectCrop.y + ":" + rectCrop.width + "x" + rectCrop.height);

        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "0_full_picture", startTime);
        }

        Mat crop = new Mat(rgbaFrame, rectCrop);
        ImageProcessorResult<BeaconColorResult> result = beaconProcessor.process(startTime, crop, saveImages);
        results.add(result.getResult());

        Log.i(TAG, "result: " + result.getResult());
        return result;
//                return new ImageProcessorResult<>(startTime, rgbaFrame, beaconProcessor.process(startTime, crop, true).getResult().getColorResult());
//                ImageUtil.saveImage(TAG, crop, Imgproc.COLOR_RGBA2BGR, "crop", startTime);


    }
}
