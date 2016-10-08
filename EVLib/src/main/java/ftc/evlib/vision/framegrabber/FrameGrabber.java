package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 */
public interface FrameGrabber {
    CameraOrientation getCameraOrientation();

    boolean isIgnoreOrientationForDisplay();

    boolean isSaveImages();

    ImageProcessor getImageProcessor();

    FrameGrabberMode getMode();

    void setCameraOrientation(CameraOrientation cameraOrientation);

    void setIgnoreOrientationForDisplay(boolean ignoreOrientationForDisplay);

    void setSaveImages(boolean saveImages);

    void setImageProcessor(ImageProcessor imageProcessor);

    void grabSingleFrame();

    void grabContinuousFrames();

    void throwAwayFrames();

    void stopFrameGrabber();

    //getter for the result
    boolean isResultReady();

    ImageProcessorResult getResult();

    Mat receiveFrame(Bitmap bitmap);

    Mat receiveFrame(Mat inputFrame);

    enum FrameGrabberMode {
        SINGLE,
        CONTINUOUS,
        THROWAWAY,
        STOPPED
    }

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
}
