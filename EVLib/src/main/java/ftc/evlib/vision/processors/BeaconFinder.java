package ftc.evlib.vision.processors;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ftc.evlib.util.StepTimer;
import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/30/16
 *
 * Finds only the position of the beacon in the field of view
 */
public class BeaconFinder implements ImageProcessor<BeaconPositionResult> {
    private static final String TAG = "BeaconFinder";
    private static final int THICKNESS = 2;

    //thresholds and parameters to tune the beacon detector
    private static final int MAX_BLACK_V = 125; //max hsv value of the black buttons
    private static final int BLUR_AMOUNT = 3; //used to blur the binary image
    private static final int MAX_BLACK_V_BLUR = 80; //post-blur hsv value threshold
    private static final double MIN_RATIO = .7; //min ratio of ellipse sides to be considered a circle
    private static final int MAX_NUM_CIRCLES = 20;
    private static final double MIN_SCORE = 0.84;
    private static final double FRAME_AREA_SCALE = 1E-4; //scaling factor for the frame's area
    private static final double MIN_AREA = 0; //min area for circles to be considered
    private static final double MAX_AREA = 500; //max area for circles
//  private static final double MAX_DEGREES = 15; //max slope for matched circle sizes
//  private static final double MAX_DIFF = .8; //max fraction for matched circle sizes to differ
//  //  private static final Scalar HSV_MIN = new Scalar(0, 40, 150);
//  private static final Scalar HSV_MIN = new Scalar(0, 0, 0);
//  private static final Scalar HSV_MAX = new Scalar(179, 255, 255);
//  private static final double MIN_MASS = 6; //minimum mass for column sum

    /**
     * Timer to log the time each step takes
     */
    private final StepTimer stepTimer = new StepTimer(TAG);

    @Override
    public ImageProcessorResult<BeaconPositionResult> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        if (saveImages) {
            stepTimer.start();
            //save the raw camera image for logging
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "00_camera", startTime);
            stepTimer.log("save 00");
        }

        stepTimer.start();
        //convert image to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsv, Imgproc.COLOR_RGB2HSV);
        stepTimer.log("rgb2hsv");

        stepTimer.start();
        //threshold the hsv image to find black regions
        Mat blackThreshImage = new Mat();
        Core.inRange(hsv, new Scalar(0, 0, 0), new Scalar(179, 255, MAX_BLACK_V), blackThreshImage);

        //blur the image and re-threshold to "de-bounce" the noisy sections
        Imgproc.blur(blackThreshImage, blackThreshImage, new Size(BLUR_AMOUNT, BLUR_AMOUNT));
        Imgproc.threshold(blackThreshImage, blackThreshImage, MAX_BLACK_V_BLUR, 255, Imgproc.THRESH_BINARY_INV);
        stepTimer.log("threshold & blur");

        if (saveImages) {
            stepTimer.start();
            //save the threshold image for logging
            ImageUtil.saveImage(TAG, blackThreshImage, Imgproc.COLOR_GRAY2BGR, "01_threshold", startTime);
            stepTimer.log("save 01");
        }

        stepTimer.start();
        //find contours (edges between black and non-black)
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(blackThreshImage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(rgbaFrame, contours, -1, ImageUtil.YELLOW, THICKNESS);
        stepTimer.log("contours");

        stepTimer.start();
        double frameArea = FRAME_AREA_SCALE * rgbaFrame.size().area();
        //loop through the contours to find the circular ones
        List<Circle> circles = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            //convert MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour = new MatOfPoint2f();
            contour.fromList(contours.get(i).toList());

            RotatedRect ellipse = null;
            boolean isValid = false;
            //fitting an ellipse requires at least 5 points
            if (contour.height() >= 5) {
                //fit an ellipse to the contour points
                ellipse = Imgproc.fitEllipse(contour);
                double area = ellipse.size.area() / frameArea;
//        Log.i(TAG, "Ellipse Area: " + area);
                //filter out ellipses that are too big or too small
                if (area >= MIN_AREA && area <= MAX_AREA) {
                    //find the ratio of the shortest side to the longest side
                    //by finding the ratio of the width to the height
                    double ratio = ellipse.size.width / ellipse.size.height;

                    //and inverting it if it is greater than 1
                    if (ratio > 1) ratio = 1 / ratio;

                    //reject ellipses that are not circular enough
                    if (ratio >= MIN_RATIO) {
                        isValid = true;
                    }
                }
            }

            //draw the ellipse if it was found
            if (ellipse != null) {
                if (isValid) {
                    //if it is valid, add it to the list of circles
                    circles.add(new Circle(ellipse));
                    //and draw it in green
                    Imgproc.ellipse(rgbaFrame, ellipse, ImageUtil.GREEN, THICKNESS);
                } else {
                    //if it is not valid, draw it in red
                    Imgproc.ellipse(rgbaFrame, ellipse, ImageUtil.RED, THICKNESS);

                }
            }
        }
        stepTimer.log("finding circles");

        stepTimer.start();
        Point p1 = null;
        Point p2 = null;
        double foundAngleDegrees = 0;
        double matchScore = 0;

        if (circles.size() >= 2) {
            if (circles.size() > MAX_NUM_CIRCLES) {
                Log.i(TAG, "Number of circles to match (" + circles.size() + ") greater than max (" + MAX_NUM_CIRCLES + "), using " + MAX_NUM_CIRCLES + " biggest circles instead.");
                Collections.sort(circles);
                for (int i = MAX_NUM_CIRCLES; i < circles.size(); i++) {
                    Imgproc.ellipse(rgbaFrame, circles.get(i).ellipse, ImageUtil.BLUE, THICKNESS);
                }
                circles = circles.subList(0, MAX_NUM_CIRCLES);
            }

            List<CircleMatch> matches = new ArrayList<>();
            //compare every circle with every other circle exactly once by:
            //looping through all the circles except the last
            for (int i = 0; i < circles.size() - 1; i++) {
                //looping through all the circles after the current one
                for (int j = i + 1; j < circles.size(); j++) {
                    matches.add(new CircleMatch(circles.get(i), circles.get(j), hsv.size()));
                }
            }
//      Log.i(TAG, String.valueOf(matches.toArray().getClass()));
            Collections.sort(matches);
//      for (CircleMatch match : matches) {
//        match.log();
//      }
            CircleMatch bestMatch = matches.get(matches.size() - 1);
            bestMatch.log();
            matchScore = bestMatch.getScore();
            if (matchScore >= MIN_SCORE) {
                p1 = bestMatch.getC1().p;
                p2 = bestMatch.getC2().p;
            }

        }

//    //if there are 2 or more circles, find the two that are closest in size
//    if(circles.size() >= 2){
//      double minDiff = Double.MAX_VALUE;
//      //compare every circle with every other circle exactly once by:
//      //looping through all the circles except the last
//      for(int i=0; i<circles.size()-1; i++){
//        Rect c1 = circles.get(i);
//        double area1 = c1.area();
//        Point point1 = ImageUtil.centerOfRect(c1);
//        //looping through all the circles after the current one
//        for(int j=i+1; j<circles.size(); j++){
//          Rect c2 = circles.get(j);
//          Point point2 = ImageUtil.centerOfRect(c2);
//
//          //find the angle of the line between them by using the atan of the slope
//          double angleDegrees = Math.toDegrees(Math.atan2(point2.y - point1.y, point2.x - point1.x));
//          if (angleDegrees < -90) angleDegrees += 180;
//          if (angleDegrees > 90) angleDegrees -= 180;
//
//          //if the line is not too sloped
//          if (angleDegrees >= -MAX_DEGREES && angleDegrees <= MAX_DEGREES) {
//            //compare how close they are in relative size
//            double area2 = c2.area();
//            double area = Math.min(area1, area2);
////            double area = (area1 + area2)/2;
////            double area = Math.max(area1, area2);
//            double diff = Math.abs(area1 - area2) / area;
//
//            //if they are similar enough, and more similar than any other so far
//            if (diff <= MAX_DIFF && diff < minDiff) {
//              //record the match
//              minDiff = diff;
//              foundAngleDegrees = angleDegrees;
//              p1 = point1;
//              p2 = point2;
//            }
//          }
//        }
//      }
//      Log.i(TAG, "minDiff: " + minDiff);
//    }
        stepTimer.log("best 2 circles");

        //if no circle matches were found, give an unknown result
        if (p1 == null) {
            if (saveImages) {
                stepTimer.start();
                ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "02_circles", startTime);
                stepTimer.log("save 02");
            }
            return new ImageProcessorResult<>(startTime, rgbaFrame, null);
        }

        //rotate the frame about the midpoint of the two circles
        //so that the line between them is horizontal

        stepTimer.start();
        BeaconPositionResult result = new BeaconPositionResult(p1, p2, hsv.size(), foundAngleDegrees, matchScore);
        //find the midpoint

        //draw the midpoint and line
        Imgproc.circle(rgbaFrame, result.getMidpoint(), THICKNESS, ImageUtil.MAGENTA, THICKNESS);
        Imgproc.line(rgbaFrame, p1, p2, ImageUtil.BLACK, THICKNESS);

        //rotate the frame and hsv image
//    Mat rotationMatrix2D = Imgproc.getRotationMatrix2D(result.getMidpoint(), foundAngleDegrees, 1);
//    Imgproc.warpAffine(rgbaFrame, rgbaFrame, rotationMatrix2D, rgbaFrame.size());
//    Imgproc.warpAffine(hsv, hsv, rotationMatrix2D, hsv.size());

        stepTimer.log("rotate image");

        //now that the beacon is found, we need to detect the color of each side

        Log.i(TAG, "BeaconPositionResult: " + result);
        return new ImageProcessorResult<>(startTime, rgbaFrame, result);
    }
}
