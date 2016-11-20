package ftc.evlib.vision.processors;

import org.opencv.core.Mat;

/**
 * takes an image, creates a result, and modifies the image to show the result
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/17/16.
 */
public interface ImageProcessor<ResultType> {
    /**
     * Process a camera frame
     *
     * @param startTime  the time the frame was taken
     * @param rgbaFrame  the frame
     * @param saveImages whether or not ot save the images for logging
     * @return the modified frame to display on the phone screen
     */
    ImageProcessorResult<ResultType> process(long startTime, Mat rgbaFrame, boolean saveImages);
}
