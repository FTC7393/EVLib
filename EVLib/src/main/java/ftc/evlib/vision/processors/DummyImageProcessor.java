package ftc.evlib.vision.processors;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ftc.evlib.vision.ImageUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/28/16
 *
 * An image processor that saves the image if requested and returns a status String
 */
public class DummyImageProcessor implements ImageProcessor<String> {
    private static final String TAG = "DummyImageProcessor";

    @Override
    public ImageProcessorResult<String> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        String result = "Displayed the image.";
        if (saveImages) {
            //save the camera frame in the Pictures directory
            if (ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "camera", startTime)) {
                result = "Saved and Displayed the image.";
            } else {
                result = "Failed to save the image. Displayed the image.";
            }
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, result + " Image Size: " + rgbaFrame.size());
    }
}
