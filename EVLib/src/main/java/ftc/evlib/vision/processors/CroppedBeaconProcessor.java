package ftc.evlib.vision.processors;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/29/16
 *
 * This is used to find the beacon color if the beacon position is known
 * It is handed a cropped image containing only the beacon and is optimized for that type of input
 *
 * @see VuforiaBeaconColorProcessor
 */
public class CroppedBeaconProcessor implements ImageProcessor<BeaconColorResult> {
    private static final String TAG = "CroppedBeaconProcessor";
    private static final double MIN_MASS = 6; //minimum mass for column sum
    private static final double BEACON_SIDE_RATIO = 0.4;

    private static final int DEFAULT_MIN_S = 50;
    private static final int DEFAULT_MIN_V = 150;

    //red gets 1.5 its actual mass and green gets no mass
    private static final double[] MASS_SCALE = new double[]{1.5, 0, 1};

    private final int minS, minV;

    public CroppedBeaconProcessor() {
        minS = DEFAULT_MIN_S;
        minV = DEFAULT_MIN_V;
    }

    public CroppedBeaconProcessor(int minS, int minV) {
        this.minS = minS;
        this.minV = minV;
    }

    /**
     * Convert to hsv
     * threshold red, green, and blue
     * find the largest mass on the left and right
     *
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

        //calculate beacon sides from image dimensions
        int leftX = (int) (BEACON_SIDE_RATIO * hsv.width());
        int rightX = (int) ((1 - BEACON_SIDE_RATIO) * hsv.width());


        //make a list of channels that are blank (used for combining binary images)
        List<Mat> rgbaChannels = new ArrayList<>();
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(null);
        rgbaChannels.add(Mat.zeros(hsv.size(), CvType.CV_8UC1));

        double[] max = new double[2];
        int[] maxIndex = new int[2];

        Arrays.fill(max, Double.MIN_VALUE);
        Arrays.fill(maxIndex, 3);

        //variables to use inside the loop
        Mat maskedImage;
        Mat colSum = new Mat();

        int[] data = new int[3];

        //loop through the rgb channels
        for (int i = 0; i < 3; i++) {
            double massScale = MASS_SCALE[i];
            if (massScale == 0) {
                rgbaChannels.set(i, Mat.zeros(hsv.size(), CvType.CV_8UC1));
            } else {

                //apply HSV thresholds
                maskedImage = new Mat();
                ImageUtil.hsvInRange(hsv, thresholdMin.get(i), thresholdMax.get(i), maskedImage);

                //copy the binary image to a channel of rgbaChannels
                rgbaChannels.set(i, maskedImage);

                //apply a column sum to the (unscaled) binary image
                Core.reduce(maskedImage, colSum, 0, Core.REDUCE_SUM, 4);

//                ImageUtil.saveImage(TAG, ImageUtil.graphColSum(colSum, hsv.height()), Imgproc.COLOR_GRAY2BGR, "1_column" + i, startTime);

                //loop through left and right to calculate mass
                int start = 0;
                int end = leftX;
                for (int j = 0; j < 2; j++) {
                    //calculate the mass
                    double mass = 0;
                    for (int x = start; x < end; x++) {
                        colSum.get(0, x, data);
                        mass += data[0];
                    }

                    //scale the mass by the image size
                    mass /= hsv.size().area();

                    //each rgb channel is scaled by a certain amount
                    //this allows red to be boosted since it is more often washed out
                    //it also allows green to be ignored
                    mass *= massScale;

                    //if the mass found is greater than the max for this side
                    if (mass >= MIN_MASS && mass > max[j]) {
                        //this mass is the new max for this side
                        max[j] = mass;
                        //and this index is the new maxIndex for this side
                        maxIndex[j] = i;
                    }

                    start = rightX;
                    end = hsv.width();
                }
            }
        }
        //merge the 3 binary images into one
        Core.merge(rgbaChannels, rgbaFrame);

        //use the minIndex and maxIndex to get the left and right colors
        BeaconColorResult.BeaconColor[] beaconColors = BeaconColorResult.BeaconColor.values();
        BeaconColorResult.BeaconColor left = beaconColors[maxIndex[0]];
        BeaconColorResult.BeaconColor right = beaconColors[maxIndex[1]];

        //draw left-right divider lines
        Imgproc.rectangle(rgbaFrame, new Point(leftX, 0), new Point(leftX, hsv.height()), ImageUtil.WHITE, hsv.width() / 30);
        Imgproc.rectangle(rgbaFrame, new Point(rightX, 0), new Point(rightX, hsv.height()), ImageUtil.WHITE, hsv.width() / 30);

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
