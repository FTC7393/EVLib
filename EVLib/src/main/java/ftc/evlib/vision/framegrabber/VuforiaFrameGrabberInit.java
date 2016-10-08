package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Frame;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;

/**
 * This file was made by the electronVolts, FTC team 7393
 * source: https://www.dropbox.com/s/lbezww0y8pi1ibg/VuforiaLocalizerImplSubclass.txt?dl=0
 * Credit to team 3491 FIX IT
 * Date Created: 10/6/16
 */

public class VuforiaFrameGrabberInit extends VuforiaLocalizerImpl {
    private final FrameGrabber.CameraOrientation cameraOrientation;
    private final boolean ignoreOrientationForDisplay;

    public VuforiaFrameGrabberInit(Parameters params) {
        this(params, FrameGrabber.CameraOrientation.PORTRAIT_UP, false);
    }

    public VuforiaFrameGrabberInit(Parameters params, FrameGrabber.CameraOrientation cameraOrientation) {
        this(params, cameraOrientation, false);
    }

    public VuforiaFrameGrabberInit(Parameters params, boolean ignoreOrientationForDisplay) {
        this(params, FrameGrabber.CameraOrientation.PORTRAIT_UP, ignoreOrientationForDisplay);
    }

    class CloseableFrame extends Frame {
        public CloseableFrame(Frame other) { // clone the frame so we can be useful beyond callback
            super(other);
        }

        public void close() {
            super.delete();
        }
    }


    public class VuforiaCallbackSubclass extends VuforiaCallback {

        @Override
        public synchronized void Vuforia_onUpdate(State state) {
            super.Vuforia_onUpdate(state);
            // We wish to accomplish two things: (a) get a clone of the Frame so we can use
            // it beyond the callback, and (b) get a variant that will allow us to proactively
            // reduce memory pressure rather than relying on the garbage collector (which here
            // has been observed to interact poorly with the image data which is allocated on a
            // non-garbage-collected heap). Note that both of this concerns are independent of
            // how the Frame is obtained in the first place.
            CloseableFrame closeableFrame = new CloseableFrame(state.getFrame());
            RobotLog.vv(TAG, "received Vuforia frame#=%d", closeableFrame.getIndex());


            long num = closeableFrame.getNumImages();
            for (int i = 0; i < num; i++) {
//                if(frame.getImage(i).getFormat() == PIXEL_FORMAT.RGBA8888){
                if (closeableFrame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    Image rgb = closeableFrame.getImage(i);
                    int width = rgb.getWidth();
                    int height = rgb.getHeight();

                    if (frameGrabber == null) {
                        new RealFrameGrabber(TAG, width, height, cameraOrientation, ignoreOrientationForDisplay);
                    }

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    bitmap.copyPixelsFromBuffer(rgb.getPixels());
                    frameGrabber.receiveFrame(bitmap);
                }
            }

            closeableFrame.close();
        }
    }

    public VuforiaFrameGrabberInit(Parameters parameters, FrameGrabber.CameraOrientation cameraOrientation, boolean ignoreOrientationForDisplay) {
        super(parameters);
        this.cameraOrientation = cameraOrientation;
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;
        stopAR();
        clearGlSurface();

        this.vuforiaCallback = new VuforiaCallbackSubclass();
        startAR();

        // Optional: set the pixel format(s) that you want to have in the callback
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
    }

    public void clearGlSurface() {
        if (this.glSurfaceParent != null) {
            appUtil.synchronousRunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    glSurfaceParent.removeAllViews();
                    glSurfaceParent.getOverlay().clear();
                    glSurface = null;
                }
            });
        }
    }
}