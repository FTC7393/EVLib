package ftc.evlib.hardware.control;

import ftc.electronvolts.util.Angle;
import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.Vector2D;
import ftc.evlib.hardware.sensors.DoubleLineSensor;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;
import ftc.evlib.vision.processors.Location;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/20/16
 * <p>
 * Factory class for TranslationControl
 * contains implementations for line following, beacon tracking, and normal movement
 */

public class TranslationControls {

    public static TranslationControl inputExtractorPolar(final InputExtractor<Double> velocity, final InputExtractor<Double> directionDegrees) {
        return new TranslationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocity.getValue(), Angle.fromDegrees(directionDegrees.getValue()));
            }

        };
    }

    public static TranslationControl inputExtractorXY(final InputExtractor<Double> velocityX, final InputExtractor<Double> velocityY) {
        return new TranslationControl() {

            @Override
            public boolean act() {
                return false;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocityX.getValue(), velocityY.getValue());
            }

        };
    }


    public static TranslationControl inputExtractorVector(final InputExtractor<Vector2D> vector2D) {
        return new TranslationControl() {

            @Override
            public boolean act() {
                return false;
            }

            @Override
            public Vector2D getTranslation() {
                return vector2D.getValue();
            }

        };
    }

//    public static TranslationControlXY polarToXY(final TranslationControlPolar translationControlPolar) {
//        return new TranslationControlXY() {
//            double velocityX, velocityY;
//
//            @Override
//            public boolean act() {
//                boolean worked = translationControlPolar.act();
//                double velocity = translationControlPolar.getVelocity();
//                double directionRadians = translationControlPolar.getDirection().getValueRadians();
//                velocityX = velocity * Math.cos(directionRadians);
//                velocityY = velocity * Math.sin(directionRadians);
//                return worked;
//            }
//
//            @Override
//            public double getVelocityX() {
//                return velocityX;
//            }
//
//            @Override
//            public double getVelocityY() {
//                return velocityY;
//            }
//        };
//    }

    public enum LineFollowDirection {
        LEFT,
        RIGHT
    }

    /**
     * Follow a line with 2 reflective light sensors
     *
     * @param doubleLineSensor    the line sensors
     * @param lineFollowDirection whether to move left or right when following
     * @param velocity            how fast to move when following
     * @return the created TranslationControl
     */
    public static TranslationControl lineFollow(final DoubleLineSensor doubleLineSensor, final LineFollowDirection lineFollowDirection, final double velocity) {
        doubleLineSensor.reset();

        final int DIRECTION_CORRECTION = 45;
        final int LARGE_DIRECTION_CORRECTION = 90;


        return new TranslationControl() {
            private Angle direction;

            /**
             * update the direction of the robot
             * @return true if it worked, false if it lost the line
             */
            @Override
            public boolean act() {
                double targetDirection;
                if (lineFollowDirection == LineFollowDirection.LEFT) {
                    targetDirection = -90;
                } else {
                    targetDirection = 90;
                }

                DoubleLineSensor.LinePosition linePosition = doubleLineSensor.getPosition();

                //calculate the correction based on the line position
                double directionCorrectionDegrees;
                if (linePosition == DoubleLineSensor.LinePosition.MIDDLE) {
                    directionCorrectionDegrees = 0;
                } else if (linePosition == DoubleLineSensor.LinePosition.LEFT) {
                    directionCorrectionDegrees = -DIRECTION_CORRECTION;
                } else if (linePosition == DoubleLineSensor.LinePosition.RIGHT) {
                    directionCorrectionDegrees = DIRECTION_CORRECTION;
                } else if (linePosition == DoubleLineSensor.LinePosition.OFF_LEFT) {
                    directionCorrectionDegrees = -LARGE_DIRECTION_CORRECTION;
                } else if (linePosition == DoubleLineSensor.LinePosition.OFF_RIGHT) {
                    directionCorrectionDegrees = LARGE_DIRECTION_CORRECTION;
                } else {
                    //we are off the line
                    return false;
                }

                //apply the correction to the targetDirection
                direction = Angle.fromDegrees(targetDirection - directionCorrectionDegrees);
                return true;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocity, direction);
            }

        };

    }

    public static TranslationControl cameraTracking(FrameGrabber frameGrabber, ImageProcessor<? extends Location> imageProcessor) {
        return cameraTracking(frameGrabber, imageProcessor, Angle.fromDegrees(90), 0, 0.2);
    }

    /**
     * Line up with the beacon
     *
     * @param frameGrabber    the source of the frames
     * @param cameraViewAngle how wide the camera angle is
     * @param targetX         where the beacon should be in the image
     * @param targetWidth     how wide the beacon should be in the image
     * @return the created TranslationControl
     */
    public static TranslationControl cameraTracking(final FrameGrabber frameGrabber, ImageProcessor<? extends Location> imageProcessor, final Angle cameraViewAngle, final double targetX, final double targetWidth) {

        frameGrabber.setImageProcessor(imageProcessor);
        frameGrabber.grabContinuousFrames();

        return new TranslationControl() {
            private double velocity;
            private Angle direction;

            @Override
            public boolean act() {

                if (frameGrabber.isResultReady()) {
                    ImageProcessorResult imageProcessorResult = frameGrabber.getResult();
                    Location location = (Location) imageProcessorResult.getResult();
                    double imageWidth = imageProcessorResult.getFrame().width();
//                    double x = beaconPositionResult.getMidpoint().x / imageWidth;
//                    double width = beaconPositionResult.getWidth() / imageWidth;
                    double x = location.getX() / imageWidth;
                    double width = location.getWidth() / imageWidth;
                    velocity = targetWidth - width;
                    direction = Angle.fromDegrees(cameraViewAngle.degrees() * (x - 0.5 - targetX));
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocity, direction);
            }

        };
    }


    /**
     * No movement
     *
     * @return the created TranslationControl
     */
    public static TranslationControl zero() {
        return constant(0, Angle.fromRadians(0));
    }

    /**
     * Controls the translation of a mecanum robot with constant velocity and direction
     *
     * @param velocity  how fast to move from 0 to 1
     * @param direction what direction to move
     * @return the created TranslationControl
     */
    public static TranslationControl constant(final double velocity, final Angle direction) {
        return new TranslationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocity, direction);
            }


        };
    }
}
