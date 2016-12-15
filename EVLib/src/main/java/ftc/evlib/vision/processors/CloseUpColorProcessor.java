package ftc.evlib.vision.processors;

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
 * Date Created: 12/14/16
 *
 * This ImageProcessor assumes the phone camera is against one side of the beacon.
 * It decides whether the side it sees is red, blue, or unknown.
 */
public class CloseUpColorProcessor implements ImageProcessor<BeaconColorResult.BeaconColor> {
    private static final String TAG = "CloseUpColorProcessor";
    private static final double MIN_MASS = 6; //minimum mass for column sum

    private static final int DEFAULT_MIN_S = 50;
    private static final int DEFAULT_MIN_V = 150;

    //red gets 1.5 its actual mass and green gets no mass
    private static final double[] MASS_SCALE = new double[]{1.5, 0, 1};

    private final int minS, minV;

    public CloseUpColorProcessor() {
        minS = DEFAULT_MIN_S;
        minV = DEFAULT_MIN_V;
    }

    public CloseUpColorProcessor(int minS, int minV) {
        this.minS = minS;
        this.minV = minV;
    }

    /**
     * Convert to hsv
     * threshold red, green, and blue
     * find the largest mass
     *
     * @param startTime the time the frame was received
     * @param rgbaFrame the frame
     * @return a BeaconColorResult.BeaconColor telling what color the beacon is
     */
    @Override
    public ImageProcessorResult<BeaconColorResult.BeaconColor> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        //save the image in the Pictures directory
        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "1_camera", startTime);
        }

        //convert image to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);

        //calculate the hsv thresholds
        //the h value goes from 0 to 179
        //the s value goes from 0 to 255
        //the v value goes from 0 to 255

        //the values are stored as a list of min HSV and a list of max HSV
        List<Scalar> thresholdMin = new ArrayList<>();
        List<Scalar> thresholdMax = new ArrayList<>();

//        thresholdMin.add(new Scalar((300) / 2, minS, minV));
//        thresholdMax.add(new Scalar((60) / 2, 255, 255));
//
//        thresholdMin.add(new Scalar((60) / 2, minS, minV));
//        thresholdMax.add(new Scalar((180) / 2, 255, 255));
//
//        thresholdMin.add(new Scalar((180) / 2, minS, minV));
//        thresholdMax.add(new Scalar((300) / 2, 255, 255));

        //larger red range
        thresholdMin.add(new Scalar((304) / 2, minS, minV));
        thresholdMax.add(new Scalar((16) / 2, 255, 255));

        //1-value green range
        thresholdMin.add(new Scalar((60) / 2, 255, 255));
        thresholdMax.add(new Scalar((60) / 2, 255, 255));

        // large blue range
        thresholdMin.add(new Scalar((150) / 2, minS, minV));
        thresholdMax.add(new Scalar((300) / 2, 255, 255));

        //make a list of channels that are blank (used for combining binary images)
        List<Mat> rgbaChannels = new ArrayList<>();
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        //TODO combine common code between different ImageProcessor implementations

        double max = Double.MIN_VALUE;
        int maxIndex = 3;

        //variables to use inside the loop
        Mat maskedImage;
        Mat colSum = new Mat();

        int[] data = new int[3];

        //used for logging the column sum
        int[][] colSum1 = new int[hsv.width()][4];

        //column
        for (int x = 0; x < hsv.width(); x++) {
            colSum1[x][0] = x;
        }

        //loop through the rgb channels
        for (int i = 0; i < 3; i++) {
            double massScale = MASS_SCALE[i];
            if (massScale == 0) {
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

                //loop through the columns to calculate mass
                double mass = 0;
                for (int x = 0; x < hsv.width(); x++) {
                    colSum.get(0, x, data);
                    mass += data[0];
                    colSum1[x][i + 1] = data[0];
                }

                //scale the mass by the image size
                mass /= hsv.size().area();

                //each rgb channel is scaled by a certain amount
                //this allows red to be boosted since it is more often washed out
                //it also allows green to be ignored
                mass *= massScale;

                //if the mass found is greater than the max
                if (mass >= MIN_MASS && mass > max) {
                    //this mass is the new max
                    max = mass;
                    //and this index is the new maxIndex
                    maxIndex = i;
                }
            }
        }

        //log the column sum to a file
        ImageUtil.log2DArray(FileUtil.getLogsDir(), "closeUpColSum", startTime, ".csv", ImmutableList.of("column", "red", "green", "blue"), colSum1);

        //merge the 3 binary images into one
        Core.merge(rgbaChannels, rgbaFrame);

        //use the maxIndex to get the color
        BeaconColorResult.BeaconColor[] beaconColors = BeaconColorResult.BeaconColor.values();
        BeaconColorResult.BeaconColor color = beaconColors[maxIndex];

        //draw the color result bar
        int barHeight = hsv.height() / 30;
        Imgproc.rectangle(rgbaFrame, new Point(0, 0), new Point(hsv.width(), barHeight), color.color, barHeight);

        if (saveImages) {
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "2_binary", startTime);
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, color);
    }
}
