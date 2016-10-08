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

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 */

public class RealFrameGrabber implements FrameGrabber {
    private FrameGrabberMode mode = FrameGrabberMode.STOPPED;

    private CameraOrientation cameraOrientation;
    private boolean ignoreOrientationForDisplay, saveImages;

    //the frame, the blank frame, and temporary images to flip the frame
    private Mat frame, blank, tmp1, tmp2;

    //logging tag
    private final String tag;

    private boolean resultReady = false;

    //objects to run and store the result
    private ImageProcessor imageProcessor = null;
    private ImageProcessorResult result = null;

    //timing variables
    private long totalTime = 0, loopCount = 0, loopTimer = 0;

    private final StepTimer stepTimer;


    public RealFrameGrabber(String tag, int width, int height, CameraOrientation cameraOrientation, boolean ignoreOrientationForDisplay) {
        this.cameraOrientation = cameraOrientation;
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;
        this.tag = tag;
        stepTimer = new StepTimer(tag);

        //create the frame and tmp images
        frame = new Mat(height, width, CvType.CV_8UC4, new Scalar(0, 0, 0));
        blank = new Mat(height, width, CvType.CV_8UC4, new Scalar(0, 0, 0));
        tmp1 = new Mat(height, width, CvType.CV_8UC4);
        tmp2 = new Mat(width, height, CvType.CV_8UC4);
        GlobalFrameGrabber.frameGrabber = this;
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
    public FrameGrabberMode getMode() {
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
        mode = FrameGrabberMode.SINGLE;
        resultReady = false;
    }

    @Override
    public void grabContinuousFrames() {
        if (isImageProcessorNull()) return;
        mode = FrameGrabberMode.CONTINUOUS;
        resultReady = false;
    }

    @Override
    public void throwAwayFrames() {
        mode = FrameGrabberMode.THROWAWAY;
        resultReady = false;
    }

    @Override
    public void stopFrameGrabber() {
        mode = FrameGrabberMode.STOPPED;
        totalTime = 0;
        loopCount = 0;
        loopTimer = 0;
    }

    //getter for the result
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
        if (mode == FrameGrabberMode.SINGLE) { //if a single frame was requested
            processFrame(inputFrame); //process it
            stopFrameGrabber(); //and stop grabbing
            resultReady = true;
        } else if (mode == FrameGrabberMode.CONTINUOUS) { //if in continuous mode
            resultReady = false;
            processFrame(inputFrame); //process and stay in continuous mode
            resultReady = true;
        } else if (mode == FrameGrabberMode.THROWAWAY) { //if throwing away frames
            return blank;
        } else if (mode == FrameGrabberMode.STOPPED) { //if stopped
            //wait for a frame request from the main program
            //in the meantime hang to avoid grabbing extra frames and wasting battery
            resultReady = true;
            while (mode == FrameGrabberMode.STOPPED) {
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

    private void processFrame(Mat inputFrame) {
        if (imageProcessor == null) {
            return;
        }
        //start the loop timer
        if (mode == FrameGrabberMode.SINGLE) {
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
