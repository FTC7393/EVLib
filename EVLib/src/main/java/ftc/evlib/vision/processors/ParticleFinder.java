package ftc.evlib.vision.processors;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
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
 * Date Created: 9/26/16
 *
 * An ImageProcessor that finds particles (field object)
 */
public class ParticleFinder implements ImageProcessor<List<Particle>> {
    private static final String TAG = "ParticleFinder";
    private static final int THICKNESS = 2;
    private static final Scalar[] colorChannels = {ImageUtil.RED, ImageUtil.GREEN, ImageUtil.BLUE};
//    private static String[] colorNames = {"red", "green", "blue"};

    private static final int BLUR_AMOUNT = 3; //used to blur the binary image
    private static final int BLUR_MAX_V = 80; //post-blur hsv value threshold
    private static final double FRAME_AREA_SCALE = 1E-4; //scaling factor for the frame's area
    private static final double MIN_AREA = 50; //min area for circles to be considered
    private static final double MAX_AREA = 5000; //max area for circles
    private static final double MIN_RATIO = .7; //min ratio of ellipse sides to be considered a circle
    private static final Particle.ParticleColor[] particleColors = {Particle.ParticleColor.RED, Particle.ParticleColor.BLUE};

    private final StepTimer stepTimer = new StepTimer(TAG);
    private static final double MIN_S = 50;
    private static final double MIN_V = 1;

    @Override
    public ImageProcessorResult<List<Particle>> process(long startTime, Mat rgbaFrame, boolean saveImages) {
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


        //calculate the hsv thresholds
        //the h value goes from 0 to 179
        //the s value goes from 0 to 255
        //the v value goes from 0 to 255

        //the values are stored as a list of min HSV and a list of max HSV
        List<Scalar> thresholdMin = new ArrayList<>();
        List<Scalar> thresholdMax = new ArrayList<>();

//        thresholdMin.add(new Scalar((300) / 2, MIN_S, MIN_V));
//        thresholdMax.add(new Scalar((60) / 2, 255, 255));
//
//        thresholdMin.add(new Scalar((60) / 2, MIN_S, MIN_V));
//        thresholdMax.add(new Scalar((180) / 2, 255, 255));
//
//        thresholdMin.add(new Scalar((180) / 2, MIN_S, MIN_V));
//        thresholdMax.add(new Scalar((300) / 2, 255, 255));

        //larger red range
        thresholdMin.add(new Scalar((304) / 2, MIN_S, MIN_V));
        thresholdMax.add(new Scalar((16) / 2, 255, 255));

        //1-value green range
//        thresholdMin.add(new Scalar((60) / 2, 255, 255));
//        thresholdMax.add(new Scalar((60) / 2, 255, 255));

        // large blue range
        thresholdMin.add(new Scalar((150) / 2, MIN_S, MIN_V));
        thresholdMax.add(new Scalar((300) / 2, 255, 255));


        List<Particle> particles = new ArrayList<>();

        Mat maskedImage;
        //loop through red, blue
        for (int c = 0; c < 2; c++) {
            int channel = 0;
            if (c == 1) { //skip green
                channel = 2;
            }
            stepTimer.start();
            //apply HSV thresholds to get binary image
            maskedImage = new Mat();
            ImageUtil.hsvInRange(hsv, thresholdMin.get(c), thresholdMax.get(c), maskedImage);

            //blur the image and re-threshold to "de-bounce" the noisy sections
            Imgproc.blur(maskedImage, maskedImage, new Size(BLUR_AMOUNT, BLUR_AMOUNT));
            Imgproc.threshold(maskedImage, maskedImage, BLUR_MAX_V, 255, Imgproc.THRESH_BINARY_INV);
            stepTimer.log("threshold & blur for channel " + channel);

            stepTimer.start();
            //find contours (edges between red/blue and not red/blue)
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(maskedImage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.drawContours(rgbaFrame, contours, -1, ImageUtil.YELLOW, THICKNESS);
            stepTimer.log("contours for channel " + channel);

            if (saveImages) {
                stepTimer.start();
                //save the threshold image for logging
                ImageUtil.saveImage(TAG, maskedImage, Imgproc.COLOR_GRAY2BGR, "01_threshold" + channel, startTime);
                stepTimer.log("save 01:" + channel + " " + particleColors[c]);
            }

            stepTimer.start();
            double frameArea = FRAME_AREA_SCALE * rgbaFrame.size().area();
            //loop through the contours to find the circular ones
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
//                    Log.i(TAG, "Ellipse Area: " + area);
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
                        particles.add(new Particle(ellipse, particleColors[c]));
                        //and draw it in green
                        Imgproc.ellipse(rgbaFrame, ellipse, colorChannels[channel], THICKNESS);
                    } else {
                        //if it is not valid, draw it in red
                        Imgproc.ellipse(rgbaFrame, ellipse, ImageUtil.BROWN, THICKNESS);

                    }
                }
            }
//            stepTimer.log("finding circles");
//            if (saveImages) {
//                stepTimer.start();
//                //save the raw camera image for logging
//                ImageUtil.saveImage(TAG, maskedImage, Imgproc.COLOR_RGBA2BGR, "02_"+colorNames[channel], startTime);
//                stepTimer.log("save 02");
//            }

        }
        if (saveImages) {
            stepTimer.start();
            //save the threshold image for logging
            ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_GRAY2BGR, "03_final", startTime);
            stepTimer.log("save 03");
        }

        Collections.sort(particles);

        Log.i(TAG, "num particles: " + particles.size());
        Log.i(TAG, "particles: " + particles);

        return new ImageProcessorResult<>(startTime, rgbaFrame, particles);
//        if (particles.size() > 0) {
//            Collections.sort(particles);
//            Log.i(TAG, "particle[0]: " + particles.get(0));
//            return new ImageProcessorResult<>(startTime, rgbaFrame, particles.get(0));
//        } else {
//            return new ImageProcessorResult<>(startTime, rgbaFrame, null);
//        }
    }
}
