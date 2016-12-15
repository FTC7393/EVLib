package ftc.evlib.vision.framegrabber;

import android.graphics.Bitmap;
import android.support.annotation.IdRes;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Frame;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.Matrix34F;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Vec2F;
import com.vuforia.Vec3F;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ftc.electronvolts.util.BasicResultReceiver;
import ftc.electronvolts.util.ResultReceiver;
import ftc.electronvolts.util.Vector2D;
import ftc.evlib.vision.processors.BeaconName;

import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;

/**
 * This file was adapted by the electronVolts, FTC team 7393
 * source: https://www.dropbox.com/s/lbezww0y8pi1ibg/VuforiaLocalizerImplSubclass.txt?dl=0
 * Credit to team 3491 FIX IT
 * Date Created: 10/6/16
 *
 * This initializes Vuforia and feeds the frames to the FrameGrabber
 *
 * @see GlobalFrameGrabber
 * @see RealFrameGrabber
 */
public class VuforiaFrameFeeder extends VuforiaLocalizerImpl {
    public static Map<BeaconName, VuforiaTrackable> beacons = null;
    private boolean frameGrabberInitialized = false;
    private final int widthRequest, heightRequest;

    private int width, height;

    /**
     * Initialize vuforia
     * This method quits immediately, but the ResultReceiver takes a few seconds before it has the result
     *
     * @param licenseKey                the Vuforia PTC licence key from https://developer.vuforia.com/license-manager
     * @param cameraMonitorViewIdParent the place in the layout where the camera display is
     * @param widthRequest              the width to scale the image to for the FrameGrabber
     * @param heightRequest             the height to scale the image to for the FrameGrabber
     * @return the ResultReceiver that will contain the VuforiaFrameFeeder object
     */
    public static ResultReceiver<VuforiaFrameFeeder> initInNewThread(final String licenseKey, final @IdRes int cameraMonitorViewIdParent, final int widthRequest, final int heightRequest) {
        //create the ResultReceiver that will store the VuforiaFrameFeeder
        final ResultReceiver<VuforiaFrameFeeder> receiver = new BasicResultReceiver<>();

        //start the init in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiver.setValue(VuforiaFrameFeeder.init(licenseKey, cameraMonitorViewIdParent, widthRequest, heightRequest));
            }
        }).start();

        //exit immediately
        return receiver;
    }

    /**
     * Initialize vuforia
     * This method takes a few seconds to complete
     *
     * @param licenseKey                the Vuforia PTC licence key from https://developer.vuforia.com/license-manager
     * @param cameraMonitorViewIdParent the place in the layout where the camera display is
     * @param widthRequest              the width to scale the image to for the FrameGrabber
     * @param heightRequest             the height to scale the image to for the FrameGrabber
     * @return the VuforiaFrameFeeder object
     */
    public static VuforiaFrameFeeder init(String licenseKey, @IdRes int cameraMonitorViewIdParent, int widthRequest, int heightRequest) {
        //display the camera on the screen
        Parameters params = new Parameters(cameraMonitorViewIdParent);

        //do not display the camera on the screen
//        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters();

        params.cameraDirection = CameraDirection.BACK;

        params.vuforiaLicenseKey = licenseKey;
        params.cameraMonitorFeedback = Parameters.CameraMonitorFeedback.AXES;

        //create a new VuforiaFrameFeeder object (this takes a few seconds)
        VuforiaFrameFeeder vuforia = new VuforiaFrameFeeder(params, widthRequest, heightRequest);

        //there are 4 beacons, so set the max image targets to 4
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);
        return vuforia;
    }

    private final FrameGrabber.CameraOrientation cameraOrientation;
    private final boolean ignoreOrientationForDisplay;

    public VuforiaFrameFeeder(Parameters params, int widthRequest, int heightRequest) {
        this(params, widthRequest, heightRequest, FrameGrabber.CameraOrientation.PORTRAIT_UP, false);
    }

    public VuforiaFrameFeeder(Parameters params, FrameGrabber.CameraOrientation cameraOrientation, int widthRequest, int heightRequest) {
        this(params, widthRequest, heightRequest, cameraOrientation, false);
    }

    public VuforiaFrameFeeder(Parameters params, boolean ignoreOrientationForDisplay, int heightRequest, int widthRequest) {
        this(params, widthRequest, heightRequest, FrameGrabber.CameraOrientation.PORTRAIT_UP, ignoreOrientationForDisplay);
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

            //get the frames from vuforia
            long num = closeableFrame.getNumImages();
            for (int i = 0; i < num; i++) {
                if (closeableFrame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    Image rgb = closeableFrame.getImage(i);
                    width = rgb.getWidth();
                    height = rgb.getHeight();

                    //if this is the first frame
                    if (!frameGrabberInitialized) {
                        //initialize the FrameGrabber
                        new RealFrameGrabber(TAG, widthRequest, heightRequest, cameraOrientation, ignoreOrientationForDisplay, true);
                        frameGrabberInitialized = true;
                    }

                    //convert the vuforia image into a Bitmap
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    bitmap.copyPixelsFromBuffer(rgb.getPixels());

                    //scale the Bitmap to the requested size
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, heightRequest, widthRequest, false);

                    //pass the resized Bitmap to the FrameGrabber
                    frameGrabber.receiveFrame(resized);
                    //TODO display resulting image on the screen
                }
            }

            closeableFrame.close();
        }
    }

    public static Vector2D vef2FToVector2D(Vec2F vec2F) {
        return new Vector2D(vec2F.getData()[1], vec2F.getData()[0]);
    }

    /**
     * The width of the beacon target images
     */
    private static final int BEACON_TARGET_WIDTH = 127 * 2;

    /**
     * The height of the beacon target images
     */
    private static final int BEACON_TARGET_HEIGHT = 92 * 2;

    /**
     * @param rawPose the pose of the beacon image VuforiaTrackable object
     * @return the list of 4 Vector2D objects that represent the points of the corners of the beacon image
     */
    public List<Vector2D> getImageCorners(Matrix34F rawPose) {
        //top left, top right, bottom left, bottom right
        List<Vec2F> vec2fList = ImmutableList.of(
                Tool.projectPoint(getCameraCalibration(), rawPose, new Vec3F(-BEACON_TARGET_WIDTH / 2, BEACON_TARGET_HEIGHT / 2, 0)),  //top left
                Tool.projectPoint(getCameraCalibration(), rawPose, new Vec3F(BEACON_TARGET_WIDTH / 2, BEACON_TARGET_HEIGHT / 2, 0)),   //top right
                Tool.projectPoint(getCameraCalibration(), rawPose, new Vec3F(-BEACON_TARGET_WIDTH / 2, -BEACON_TARGET_HEIGHT / 2, 0)), //bottom left
                Tool.projectPoint(getCameraCalibration(), rawPose, new Vec3F(BEACON_TARGET_WIDTH / 2, -BEACON_TARGET_HEIGHT / 2, 0))   //bottom right
        );

//        Log.i(TAG, "unscaled frame size: " + new Vector2D(width, height));
//
//        Log.i(TAG, "unscaled tl: " + vef2FToVector2D(vec2fList.get(0)));
//        Log.i(TAG, "unscaled tr: " + vef2FToVector2D(vec2fList.get(1)));
//        Log.i(TAG, "unscaled bl: " + vef2FToVector2D(vec2fList.get(2)));
//        Log.i(TAG, "unscaled br: " + vef2FToVector2D(vec2fList.get(3)));

        //get average width from the top width and bottom width
//        double w = ((vec2fList.get(1).getData()[1] - vec2fList.get(0).getData()[1]) + (vec2fList.get(3).getData()[1] - vec2fList.get(2).getData()[1])) / 2;
        //same for height
//        double h = ((vec2fList.get(2).getData()[0] - vec2fList.get(0).getData()[0]) + (vec2fList.get(3).getData()[0] - vec2fList.get(1).getData()[0])) / 2;

//        Log.i(TAG, "beacon picture size: " + new Vector2D(w, h));

        //convert the Vec2F list to a Vector2D list and scale it to match the requested frame size
        List<Vector2D> corners = new ArrayList<>();
        for (Vec2F vec2f : vec2fList) {
            corners.add(new Vector2D(
                    (height - vec2f.getData()[1]) * widthRequest / height,
                    vec2f.getData()[0] * heightRequest / width
            ));
        }

        return corners;
    }

    /**
     * @param parameters                  the vuforia parameters
     * @param widthRequest                the width to resize the input frame to
     * @param heightRequest               the height to resize the input frame to
     * @param cameraOrientation           the orientation of the camera on the robot
     * @param ignoreOrientationForDisplay whether or not to rotate the output frame to display on the phone
     */
    public VuforiaFrameFeeder(Parameters parameters, int widthRequest, int heightRequest, FrameGrabber.CameraOrientation cameraOrientation, boolean ignoreOrientationForDisplay) {
        super(parameters);
        this.widthRequest = widthRequest;
        this.heightRequest = heightRequest;
        this.cameraOrientation = cameraOrientation;
        this.ignoreOrientationForDisplay = ignoreOrientationForDisplay;
        stopAR();
        clearGlSurface();

        this.vuforiaCallback = new VuforiaCallbackSubclass();
        startAR();

        // Optional: set the pixel format(s) that you want to have in the callback
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

        //load the beacon trackables
        VuforiaTrackables beaconsVuforiaTrackables = loadTrackablesFromAsset("FTC_2016-17");

        //set the names of the beacon trackables
        beaconsVuforiaTrackables.get(0).setName("Wheels");
        beaconsVuforiaTrackables.get(1).setName("Tools");
        beaconsVuforiaTrackables.get(2).setName("Legos");
        beaconsVuforiaTrackables.get(3).setName("Gears");

        //create a Map to connect BeaconName to beacon VuforiaTrackable
        beacons = new HashMap<>();

        //add the beacon trackables to the Map
        beacons.put(BeaconName.WHEELS, beaconsVuforiaTrackables.get(0));
        beacons.put(BeaconName.TOOLS, beaconsVuforiaTrackables.get(1));
        beacons.put(BeaconName.LEGOS, beaconsVuforiaTrackables.get(2));
        beacons.put(BeaconName.GEARS, beaconsVuforiaTrackables.get(3));

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