package ftc.evlib.vision.framegrabber;

import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;

/**
 * used to manage the frames that openCV takes
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/24/16.
 */
public class OpenCVFrameGrabberInit implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OpenCVFrameGrabber";
    private final FrameGrabber.CameraOrientation cameraOrientation;
    private final boolean ignoreOrientationForDisplay;

    public OpenCVFrameGrabberInit(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest) {
        this(cameraBridgeViewBase, frameWidthRequest, frameHeightRequest, FrameGrabber.CameraOrientation.PORTRAIT_UP, false);
    }

    public OpenCVFrameGrabberInit(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest, FrameGrabber.CameraOrientation cameraOrientation) {
        this(cameraBridgeViewBase, frameWidthRequest, frameHeightRequest, cameraOrientation, false);
    }

    public OpenCVFrameGrabberInit(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest, boolean ignoreOrientationForDisplay) {
        this(cameraBridgeViewBase, frameWidthRequest, frameHeightRequest, FrameGrabber.CameraOrientation.PORTRAIT_UP, ignoreOrientationForDisplay);
    }

    public OpenCVFrameGrabberInit(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest, FrameGrabber.CameraOrientation cameraOrientation, boolean ignoreOrientationForDisplay) {
        this.cameraOrientation = cameraOrientation;
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;

        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        cameraBridgeViewBase.setMinimumWidth(frameWidthRequest);
        cameraBridgeViewBase.setMinimumHeight(frameHeightRequest);
        cameraBridgeViewBase.setMaxFrameSize(frameWidthRequest, frameHeightRequest);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        new RealFrameGrabber(TAG, width, height, cameraOrientation, ignoreOrientationForDisplay);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return frameGrabber.receiveFrame(inputFrame.rgba());
    }
}
