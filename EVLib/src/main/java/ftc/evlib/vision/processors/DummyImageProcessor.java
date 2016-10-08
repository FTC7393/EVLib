package ftc.evlib.vision.processors;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ftc.evlib.vision.ImageUtil;

/**
 * Created by vandejd1 on 8/28/16.
 * FTC Team EV 7393
 */
public class DummyImageProcessor implements ImageProcessor<String> {
    private static final String TAG = "DummyImageProcessor";

    @Override
    public ImageProcessorResult<String> process(long startTime, Mat rgbaFrame, boolean saveImages) {
        //save the image in the Pictures directory
        String result = "Displayed the image.";
        if (saveImages) {
            if (ImageUtil.saveImage(TAG, rgbaFrame, Imgproc.COLOR_RGBA2BGR, "camera", startTime)) {
                result = "Saved and Displayed the image.";
            } else {
                result = "Failed to save the image. Displayed the image.";
            }
        }

        return new ImageProcessorResult<>(startTime, rgbaFrame, result + " Image Size: " + rgbaFrame.size());
    }
}
