package ftc.evlib.statemachine;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.GyroSensor;

import java.util.List;
import java.util.Map;

import ftc.electronvolts.statemachine.AbstractState;
import ftc.electronvolts.statemachine.BasicAbstractState;
import ftc.electronvolts.statemachine.EndCondition;
import ftc.electronvolts.statemachine.EndConditions;
import ftc.electronvolts.statemachine.State;
import ftc.electronvolts.statemachine.StateMachine;
import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.States;
import ftc.electronvolts.statemachine.Transition;
import ftc.electronvolts.util.Angle;
import ftc.electronvolts.util.Distance;
import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.ResultReceiver;
import ftc.electronvolts.util.Time;
import ftc.electronvolts.util.Velocity;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.control.RotationControl;
import ftc.evlib.hardware.control.RotationControls;
import ftc.evlib.hardware.control.TranslationControl;
import ftc.evlib.hardware.control.TranslationControls;
import ftc.evlib.hardware.motors.MecanumMotors;
import ftc.evlib.hardware.motors.NMotors;
import ftc.evlib.hardware.motors.TwoMotors;
import ftc.evlib.hardware.sensors.DoubleLineSensor;
import ftc.evlib.hardware.servos.ServoCommand;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.processors.BeaconColorResult;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.ImageProcessorResult;
import ftc.evlib.vision.processors.Location;
import ftc.evlib.vision.processors.RGBBeaconProcessor;

import static ftc.evlib.driverstation.Telem.telemetry;
import static ftc.evlib.vision.framegrabber.GlobalFrameGrabber.frameGrabber;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/10/16
 */
public class EVStates extends States {

    public static State subStates(StateName stateName, final StateMachineBuilder stateMachineBuilder, final Map<StateName, StateName> subStateToState) {
        StateName firstState = stateMachineBuilder.build().getCurrentStateName();
        for (Map.Entry<StateName, StateName> entry : subStateToState.entrySet()) {
            StateName subState = entry.getKey();
            stateMachineBuilder.add(States.basicEmpty(subState, firstState));
        }
        final StateMachine stateMachine = stateMachineBuilder.build();
        return new BasicAbstractState(stateName) {
            private StateName nextStateName;

            @Override
            public void init() {
            }

            @Override
            public boolean isDone() {
                stateMachine.act();
                for (Map.Entry<StateName, StateName> entry : subStateToState.entrySet()) {
                    //if the current state is one of the ending sub-states
                    if (stateMachine.getCurrentStateName() == entry.getKey()) {
                        //go to the corresponding super-state
                        nextStateName = entry.getValue();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public StateName getNextStateName() {
                return nextStateName;
            }
        };
    }

    /**
     * use the camera to detect and drive up to the beacon
     *
     * @param stateName       the name of the state
     * @param doneState       the state to go to if it works
     * @param lostObjectState the state to go to if it cannot find the beacon
     * @param timeoutMillis   the number of milliseconds before the timeout
     * @param timeoutState    the state to go to if it times out
     * @param mecanumControl  the mecanum wheels
     * @param frameGrabber    access to the camera frames
     * @return the created State
     */
    public static State mecanumCameraTrack(StateName stateName, final StateName doneState, final StateName lostObjectState, long timeoutMillis, final StateName timeoutState, final MecanumControl mecanumControl, final FrameGrabber frameGrabber, ImageProcessor<? extends Location> imageProcessor) {
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
     * @param imageProcessor the object that processes the image
     * @param resultReceiver the object that stores the image
     * @param nextStateName  the name of the next state
     * @return the created State
     */
    public static State processFrame(StateName stateName, final ImageProcessor imageProcessor, final ResultReceiver<ImageProcessorResult> resultReceiver, final StateName nextStateName) {
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
     * @param servos        the servos to be initialized
     * @param nextStateName the name of the next state
     * @return the created State
     */
    public static State servoInit(StateName stateName, final Servos servos, final StateName nextStateName) {
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
     * @param gyro          the gyro sensor to be calibrated
     * @param nextStateName the name of the next state
     * @return the created State
     */
    public static State calibrateGyro(StateName stateName, final GyroSensor gyro, final StateName nextStateName) {
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
     * @param doubleLineSensor the 2 line sensors to be calibrated
     * @param nextStateName    the name of the next state
     * @return the created State
     */
    public static State calibrateLineSensor(StateName stateName, final DoubleLineSensor doubleLineSensor, final StateName nextStateName) {
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
     * move a servo
     *
     * @param stateName     the name of the state
     * @param servoCommand  where to move the servo
     * @param waitForDone   if false, move on to the next state immediately. if true, wait for the servo to finish turning
     * @param nextStateName the name of the next state
     * @return the created State
     */
    public static State servoTurn(StateName stateName, final ServoCommand servoCommand, final boolean waitForDone, final StateName nextStateName) {
        final ServoControl servoControl = servoCommand.getServo();
        return new BasicAbstractState(stateName) {

            @Override
            public void init() {
                servoControl.go(servoCommand);
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
     * @param distance        the distance to travel
     * @param nextStateName   the next state to go to
     * @param mecanumControl  the mecanum wheels
     * @param gyro            the gyro sensor
     * @param velocity        the velocity to drive at
     * @param direction       the direction to drive
     * @param orientation     the angle to rotate to
     * @param maxAngularSpeed the max speed to rotate to that angle
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, Distance distance, final StateName nextStateName, final MecanumControl mecanumControl, final GyroSensor gyro, final double velocity, final Angle direction, final Angle orientation, final double maxAngularSpeed) {
        mecanumControl.setDriveMode(MecanumMotors.MecanumDriveMode.NORMALIZED);
        double speedMetersPerMillisecond = mecanumControl.getMaxRobotSpeed().metersPerMillisecond() * velocity;
        final double durationMillis = Math.abs(distance.meters() / speedMetersPerMillisecond);
        return new BasicAbstractState(stateName) {
            long startTime = 0;

            @Override
            public void init() {
                mecanumControl.setTranslationControl(TranslationControls.constant(velocity, direction));
                mecanumControl.setRotationControl(RotationControls.gyro(gyro, orientation, maxAngularSpeed));
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
                mecanumControl.setTranslationControl(TranslationControls.constant(velocity, direction));
                mecanumControl.setRotationControl(RotationControls.gyro(gyro, orientation, maxAngularSpeed));
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
     * @param distance       the distance to travel
     * @param nextStateName  the next state to go to
     * @param mecanumControl the mecanum wheels
     * @param gyro           the gyro sensor
     * @param velocity       the velocity to drive at
     * @param direction      the direction to drive
     * @param orientation    the angle to rotate to
     * @return the created State
     */
    public static State mecanumDrive(StateName stateName, Distance distance, StateName nextStateName, final MecanumControl mecanumControl, final GyroSensor gyro, final double velocity, final Angle direction, final Angle orientation) {
        return mecanumDrive(stateName, distance, nextStateName, mecanumControl, gyro, velocity, direction, orientation, RotationControl.DEFAULT_MAX_ANGULAR_SPEED);
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


    public static State drive(StateName stateName, Distance distance, StateName nextStateName, Velocity maxRobotSpeed, TwoMotors twoMotors, double velocity) {
        double speedMetersPerMillisecond = maxRobotSpeed.metersPerMillisecond() * velocity;
        double durationMillis = Math.abs(distance.meters() / speedMetersPerMillisecond);
        return drive(stateName, ImmutableList.of(
                new Transition(
                        EndConditions.timed((long) durationMillis),
                        nextStateName
                )
        ), twoMotors, velocity);
    }


    public static State turn(StateName stateName, Angle angle, StateName nextStateName, Time minRobotTurnTime, TwoMotors twoMotors, double velocity) {
        double speedRotationsPerMillisecond = velocity / minRobotTurnTime.milliseconds();
        double durationMillis = Math.abs(angle.degrees() / 360 / speedRotationsPerMillisecond);
        return turn(stateName, ImmutableList.of(
                new Transition(
                        EndConditions.timed((long) durationMillis),
                        nextStateName
                )
        ), twoMotors, velocity);
    }


    public static State turn(StateName stateName, Angle angle, StateName nextStateName, GyroSensor gyro, TwoMotors twoMotors, double velocity) {
        return turn(stateName, ImmutableList.of(
                new Transition(
                        EVEndConditions.gyroCloseToRelative(gyro, angle.degrees(), 5),
                        nextStateName
                )
        ), twoMotors, velocity);
    }


    public static State oneWheelTurn(StateName stateName, Angle angle, StateName nextStateName, Time minRobotTurnTime, TwoMotors twoMotors, boolean isRightWheel, double velocity) {
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


    public static State turn(StateName stateName, Angle angle, StateName nextStateName, GyroSensor gyro, TwoMotors twoMotors, boolean isRightWheel, double velocity) {
        velocity = Math.abs(velocity) * Math.signum(angle.radians());
        if (isRightWheel) {
            velocity *= -1;
        }
        return oneWheelTurn(stateName, ImmutableList.of(
                new Transition(
                        EVEndConditions.gyroCloseToRelative(gyro, angle.degrees(), 5),
                        nextStateName
                )
        ), twoMotors, isRightWheel, velocity);
    }

}
