package ftc.evlib.statemachine;

import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

import java.util.List;

import ftc.electronvolts.statemachine.AbstractState;
import ftc.electronvolts.statemachine.BasicAbstractState;
import ftc.electronvolts.statemachine.EndCondition;
import ftc.electronvolts.statemachine.EndConditions;
import ftc.electronvolts.statemachine.State;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.States;
import ftc.electronvolts.statemachine.Transition;
import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.PIDController;
import ftc.electronvolts.util.ResultReceiver;
import ftc.electronvolts.util.TeamColor;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.Distance;
import ftc.electronvolts.util.units.Time;
import ftc.electronvolts.util.units.Velocity;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.control.RotationControl;
import ftc.evlib.hardware.control.RotationControls;
import ftc.evlib.hardware.control.TranslationControl;
import ftc.evlib.hardware.control.TranslationControls;
import ftc.evlib.hardware.motors.MecanumMotors;
import ftc.evlib.hardware.motors.Motor;
import ftc.evlib.hardware.motors.NMotors;
import ftc.evlib.hardware.motors.TwoMotors;
import ftc.evlib.hardware.sensors.DistanceSensor;
import ftc.evlib.hardware.sensors.DoubleLineSensor;
import ftc.evlib.hardware.sensors.LineSensorArray;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.framegrabber.VuforiaFrameGrabberInit;
import ftc.evlib.vision.processors.BeaconColorResult;
import ftc.evlib.vision.processors.BeaconName;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;
import ftc.evlib.vision.processors.Location;
import ftc.evlib.vision.processors.RGBBeaconProcessor;
import ftc.evlib.vision.processors.VuforiaBeaconColorProcessor;

import static ftc.evlib.driverstation.Telem.telemetry;
import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;
import static ftc.evlib.vision.framegrabber.VuforiaFrameGrabberInit.beacons;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/10/16
 */
public class EVStates extends States {

    /**
     * Displays the left and right color of a BeaconColorResult
     *
     * @param stateName the name of the state
     * @param receiver  the ResultReceiver to get the color from
     * @return the created State
     */
    public static State displayBeaconColor(StateName stateName, final ResultReceiver<BeaconColorResult> receiver) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {

            }

            @Override
            public boolean isDone() {
                if (receiver.isReady()) {
                    BeaconColorResult result = receiver.getValue();
                    telemetry.addData("leftColor", result.getLeftColor());
                    telemetry.addData("rightColor", result.getRightColor());
                } else {
                    telemetry.addData("receiver not ready", "");
                }
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return null;
            }
        };
    }

    /**
     * Uses vuforia to find the beacon target image, then uses opencv to determine the beacon color
     *
     * @param stateName         the name of the state
     * @param successState      the state to go to if it succeeds
     * @param failState         the state to go to if it fails
     * @param timeoutState      the state to go to if it times out
     * @param timeoutTime       the time before it will time out
     * @param receiver          the ResultReceiver to get the VuforiaFrameGrabberInit object from
     * @param beaconColorResult the ResultReceiver to store the result in
     * @param teamColor         your team's color to decide which beacons to look for
     * @param numFrames         the number of frames to process
     * @param saveImages        whether or not to save the frames for logging
     * @return the created State
     */
    public static State findBeaconColorState(StateName stateName, final StateName successState, final StateName failState, final StateName timeoutState, Time timeoutTime, final ResultReceiver<VuforiaFrameGrabberInit> receiver, final ResultReceiver<BeaconColorResult> beaconColorResult, TeamColor teamColor, final int numFrames, final boolean saveImages) {
        final List<BeaconName> beaconNames = BeaconName.getNamesForTeamColor(teamColor);
        final EndCondition timeout = EndConditions.timed(timeoutTime);

        return new BasicAbstractState(stateName) {
            private VuforiaFrameGrabberInit vuforia = null;
            private VuforiaBeaconColorProcessor processor = null;
            private BeaconName beaconName;
            private int beaconIndex = 0; //index of the beaconNames list
            private boolean timedOut = false;

            @Override
            public void init() {
                timeout.init();
                timedOut = false;

                if (beaconIndex >= beaconNames.size()) {
                    beaconIndex = 0;
                    //we should never go here
                }
                beaconName = beaconNames.get(beaconIndex);
                if (processor != null) {
                    processor.setBeaconName(beaconName);
                }
            }

            @Override
            public boolean isDone() {
                if (vuforia == null && receiver.isReady()) {
                    vuforia = receiver.getValue();
                    if (vuforia == null) {
                        Log.e("EVStates", "vuforia is null!!!!!!!!!!!!!");
                    }

                    processor = new VuforiaBeaconColorProcessor(vuforia);
                    processor.setBeaconName(beaconName);

                    VuforiaTrackable beacon = beacons.get(beaconName);
                    beacon.getTrackables().activate();

                    frameGrabber.setImageProcessor(processor);
                    frameGrabber.setSaveImages(saveImages);
                    frameGrabber.grabContinuousFrames();
                }
                timedOut = timeout.isDone();
                return timedOut || processor != null && processor.getResultsFound() >= numFrames;
            }

            @Override
            public StateName getNextStateName() {
                beaconIndex++;
                frameGrabber.stopFrameGrabber();
                VuforiaTrackable beacon = beacons.get(beaconName);
                beacon.getTrackables().deactivate();

                BeaconColorResult result = processor.getAverageResult();
                processor.reset();
                BeaconColorResult.BeaconColor leftColor = result.getLeftColor();
                BeaconColorResult.BeaconColor rightColor = result.getRightColor();
                if ((leftColor == BeaconColorResult.BeaconColor.RED && rightColor == BeaconColorResult.BeaconColor.BLUE)
                        || (leftColor == BeaconColorResult.BeaconColor.BLUE && rightColor == BeaconColorResult.BeaconColor.RED)) {
                    beaconColorResult.setValue(result);
                    return successState;
                } else {
                    beaconColorResult.setValue(new BeaconColorResult());
                    if (timedOut) {
                        return timeoutState;
                    } else {
                        return failState;
                    }
                }
            }
        };
    }

    /**
     * use the camera to detect and drive up to the beacon
     *
     * @param stateName       the name of the state
     * @param doneState       the state to go to if it works
     * @param lostObjectState the state to go to if it cannot find the beacon
     * @param timeoutState    the state to go to if it times out
     * @param timeoutMillis   the number of milliseconds before the timeout
     * @param mecanumControl  the mecanum wheels
     * @param frameGrabber    access to the camera frames
     * @return the created State
     */
    public static State mecanumCameraTrack(StateName stateName, final StateName doneState, final StateName lostObjectState, final StateName timeoutState, long timeoutMillis, final MecanumControl mecanumControl, final FrameGrabber frameGrabber, ImageProcessor<? extends Location> imageProcessor) {
        mecanumControl.setDriveMode(MecanumMotors.MecanumDriveMode.NORMALIZED);
        final TranslationControl beaconTrackingControl = TranslationControls.cameraTracking(frameGrabber, imageProcessor);
        final long timeoutTime = System.currentTimeMillis() + timeoutMillis;

        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
                mecanumControl.setTranslationControl(beaconTrackingControl);
            }

            @Override
            public boolean isDone() {
                mecanumControl.act();
                return !mecanumControl.translationWorked() || beaconTrackingControl.getTranslation().getLength() < 0.1 || System.currentTimeMillis() >= timeoutTime;
            }

            @Override
            public StateName getNextStateName() {
                mecanumControl.setTranslationControl(TranslationControls.zero());
                mecanumControl.stopMotors();

                if (beaconTrackingControl.getTranslation().getLength() < 0.1) {
                    return doneState;
                } else {
                    if (!mecanumControl.translationWorked()) {
                        return lostObjectState;
                    } else {
                        return timeoutState;
                    }
                }
            }
        };
    }

    /**
     * @param stateName      the name of the state
     * @param nextStateName  the name of the next state
     * @param imageProcessor the object that processes the image
     * @param resultReceiver the object that stores the image
     * @return the created State
     */
    public static State processFrame(StateName stateName, final StateName nextStateName, final ImageProcessor imageProcessor, final ResultReceiver<ImageProcessorResult> resultReceiver) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
                frameGrabber.setImageProcessor(imageProcessor);
                frameGrabber.grabSingleFrame();
            }

            @Override
            public boolean isDone() {
                return frameGrabber.isResultReady();
            }

            @Override
            public StateName getNextStateName() {
                resultReceiver.setValue(frameGrabber.getResult());
                return nextStateName;
            }
        };
    }

    /**
     * @param stateName           the name of the state
     * @param unknownUnknownState if both sides are unknown
     * @param unknownRedState     if the left is unknown and the right is red
     * @param unknownBlueState    if the left is unknown and the right is blue
     * @param redUnknownState     if the left is red and the right is unknown
     * @param redRedState         if the left is red and the right is red
     * @param redBlueState        if the left is red and the right is blue
     * @param blueUnknownState    if the left is blue and the right is unknown
     * @param blueRedState        if the left is blue and the right is red
     * @param blueBlueState       if the left is blue and the right is blue
     * @return the created State
     */
    public static State processBeaconPicture(StateName stateName,
                                             final StateName unknownUnknownState, final StateName unknownRedState, final StateName unknownBlueState,
                                             final StateName redUnknownState, final StateName redRedState, final StateName redBlueState,
                                             final StateName blueUnknownState, final StateName blueRedState, final StateName blueBlueState
    ) {

        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
                frameGrabber.setImageProcessor(new RGBBeaconProcessor());
                frameGrabber.grabSingleFrame();
            }

            @Override
            public boolean isDone() {
                return frameGrabber.isResultReady();
            }

            @Override
            public StateName getNextStateName() {
                BeaconColorResult beaconColorResult = (BeaconColorResult) frameGrabber.getResult().getResult();
                BeaconColorResult.BeaconColor leftColor = beaconColorResult.getLeftColor();
                BeaconColorResult.BeaconColor rightColor = beaconColorResult.getRightColor();

                if (leftColor == BeaconColorResult.BeaconColor.RED) {
                    if (rightColor == BeaconColorResult.BeaconColor.RED) {
                        return redRedState;
                    } else if (rightColor == BeaconColorResult.BeaconColor.BLUE) {
                        return redBlueState;
                    } else {
                        return redUnknownState;
                    }
                } else if (leftColor == BeaconColorResult.BeaconColor.BLUE) {
                    if (rightColor == BeaconColorResult.BeaconColor.RED) {
                        return blueRedState;
                    } else if (rightColor == BeaconColorResult.BeaconColor.BLUE) {
                        return blueBlueState;
                    } else {
                        return blueUnknownState;
                    }
                } else {
                    if (rightColor == BeaconColorResult.BeaconColor.RED) {
                        return unknownRedState;
                    } else if (rightColor == BeaconColorResult.BeaconColor.BLUE) {
                        return unknownBlueState;
                    } else {
                        return unknownUnknownState;
                    }
                }
            }
        };
    }

    /**
     * @param stateName the name of the state
     * @param nMotors   the motors to turn off
     * @return the created State
     */
    public static State stop(StateName stateName, final NMotors nMotors) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
                nMotors.stopMotors();
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return null;
            }
        };
    }

    /**
     * @param stateName      the name of the state
     * @param mecanumControl the motors to turn off
     * @return the created State
     */
    public static State stop(StateName stateName, final MecanumControl mecanumControl) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
                mecanumControl.stopMotors();
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return null;
            }
        };
    }

    /**
     * @param stateName the name of the state
     * @param message   the message to display to the driver station
     * @param value     the value associated with that message
     * @return the created State
     */
    public static State telemetry(StateName stateName, final String message, final double value) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                telemetry.addData(message, value);
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return null;
            }
        };
    }

    /**
     * @param stateName the name of the state
     * @param message1  the first message to display to the driver station
     * @param input1    an InputExtractor that returns the value associated with the first message
     * @param message2  the second message to display to the driver station
     * @param input2    an InputExtractor that returns the value associated with the second message
     * @return the created State
     */
    public static State telemetry(StateName stateName, final String message1, final InputExtractor<Double> input1, final String message2, final InputExtractor<Double> input2) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                telemetry.addData(message1, input1.getValue());
                telemetry.addData(message2, input2.getValue());
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return null;
            }
        };
    }

    /**
     * @param stateName   the name of the state
     * @param transitions the list of transitions to the next states
     * @param message     the message to display to the driver station
     * @param value       the value associated with that message
     * @return the created State
     */
    public static State telemetry(StateName stateName, List<Transition> transitions, final String message, final double value) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
            }

            @Override
            public void run() {
                telemetry.addData(message, value);
            }

            @Override
            public void dispose() {
            }
        };
    }

    /**
     * @param stateName   the name of the state
     * @param transitions the list of transitions to the next states
     * @param message1    the first message to display to the driver station
     * @param input1      an InputExtractor that returns the value associated with the first message
     * @param message2    the second message to display to the driver station
     * @param input2      an InputExtractor that returns the value associated with the second message
     * @return the created State
     */
    public static State telemetry(StateName stateName, List<Transition> transitions, final String message1, final InputExtractor<Double> input1, final String message2, final InputExtractor<Double> input2) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
            }

            @Override
            public void run() {
                telemetry.addData(message1, input1.getValue());
                telemetry.addData(message2, input2.getValue());
            }

            @Override
            public void dispose() {
            }
        };
    }

    /**
     * @param stateName     the name of the state
     * @param nextStateName the name of the next state
     * @param servos        the servos to be initialized
     * @return the created State
     */
    public static State servoInit(StateName stateName, final StateName nextStateName, final Servos servos) {
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                return servos.areServosDone();
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }

    /**
     * @param stateName     the name of the state
     * @param nextStateName the name of the next state
     * @param gyro          the gyro sensor to be calibrated
     * @return the created State
     */
    public static State calibrateGyro(StateName stateName, final StateName nextStateName, final GyroSensor gyro) {
        gyro.calibrate();
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                return !gyro.isCalibrating();
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }

    /**
     * @param stateName        the name of the state
     * @param nextStateName    the name of the next state
     * @param doubleLineSensor the 2 line sensors to be calibrated
     * @return the created State
     */
    public static State calibrateLineSensor(StateName stateName, final StateName nextStateName, final DoubleLineSensor doubleLineSensor) {
        doubleLineSensor.calibrate();
        return new BasicAbstractState(stateName) {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                return doubleLineSensor.isReady();
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }

    /**
     * Turn a servo to a preset at max speed
     *
     * @param stateName     the name of the state
     * @param nextStateName the name of the state to go to next
     * @param servoControl  the servo
     * @param servoPreset   the preset to go to
     * @param waitForDone   whether to wait for the servo to finish turning or move to the next state immediately
     * @return the created State
     */
    public static State servoTurn(StateName stateName, StateName nextStateName, ServoControl servoControl, Enum servoPreset, boolean waitForDone) {
        return servoTurn(stateName, nextStateName, servoControl, servoPreset, ServoControl.MAX_SPEED, waitForDone);
    }

    /**
     * Turn a servo to a preset at a given speed
     *
     * @param stateName     the name of the state
     * @param nextStateName the name of the state to go to next
     * @param servoControl  the servo
     * @param servoPreset   the preset to go to
     * @param speed         the speed to turn the servo at
     * @param waitForDone   whether to wait for the servo to finish turning or move to the next state immediately
     * @return the created State
     */
    public static State servoTurn(StateName stateName, final StateName nextStateName, final ServoControl servoControl, final Enum servoPreset, final double speed, final boolean waitForDone) {
        return new BasicAbstractState(stateName) {

            @Override
            public void init() {
                servoControl.goToPreset(servoPreset, speed);
            }

            @Override
            public boolean isDone() {
                return !waitForDone || servoControl.isDone();
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }

    /**
     * Turn a servo to a position at max speed
     *
     * @param stateName     the name of the state
     * @param nextStateName the name of the state to go to next
     * @param servoControl  the servo
     * @param servoPosition the position to go to
     * @param waitForDone   whether to wait for the servo to finish turning or move to the next state immediately
     * @return the created State
     */
    public static State servoTurn(StateName stateName, StateName nextStateName, ServoControl servoControl, double servoPosition, boolean waitForDone) {
        return servoTurn(stateName, nextStateName, servoControl, servoPosition, ServoControl.MAX_SPEED, waitForDone);
    }

    /**
     * Turn a servo to a position at a given speed
     *
     * @param stateName     the name of the state
     * @param nextStateName the name of the state to go to next
     * @param servoControl  the servo
     * @param servoPosition the position to go to
     * @param speed         the speed to turn the servo at
     * @param waitForDone   whether to wait for the servo to finish turning or move to the next state immediately
     * @return the created State
     */
    public static State servoTurn(StateName stateName, final StateName nextStateName, final ServoControl servoControl, final double servoPosition, final double speed, final boolean waitForDone) {
        return new BasicAbstractState(stateName) {

            @Override
            public void init() {
                servoControl.setPosition(servoPosition, speed);
            }

            @Override
            public boolean isDone() {
                return !waitForDone || servoControl.isDone();
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }


    /**
     * drive using the mecanum wheels
     * travels for a certain amount of time defined by the robots speed and a desired distance
     *
     * @param stateName       the name of the state
     * @param nextStateName   the next state to go to
     * @param distance        the distance to travel
     * @param mecanumControl  the mecanum wheels
     * @param gyro            the gyro sensor
     * @param velocity        the velocity to drive at
     * @param direction       the direction to drive
     * @param orientation     the angle to rotate to
     * @param maxAngularSpeed the max speed to rotate to that angle
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, final StateName nextStateName, Distance distance, final MecanumControl mecanumControl, final GyroSensor gyro, final double velocity, final Angle direction, final Angle orientation, final double maxAngularSpeed) {
        mecanumControl.setDriveMode(MecanumMotors.MecanumDriveMode.NORMALIZED);
        double speedMetersPerMillisecond = mecanumControl.getMaxRobotSpeed().metersPerMillisecond() * velocity;
        final double durationMillis = Math.abs(distance.meters() / speedMetersPerMillisecond);
        return new BasicAbstractState(stateName) {
            long startTime = 0;

            @Override
            public void init() {
                mecanumControl.setControl(
                        TranslationControls.constant(velocity, direction),
                        RotationControls.gyro(gyro, orientation, maxAngularSpeed)
                );
                startTime = System.currentTimeMillis();
            }

            @Override
            public boolean isDone() {
                long now = System.currentTimeMillis();
                long elapsedTime = now - startTime;
                mecanumControl.act();
                return elapsedTime >= durationMillis;
            }

            @Override
            public StateName getNextStateName() {
                mecanumControl.stopMotors();
                return nextStateName;
            }
        };
    }

    /**
     * drive using the mecanum wheels
     *
     * @param stateName       the name of the state
     * @param transitions     the list of transitions to the next states
     * @param mecanumControl  the mecanum wheels
     * @param gyro            the gyro sensor
     * @param velocity        the velocity to drive at
     * @param direction       the direction to drive
     * @param orientation     the angle to rotate to
     * @param maxAngularSpeed the max speed to rotate to that angle
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, List<Transition> transitions, final MecanumControl mecanumControl, final GyroSensor gyro, final double velocity, final Angle direction, final Angle orientation, final double maxAngularSpeed) {
        mecanumControl.setDriveMode(MecanumMotors.MecanumDriveMode.NORMALIZED);
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                mecanumControl.setControl(
                        TranslationControls.constant(velocity, direction),
                        RotationControls.gyro(gyro, orientation, maxAngularSpeed)
                );
            }

            @Override
            public void run() {
                mecanumControl.act();
            }

            @Override
            public void dispose() {
                mecanumControl.stopMotors();
            }
        };
    }

    /**
     * @param stateName      the name of the state
     * @param transitions    the list of transitions to the next states
     * @param mecanumControl the mecanum wheels
     * @param gyro           the gyro sensor
     * @param velocity       the velocity to drive at
     * @param direction      the direction to drive
     * @param orientation    the angle to rotate to
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, List<Transition> transitions, MecanumControl mecanumControl, GyroSensor gyro, double velocity, Angle direction, Angle orientation) {
        return mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation, RotationControl.DEFAULT_MAX_ANGULAR_SPEED);
    }

    /**
     * drive using the mecanum wheels
     * travels for a certain amount of time defined by the robots speed and a desired distance
     *
     * @param stateName      the name of the state
     * @param nextStateName  the next state to go to
     * @param distance       the distance to travel
     * @param mecanumControl the mecanum wheels
     * @param gyro           the gyro sensor
     * @param velocity       the velocity to drive at
     * @param direction      the direction to drive
     * @param orientation    the angle to rotate to
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, StateName nextStateName, Distance distance, final MecanumControl mecanumControl, final GyroSensor gyro, final double velocity, final Angle direction, final Angle orientation) {
        return mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, RotationControl.DEFAULT_MAX_ANGULAR_SPEED);
    }

    /**
     * follow a line with the mecanum wheels
     *
     * @param stateName           the name of the state
     * @param transitions         the list of transitions to the next states
     * @param lostLineState       what state to go to if the line is lost
     * @param mecanumControl      the mecanum wheels
     * @param doubleLineSensor    the 2 line sensors
     * @param velocity            the velocity to drive at
     * @param lineFollowDirection the direction (left or right) to follow the line at
     * @return the created State
     */
    public State mecanumLineFollow(StateName stateName, List<Transition> transitions, StateName lostLineState, final MecanumControl mecanumControl, final DoubleLineSensor doubleLineSensor, final double velocity, final TranslationControls.LineFollowDirection lineFollowDirection) {
        transitions.add(new Transition(new EndCondition() {
            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                return !mecanumControl.translationWorked();
            }
        }, lostLineState));

        mecanumControl.setDriveMode(MecanumMotors.MecanumDriveMode.NORMALIZED);

        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                mecanumControl.setTranslationControl(TranslationControls.lineFollow(doubleLineSensor, lineFollowDirection, velocity));
            }

            @Override
            public void run() {
                mecanumControl.act();
            }

            @Override
            public void dispose() {
                mecanumControl.setTranslationControl(TranslationControls.zero());
                mecanumControl.stopMotors();
            }
        };
    }


    /**
     * Drive forward or backward with two motors
     *
     * @param stateName   the name of the state
     * @param transitions the transitions to new states
     * @param twoMotors   the motors to move
     * @param velocity    the velocity to drive at (negative for backwards)
     * @return the created State
     */
    public static State drive(StateName stateName, List<Transition> transitions, final TwoMotors twoMotors, final double velocity) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                twoMotors.runMotors(velocity, velocity);
            }

            @Override
            public void run() {

            }

            @Override
            public void dispose() {
                twoMotors.runMotors(0, 0);
            }
        };
    }

    /**
     * Turn left or right
     *
     * @param stateName   the name of the state
     * @param transitions the transitions to new states
     * @param twoMotors   the motors to move
     * @param velocity    the velocity to turn at (negative for turning left)
     * @return the created State
     */
    public static State turn(StateName stateName, List<Transition> transitions, final TwoMotors twoMotors, final double velocity) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                twoMotors.runMotors(velocity, -velocity);
            }

            @Override
            public void run() {

            }

            @Override
            public void dispose() {
                twoMotors.runMotors(0, 0);
            }
        };
    }

    /**
     * Turn with one wheel
     *
     * @param stateName    the name of the state
     * @param transitions  the transitions to new states
     * @param twoMotors    the motors to move
     * @param isRightWheel tells which wheel to turn
     * @param velocity     the velocity to turn the wheel at (negative for backwards)
     * @return the created State
     */
    public static State oneWheelTurn(StateName stateName, List<Transition> transitions, final TwoMotors twoMotors, final boolean isRightWheel, final double velocity) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                if (isRightWheel) {
                    twoMotors.runMotors(0, velocity);
                } else {
                    twoMotors.runMotors(velocity, 0);
                }
            }

            @Override
            public void run() {

            }

            @Override
            public void dispose() {
                twoMotors.runMotors(0, 0);
            }
        };
    }


    /**
     * Drive for a certain distance
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to after the drive is done
     * @param distance      the distance to drive
     * @param maxRobotSpeed the speed of the robot at 100% power
     * @param twoMotors     the motors to move
     * @param velocity      the velocity to drive at (negative for backwards)
     * @return the created State
     */
    public static State drive(StateName stateName, StateName nextStateName, Distance distance, Velocity maxRobotSpeed, TwoMotors twoMotors, double velocity) {
        double speedMetersPerMillisecond = maxRobotSpeed.metersPerMillisecond() * velocity;
        double durationMillis = Math.abs(distance.meters() / speedMetersPerMillisecond);
        return drive(stateName, ImmutableList.of(
                new Transition(
                        EndConditions.timed((long) durationMillis),
                        nextStateName
                )
        ), twoMotors, velocity);
    }

    /**
     * Turn for a certain angle by calculating the time required for that angle
     *
     * @param stateName        the name of the state
     * @param nextStateName    the state to go to when done turning
     * @param angle            the angle to turn
     * @param minRobotTurnTime the time it takes for the robot to turn
     * @param twoMotors        the motors to run
     * @param velocity         the velocity to turn (negative for turning left)
     * @return the created State
     */
    public static State turn(StateName stateName, StateName nextStateName, Angle angle, Time minRobotTurnTime, TwoMotors twoMotors, double velocity) {
        double speedRotationsPerMillisecond = velocity / minRobotTurnTime.milliseconds();
        double durationMillis = Math.abs(angle.degrees() / 360 / speedRotationsPerMillisecond);
        return turn(stateName, ImmutableList.of(
                new Transition(
                        EndConditions.timed((long) durationMillis),
                        nextStateName
                )
        ), twoMotors, velocity);
    }

    /**
     * Turn for a certain angle using a gyro sensor
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to when done turning
     * @param angle         the angle to turn
     * @param gyro          the gyro sensor to use
     * @param twoMotors     the motors to turn
     * @param velocity      the velocity to turn at (negative to turn left)
     * @return the created State
     */
    public static State turn(StateName stateName, StateName nextStateName, Angle angle, GyroSensor gyro, TwoMotors twoMotors, double velocity) {
        return turn(stateName, ImmutableList.of(
                new Transition(
                        EVEndConditions.gyroCloseToRelative(gyro, angle, Angle.fromDegrees(5)),
                        nextStateName
                )
        ), twoMotors, velocity);
    }

    /**
     * Turn with one wheel for a certain angle by calculating the time needed to turn that angle
     *
     * @param stateName        the name of the state
     * @param nextStateName    the state to go to when done turning
     * @param angle            the angle to turn
     * @param minRobotTurnTime the time it takes for the robot to turn
     * @param twoMotors        the motors to turn
     * @param isRightWheel     tells which wheel to turn
     * @param velocity         the velocity to turn the wheel at (negative for backwards)
     * @return the created State
     */
    public static State oneWheelTurn(StateName stateName, StateName nextStateName, Angle angle, Time minRobotTurnTime, TwoMotors twoMotors, boolean isRightWheel, double velocity) {
        double speedRotationsPerMillisecond = velocity / minRobotTurnTime.milliseconds();
        double durationMillis = Math.abs(2 * angle.degrees() / 360 / speedRotationsPerMillisecond);
        velocity = Math.abs(velocity) * Math.signum(angle.radians());
        if (isRightWheel) {
            velocity *= -1;
        }
        return oneWheelTurn(stateName, ImmutableList.of(
                new Transition(
                        EndConditions.timed((long) durationMillis),
                        nextStateName
                )
        ), twoMotors, isRightWheel, velocity);
    }

    /**
     * Turn with one wheel for a certain angle using a gyro sensor
     *
     * @param stateName     the name of the state
     * @param nextStateName the state to go to after the turn is done
     * @param angle         the angle to turn
     * @param gyro          the gyro sensor to use
     * @param twoMotors     the motors to turn
     * @param isRightWheel  which wheel to use
     * @param velocity      the velocity to turn the wheel at (negative for backwards)
     * @return the created State
     */
    public static State turn(StateName stateName, StateName nextStateName, Angle angle, GyroSensor gyro, TwoMotors twoMotors, boolean isRightWheel, double velocity) {
        velocity = Math.abs(velocity) * Math.signum(angle.radians());
        if (isRightWheel) {
            velocity *= -1;
        }
        return oneWheelTurn(stateName, ImmutableList.of(
                new Transition(
                        EVEndConditions.gyroCloseToRelative(gyro, angle, Angle.fromDegrees(5)),
                        nextStateName
                )
        ), twoMotors, isRightWheel, velocity);
    }

    /**
     * Turn a motor at a given power
     *
     * @param stateName    the name of the state
     * @param transitions  the transitions to new states
     * @param motor        the motor to be turned
     * @param power        the power to turn the motor at
     * @param stopBehavior whether to brake or coast when the motor stops
     * @return the created State
     */
    public static State motorTurn(StateName stateName, List<Transition> transitions, final Motor motor, final double power, final Motor.StopBehavior stopBehavior) {
        return new AbstractState(stateName, transitions) {
            @Override
            public void init() {
                motor.setStopBehavior(stopBehavior);
                motor.setPower(power);
            }

            @Override
            public void run() {

            }

            @Override
            public void dispose() {
                motor.setPower(0);
            }
        };
    }

    /**
     * Line up with the beacon using the line sensor array and distance sensor
     *
     * @param stateName         the name of the state
     * @param successState      the state to go to if the line up succeeds
     * @param failState         the state to go to if the line up fails
     * @param mecanumControl    the mecanum wheels
     * @param direction         the direction angle to face
     * @param gyro              the gyro to use for rotation stabilization
     * @param distSensor        the distance sensor to detect distance from the beacon
     * @param lineSensorArray   the line sensor array to line up sideways with the line
     * @param teamColor         the team you are on and ...
     * @param beaconColorResult ... the beacon configuration to decide which button to line up with
     * @param distance          the distance from the beacon to line up to
     * @return the created State
     */
    public static State beaconLineUp(StateName stateName, final StateName successState, final StateName failState, final MecanumControl mecanumControl, final Angle direction, final GyroSensor gyro, final DistanceSensor distSensor, final LineSensorArray lineSensorArray, TeamColor teamColor, final ResultReceiver<BeaconColorResult> beaconColorResult, final Distance distance) {
        final EndCondition dist = EVEndConditions.distanceSensorLess(distSensor, distance);
        final BeaconColorResult.BeaconColor myColor = BeaconColorResult.BeaconColor.fromTeamColor(teamColor);
        final BeaconColorResult.BeaconColor opponentColor = BeaconColorResult.BeaconColor.fromTeamColor(teamColor.opposite());

        return new BasicAbstractState(stateName) {
            private boolean success;
            int target = 0;

            @Override
            public void init() {
                success = false;

                if (beaconColorResult.isReady()) {
                    BeaconColorResult result = beaconColorResult.getValue();
                    BeaconColorResult.BeaconColor leftColor = result.getLeftColor();
                    BeaconColorResult.BeaconColor rightColor = result.getRightColor();
                    if (leftColor == myColor && rightColor == opponentColor) {
                        target = 2;
                        success = true;
                    }
                    if (leftColor == opponentColor && rightColor == myColor) {
                        target = 14;
                        success = true;
                    }
                }

                mecanumControl.setControl(
                        RotationControls.gyro(gyro, direction),
                        TranslationControls.lineUp(lineSensorArray, target, new PIDController(-0.1, 0, 0, 1), distSensor, distance, new PIDController(0.1, 0, 0, 1))
                );
                dist.init();
            }

            @Override
            public boolean isDone() {

                if (!mecanumControl.translationWorked()) {
                    success = false;
                }
                return !success || dist.isDone();
            }

            @Override
            public StateName getNextStateName() {
                mecanumControl.setControl(
                        TranslationControls.zero(),
                        RotationControls.zero()
                );
                return success ? successState : failState;
            }
        };
    }

}
