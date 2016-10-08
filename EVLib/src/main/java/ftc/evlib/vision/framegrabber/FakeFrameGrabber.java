package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 */

public class FakeFrameGrabber implements FrameGrabber {
    @Override
    public CameraOrientation getCameraOrientation() {
        return CameraOrientation.PORTRAIT_UP;
    }

    @Override
    public boolean isIgnoreOrientationForDisplay() {
        return false;
    }

    @Override
    public boolean isSaveImages() {
        return false;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return null;
    }

    @Override
    public FrameGrabberMode getMode() {
        return FrameGrabberMode.STOPPED;
    }

    @Override
    public void setCameraOrientation(CameraOrientation cameraOrientation) {

    }

    @Override
    public void setIgnoreOrientationForDisplay(boolean ignoreOrientationForDisplay) {

    }

    @Override
    public void setSaveImages(boolean saveImages) {

    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {

    }

    @Override
    public void grabSingleFrame() {

    }

    @Override
    public void grabContinuousFrames() {

    }

    @Override
    public void throwAwayFrames() {

    }

    @Override
    public void stopFrameGrabber() {

    }

    @Override
    public boolean isResultReady() {
        return false;
    }

    @Override
    public ImageProcessorResult getResult() {
        return null;
    }

    @Override
    public Mat receiveFrame(Bitmap bitmap) {
        Mat tmp = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, tmp);
        return tmp;
    }

    @Override
    public Mat receiveFrame(Mat inputFrame) {
        return inputFrame;
    }
}
