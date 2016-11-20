package ftc.evlib.vision.processors;

import org.opencv.core.Mat;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 8/27/16.
 *
 * Stores results of an ImageProcessor object,
 * and the time the processing started and ended
 */
public class ImageProcessorResult<ResultType> {
    private final long startTime, endTime;
    private final ResultType result;
    private final Mat frame;

    /**
     * @param startTime the time the frame was taken
     * @param frame     the frame
     * @param result    the result of your type
     */
    ImageProcessorResult(long startTime, Mat frame, ResultType result) {
        this.startTime = startTime;
        this.result = result;
        this.frame = frame;
        this.endTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public boolean isResultNull() {
        return result == null;
    }

    public ResultType getResult() {
        return result;
    }

    public Mat getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        if (isResultNull()) {
            return "null";
        } else {
            return result.toString();
        }
    }
}
