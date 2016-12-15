package ftc.evlib.hardware.control;

import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.Vector2D;
import ftc.electronvolts.util.units.Angle;
import ftc.evlib.hardware.sensors.DoubleLineSensor;
import ftc.evlib.hardware.sensors.LineSensorArray;
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
 *
 * @see TranslationControl
 */

public class TranslationControls {
    /**
     * No movement
     */
    public static final TranslationControl ZERO = constant(0, Angle.zero());

    /**
     * Controls the translation of a mecanum robot with constant velocity and direction
     *
     * @param velocity  how fast to move from -1 to 1
     * @param direction what direction to move
     * @return the created TranslationControl
     */
    public static TranslationControl constant(double velocity, Angle direction) {
        final Vector2D vector2D = new Vector2D(velocity, direction);
        return new TranslationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public Vector2D getTranslation() {
                return vector2D;
            }


        };
    }

    public static TranslationControl inputExtractorPolarDegrees(final InputExtractor<Double> velocity, final InputExtractor<Double> directionDegrees) {
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

    public static TranslationControl inputExtractorPolar(final InputExtractor<Double> velocity, final InputExtractor<Angle> direction) {
        return new TranslationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(velocity.getValue(), direction.getValue());
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


    public static TranslationControl inputExtractorXY(final InputExtractor<Vector2D> vector2D) {
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

    public static TranslationControl lineFollow(final LineSensorArray lineSensorArray, final LineFollowDirection direction, final double center, final double velocity) {
        return new TranslationControl() {
            @Override
            public boolean act() {
                lineSensorArray.update();
                return lineSensorArray.getNumSensorsActive() != 0;
            }

            @Override
            public Vector2D getTranslation() {
                return new Vector2D(center - lineSensorArray.getCentroid(), velocity * direction.sign);
            }
        };

    }

//    public static TranslationControl lineUp(final LineSensorArray lineSensorArray, final double lineTarget, final ControlLoop lineControl, final DistanceSensor distanceSensor, final Distance distanceTarget, final ControlLoop distanceControl) {
//        final int stopCycles = 5;
//        lineControl.initialize();
//        return new TranslationControl() {
//            private boolean distPIDInit = false;
//            private int cyclesLeft = -1;
//            private int lostLineCycles = 0;
//
//            private Vector2D translation = new Vector2D(0, 0);
//
//            @Override
//            public boolean act() {
//                lineSensorArray.update();
//                if (lineSensorArray.getNumSensorsActive() == 0) {
//                    lostLineCycles++;
//                }
//
//                if (lostLineCycles >= 5) {
//                    translation = new Vector2D(0, 0);
//                    return false;
//                }
//
//                double x = lineControl.computeCorrection(lineTarget, lineSensorArray.getCentroid());
//                double xError = Math.abs(lineTarget - lineSensorArray.getCentroid());
//                double y = 0;
//                if (xError <= 0.1) {
//                    if (cyclesLeft == -1) {
//                        cyclesLeft = stopCycles;
//                    }
//                    if (cyclesLeft > 0) {
//                        cyclesLeft--;
//                        return true;
//                    }
//                    if (!distPIDInit) {
//                        distPIDInit = true;
//                        distanceControl.initialize();
//                    }
//                    y = distanceControl.computeCorrection(distanceTarget.meters(), distanceSensor.getDistance().meters());
//                } else {
//                    cyclesLeft = -1;
//                    distPIDInit = false;
//                }
//                telemetry.addData("LineUp x", x);
//                telemetry.addData("LineUp y", y);
//                translation = new Vector2D(x, y);
////                translation = new Vector2D(0, 0);
//
//                return true;
//            }
//
//            @Override
//            public Vector2D getTranslation() {
//                return translation;
//            }
//        };
//    }


    public enum LineFollowDirection {
        LEFT(-1),
        RIGHT(1);

        public final int sign;
        public final Angle angle;

        LineFollowDirection(int sign) {
            this.sign = sign;
            this.angle = Angle.fromDegrees(90 * sign);
        }

        public LineFollowDirection opposite() {
            if (this == LEFT) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }
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
                double targetDirection = 90 * lineFollowDirection.sign;
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
}
