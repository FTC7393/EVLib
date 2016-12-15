package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ftc.evlib.util.StepTimer;
import ftc.evlib.vision.ImageUtil;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;

import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 *
 * This implements FrameGrabber and uses opencv methods
 *
 * @see GlobalFrameGrabber
 */
public class RealFrameGrabber implements FrameGrabber {
    private Mode mode = Mode.STOPPED;

    private CameraOrientation cameraOrientation;
    private boolean ignoreOrientationForDisplay, saveImages;
    private final boolean throwAway;

    /**
     * The camera frame
     */
    private Mat frame;

    /**
     * A completely black image
     */
    private final Mat blank;

    /**
     * temporary images used for flipping
     */
    private final Mat tmp1, tmp2;

    /**
     * logging tag
     */
    private final String tag;

    private boolean resultReady = false;

    /**
     * The ImageProcessor to run
     */
    private ImageProcessor imageProcessor = null;

    /**
     * The object to store the result from the ImageProcessor
     */
    private ImageProcessorResult result = null;

    /**
     * timing variables
     */
    private long totalTime = 0, loopCount = 0, loopTimer = 0;

    /**
     * StepTimer to keep track of the different steps in each loop
     */
    private final StepTimer stepTimer;

    /**
     * Create a FrameGrabber
     *
     * @param tag                         the logging tag
     * @param width                       the width of the camera frames it will get
     * @param height                      the height of the camera frames it will get
     * @param cameraOrientation           the orientation of the camera on the robot
     * @param ignoreOrientationForDisplay whether or not to flip the output frame when displaying
     * @param throwAway                   whether or not to throw away frames instead of stopping
     */
    public RealFrameGrabber(String tag, int width, int height, CameraOrientation cameraOrientation, boolean ignoreOrientationForDisplay, boolean throwAway) {
        this.cameraOrientation = cameraOrientation;
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;
        this.tag = tag;
        this.throwAway = throwAway;

        stepTimer = new StepTimer(tag);

        //create the frame and tmp images
        frame = new Mat(height, width, CvType.CV_8UC4, new Scalar(0, 0, 0));
        blank = new Mat(height, width, CvType.CV_8UC4, new Scalar(0, 0, 0));
        tmp1 = new Mat(height, width, CvType.CV_8UC4);
        tmp2 = new Mat(width, height, CvType.CV_8UC4);


        if (frameGrabber != null) {
            imageProcessor = frameGrabber.getImageProcessor();
        }
        frameGrabber = this;
    }

    @Override
    public CameraOrientation getCameraOrientation() {
        return cameraOrientation;
    }

    @Override
    public boolean isIgnoreOrientationForDisplay() {
        return ignoreOrientationForDisplay;
    }

    @Override
    public boolean isSaveImages() {
        return saveImages;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setCameraOrientation(CameraOrientation cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
    }

    @Override
    public void setIgnoreOrientationForDisplay(boolean ignoreOrientationForDisplay) {
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;
    }

    @Override
    public void setSaveImages(boolean saveImages) {
        this.saveImages = saveImages;
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    private boolean isImageProcessorNull() {
        if (imageProcessor == null) {
            Log.e(tag, "imageProcessor is null! Call setImageProcessor() to set it.");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void grabSingleFrame() {
        if (isImageProcessorNull()) return;
        mode = Mode.SINGLE;
        resultReady = false;
    }

    @Override
    public void grabContinuousFrames() {
        if (isImageProcessorNull()) return;
        mode = Mode.CONTINUOUS;
        resultReady = false;
    }

    @Override
    public void throwAwayFrames() {
        mode = Mode.THROWAWAY;
        resultReady = false;
    }

    @Override
    public void stopFrameGrabber() {
        mode = Mode.STOPPED;
        totalTime = 0;
        loopCount = 0;
        loopTimer = 0;
    }

    @Override
    public boolean isResultReady() {
        return resultReady;
    }

    @Override
    public ImageProcessorResult getResult() {
        return result;
    }

    @Override
    public Mat receiveFrame(Bitmap bitmap) {
        Utils.bitmapToMat(bitmap, tmp2);
        return receiveFrame(tmp2);
    }

    @Override
    public Mat receiveFrame(Mat inputFrame) {
        //throw frames away instead of stopping if that behavior has been requested
        if (throwAway && mode == Mode.STOPPED) {
            mode = Mode.THROWAWAY;
        }
        if (mode == Mode.SINGLE) { //if a single frame was requested
            processFrame(inputFrame); //process it
            stopFrameGrabber(); //and stop grabbing
            resultReady = true;
        } else if (mode == Mode.CONTINUOUS) { //if in continuous mode
            resultReady = false;
            processFrame(inputFrame); //process and stay in continuous mode
            resultReady = true;
        } else if (mode == Mode.THROWAWAY) { //if throwing away frames
            return blank;
        } else if (mode == Mode.STOPPED) { //if stopped
            //wait for a frame request from the main program
            //in the meantime hang to avoid grabbing extra frames and wasting battery
            resultReady = true;
            while (mode == Mode.STOPPED) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            resultReady = false;
        } else {
            stopFrameGrabber(); //paranoia
        }
        return frame; //this is displayed on the screen
    }

    /**
     * Process a single frame
     *
     * @param inputFrame the frame
     */
    private void processFrame(Mat inputFrame) {
        if (imageProcessor == null) {
            return;
        }
        //start the loop timer
        if (mode == Mode.SINGLE) {
            loopTimer = System.nanoTime();
        }
        long frameTime = System.currentTimeMillis();

        //get the rgb alpha image
        stepTimer.start();
        Log.i(tag, "frame orientation: " + cameraOrientation);

        ImageUtil.rotate(inputFrame, frame, cameraOrientation.angle + 90);

        stepTimer.log("rgba conversion");

        //process the image using the provided imageProcessor
        stepTimer.start();
        result = imageProcessor.process(frameTime, frame, saveImages); //process
        frame = result.getFrame(); //get the output frame
        stepTimer.log("imageProcessor");
        if (result == null) {
            Log.i(tag, "Result is null");
        } else {
            Log.i(tag, "Result: " + result);
        }

        stepTimer.start();
        Log.i(tag, "frame size: " + frame.size());

        if (ignoreOrientationForDisplay) {
            ImageUtil.rotate(frame, tmp1, 0);
        } else {
            ImageUtil.rotate(frame, tmp1, -cameraOrientation.angle);
        }
        Core.transpose(tmp1, frame);
        Imgproc.resize(frame, tmp2, tmp2.size(), 0, 0, 0);
        Core.transpose(tmp2, frame);

        stepTimer.log("flipping the image");

        //Loop timer
        long now = System.nanoTime();
        long loopTime = now - loopTimer;
        if (loopTimer > 0) {
            loopCount++;
            totalTime += loopTime;
            Log.i(tag, "LOOP #" + loopCount);
            Log.i(tag, "LOOP TIME: " + loopTime / 1000000.0 + " ms");
            Log.i(tag, "AVERAGE LOOP TIME: " + totalTime / 1000000.0 / loopCount + " ms");
            Log.i(tag, "ESTIMATED AVERAGE FPS: " + 1000.0 * 1000000.0 * loopCount / totalTime);
        }
        loopTimer = now;
    }
}
