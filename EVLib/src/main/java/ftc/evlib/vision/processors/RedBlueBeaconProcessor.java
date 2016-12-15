package ftc.evlib.vision.processors;

import android.util.Log;

import com.google.common.collect.ImmutableList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import ftc.evlib.util.FileUtil;
import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/26/16
 *
 * Assumes the beacon is centered in the image and finds the color (red or blue), ignoring green
 *
 * This is used to find the beacon color if the beacon position is known
 * It is handed a cropped image containing only the beacon and is optimized for that type of input
 *
 * @see VuforiaBeaconColorProcessor
 */
public class RedBlueBeaconProcessor implements ImageProcessor<BeaconColorResult> {
    private static final String TAG = "RedBlueBeaconProcessor";
    private static final double MIN_MASS = 10; //minimum mass for column sum
    private static final double MIN_CM_DIFF = 0.1; //the minimum difference between the red and blue centers of mass

    private static final int DEFAULT_MIN_S = 50;
    private static final int DEFAULT_MIN_V = 150;

    //red gets 2 times its actual mass and green gets no mass
    private static final double[] MASS_SCALE_FACTORS = new double[]{2, 0, 1};
    //each rgb channel is scaled by a certain amount
    //this allows red to be boosted since it is more often washed out
    //it also allows green to be ignored


    //variables to use inside the loop
    Mat maskedImage;
    Mat colSum = new Mat();
    int[] data = new int[3];


    private final List<Scalar> thresholdMin;
    private final List<Scalar> thresholdMax;

    public RedBlueBeaconProcessor() {
        this(DEFAULT_MIN_S, DEFAULT_MIN_V);
    }

    public RedBlueBeaconProcessor(int minS, int minV) {
        //calculate the hsv thresholds
        //the h value goes from 0 to 179
        //the s value goes from 0 to 255
        //the v value goes from 0 to 255

        //the values are stored as a list of min HSV and a list of max HSV
        thresholdMin = new ArrayList<>();
        thresholdMax = new ArrayList<>();

        //larger red range
        thresholdMin.add(new Scalar((304) / 2, minS, minV));
        thresholdMax.add(new Scalar((16) / 2, 255, 255));

        //empty green range
        thresholdMin.add(new Scalar((60) / 2, 255, 255));
        thresholdMax.add(new Scalar((60) / 2, 255, 255));

        //large blue range
        thresholdMin.add(new Scalar((150) / 2, minS, minV));
        thresholdMax.add(new Scalar((300) / 2, 255, 255));
    }

    /**
     * @param startTime the time the frame was received
     * @param rgbaFrame the frame
     * @return a BeaconColorResult telling what color the beacon is
     */
    @Override
    public ImageProcessorResult<BeaconColorResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        //save the image in the Pictures directory
        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "0_camera", startTime);
        }

        //convert image to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);

        //make a list of channels that are blank (used for combining binary images)
        List<Mat> rgbaChannels = new ArrayList<>();
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        double[] centerOfMass = new double[3];
//        Arrays.fill(centerOfMass, Double.MIN_VALUE);

        //used for logging the column sum
        int[][] colSum1 = new int[hsv.width()][4];

        //column
        for (int x = 0; x < hsv.width(); x++) {
            colSum1[x][0] = x;
        }

        //loop through the rgb channels
        for (int i = 0; i < 3; i++) {
            double massScaleFactor = MASS_SCALE_FACTORS[i];
            if (massScaleFactor == 0) {
                rgbaChannels.set(i, Mat.zeros(hsv.size(), CvType.CV_8UC1));
                for (int x = 0; x < hsv.width(); x++) {
                    colSum1[x][i + 1] = 0;
                }
            } else {

                //apply HSV thresholds
                maskedImage = new Mat();
                ImageUtil.hsvInRange(hsv, thresholdMin.get(i), thresholdMax.get(i), maskedImage);

                //copy the binary image to a channel of rgbaChannels
                rgbaChannels.set(i, maskedImage);

                //apply a column sum to the (unscaled) binary image
                Core.reduce(maskedImage, colSum, 0, Core.REDUCE_SUM, 4);

                //retrieve values to log the column sum
                for (int x = 0; x < hsv.width(); x++) {
                    colSum.get(0, x, data);
                    colSum1[x][i + 1] = data[0];
                }

                //calculate the mass
                double totalMass = 0;
                for (int x = 0; x < hsv.width(); x++) {
                    colSum.get(0, x, data);
                    totalMass += data[0];
                    centerOfMass[i] += data[0] * x;
                }

                //scale the mass by the image size and scale factor for each channel
                double scaledMass = totalMass / hsv.size().area() * massScaleFactor;
                if (scaledMass > MIN_MASS) {
                    centerOfMass[i] /= totalMass;
                } else {
                    centerOfMass[i] = Double.MIN_VALUE;
                }
                Log.i(TAG, "scaledMass[" + i + "]: " + scaledMass);
                Log.i(TAG, "centerOfMass[" + i + "]: " + centerOfMass[i]);
            }
        }

        ImageUtil.log2DArray(FileUtil.getLogsDir(), "RedBlueColSum", startTime, ".csv", ImmutableList.of("column", "red", "green", "blue"), colSum1);

        //merge the 3 binary images into one
        Core.merge(rgbaChannels, rgbaFrame);

        BeaconColorResult.BeaconColor left = BeaconColorResult.BeaconColor.UNKNOWN;
        BeaconColorResult.BeaconColor right = BeaconColorResult.BeaconColor.UNKNOWN;

        double difference = (centerOfMass[2] - centerOfMass[0]) / hsv.width();
        Log.i(TAG, "difference: " + difference);
        if (centerOfMass[0] != Double.MIN_VALUE && centerOfMass[2] != Double.MIN_VALUE && Math.abs(difference) >= MIN_CM_DIFF) {
            //if the blue is to the right of the red
            if (difference > 0) {
                left = BeaconColorResult.BeaconColor.RED;
                right = BeaconColorResult.BeaconColor.BLUE;
            } else {
                left = BeaconColorResult.BeaconColor.BLUE;
                right = BeaconColorResult.BeaconColor.RED;
            }
        }

        //draw the color result bars
        int barHeight = hsv.height() / 30;
        Imgproc.rectangle(rgbaFrame, new Point(0, 0), new Point(hsv.width() / 4, barHeight), left.color, barHeight);
        Imgproc.rectangle(rgbaFrame, new Point(hsv.width() * 3 / 4, 0), new Point(hsv.width(), barHeight), right.color, barHeight);

        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_binary", startTime);
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, new BeaconColorResult(left, right));
    }
}
