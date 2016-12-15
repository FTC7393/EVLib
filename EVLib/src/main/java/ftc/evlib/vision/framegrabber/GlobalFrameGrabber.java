package ftc.evlib.vision.framegrabber;

import ftc.evlib.Fake;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 *
 * This class gives everything access to the camera frames
 *
 * @see FrameGrabber
 * @see RealFrameGrabber
 */
public class GlobalFrameGrabber {
    /**
     * This must be set by something for other classes to use it
     */
    public static FrameGrabber frameGrabber = Fake.FRAME_GRABBER;
}
