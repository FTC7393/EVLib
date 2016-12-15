package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 *
 * interface for retrieving camera frames
 *
 * @see RealFrameGrabber
 * @see GlobalFrameGrabber
 */
public interface FrameGrabber {
    /**
     * Whether to grab one, all, or no frames, or throw away the frames
     */
    enum Mode {
        SINGLE, //grab one frame, then stop
        CONTINUOUS, //grab all frames that are available
        THROWAWAY, //throw away all frames that are given
        STOPPED //wait for a frame grab request
    }

    /**
     * The rotation to be applied to the camera frame
     */
    enum CameraOrientation {
        PORTRAIT_UP(0),
        PORTRAIT_DOWN(180),
        LANDSCAPE_LEFT(-90),
        LANDSCAPE_RIGHT(90);

        public final int angle;

        CameraOrientation(int angle) {
            this.angle = angle;
        }
    }

    /**
     * @return the orientation of the camera on the robot
     */
    CameraOrientation getCameraOrientation();

    /**
     * @return whether or not the frame is rotated to display on the phone
     */
    boolean isIgnoreOrientationForDisplay();

    /**
     * @return whether or not to save the output of the image processor to a file
     */
    boolean isSaveImages();

    /**
     * @return the current image processor
     */
    ImageProcessor getImageProcessor();

    /**
     * @return the mode the FrameGrabber is in
     */
    Mode getMode();

    /**
     * @param cameraOrientation the orientation of the camera on the robot
     */
    void setCameraOrientation(CameraOrientation cameraOrientation);

    /**
     * @param ignoreOrientationForDisplay whether or not the frame is rotated to display on the phone
     */
    void setIgnoreOrientationForDisplay(boolean ignoreOrientationForDisplay);

    /**
     * @param saveImages whether or not to save the output of the image processor to a file
     */
    void setSaveImages(boolean saveImages);

    /**
     * @param imageProcessor the ImageProcessor object that takes the frame and returns a result
     */
    void setImageProcessor(ImageProcessor imageProcessor);

    /**
     * Grab one frame and stop
     */
    void grabSingleFrame();

    /**
     * Grab every frame and pass it to the current ImageProcessor
     */
    void grabContinuousFrames();

    /**
     * Keep frames moving through the system but don't use an imageProcessor
     */
    void throwAwayFrames();

    /**
     * Stop grabbing frames
     */
    void stopFrameGrabber();

    /**
     * @return whether or not the result from the current ImageProcessor is ready
     */
    boolean isResultReady();

    /**
     * @return the result from the current ImageProcessor
     */
    ImageProcessorResult getResult();

    /**
     * This should be called when a new camera frame is acquired
     *
     * @param bitmap the camera frame in bitmap form
     * @return the modified frame
     */
    Mat receiveFrame(Bitmap bitmap);

    /**
     * This should be called when a new camera frame is acquired
     *
     * @param inputFrame the camera frame in opencv Mat form
     * @return the modified frame
     */
    Mat receiveFrame(Mat inputFrame);
}
