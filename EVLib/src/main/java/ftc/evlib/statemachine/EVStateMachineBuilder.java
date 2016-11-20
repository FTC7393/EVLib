package ftc.evlib.statemachine;

import com.qualcomm.robotcore.hardware.GyroSensor;

import java.util.List;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.Transition;
import ftc.electronvolts.util.ResultReceiver;
import ftc.electronvolts.util.TeamColor;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.sensors.DistanceSensor;
import ftc.evlib.hardware.sensors.LineSensorArray;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.processors.BeaconColorResult;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/10/16
 * <p>
 * Builder that uses the EVStates Factory class to create states and build them into a StateMachine
 * extends StateMachineBuilder which has the basic builder methods as well as some useful addXYZ methods
 *
 * @see EVStates
 * @see StateMachineBuilder
 */
public class EVStateMachineBuilder extends StateMachineBuilder {
    private final MecanumControl mecanumControl;
    private final GyroSensor gyro;
    private final FrameGrabber frameGrabber;
    private final Servos servos;
    private final DistanceSensor distanceSensor;
    private final LineSensorArray lineSensorArray;
    private final TeamColor teamColor;

    /**
     * any of the parameters can be null if the robot does not have it
     *
     * @param firstStateName  the state to start with
     * @param mecanumControl  the mecanum wheel controller
     * @param gyro            the gyro sensor
     * @param frameGrabber    access to the camera
     * @param servos          the servos
     * @param distanceSensor  the distance sensor
     * @param lineSensorArray the line sensor array
     * @param teamColor       the alliance you are on
     */
    public EVStateMachineBuilder(StateName firstStateName, MecanumControl mecanumControl, GyroSensor gyro, FrameGrabber frameGrabber, Servos servos, DistanceSensor distanceSensor, LineSensorArray lineSensorArray, TeamColor teamColor) {
        super(firstStateName);
        this.mecanumControl = mecanumControl;
        this.gyro = gyro;
        this.frameGrabber = frameGrabber;
        this.servos = servos;
        this.distanceSensor = distanceSensor;
        this.lineSensorArray = lineSensorArray;
        this.teamColor = teamColor;
    }

    public EVStateMachineBuilder(StateName firstStateName, EVStateMachineBuilder builder) {
        this(firstStateName, builder.mecanumControl, builder.gyro, builder.frameGrabber, builder.servos, builder.distanceSensor, builder.lineSensorArray, builder.teamColor);
    }

//    public void addCameraTracking(StateName stateName, StateName doneState, StateName lostObjectState, long timeoutMillis, StateName timeoutState, ImageProcessor<? extends Location> imageProcessor) {
//        add(EVStates.mecanumCameraTrack(stateName, doneState, lostObjectState, timeoutMillis, timeoutState, mecanumControl, frameGrabber, imageProcessor));
//    }

    //convenience methods for adding different types of States

    public void addServoInit(StateName stateName, StateName nextStateName) {
        add(EVStates.servoInit(stateName, nextStateName, servos));
    }

    public void addCalibrateGyro(StateName stateName, StateName nextStateName) {
        add(EVStates.calibrateGyro(stateName, nextStateName, gyro));
    }

    public void addStop(StateName stateName) {
        add(EVStates.stop(stateName, mecanumControl));
    }

    ///// START DRIVE STATES /////
    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, double directionDegrees, double orientationDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees), maxAngularSpeed));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, double directionDegrees, double orientationDegrees) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees)));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, double directionDegrees, double orientationDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees), maxAngularSpeed));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, double directionDegrees, double orientationDegrees) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees)));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation));
    }
    ///// END DRIVE STATES /////

    ///// START SERVO STATES /////
    private ServoControl getServo(ServoName servoName) {
        return servos.getServoMap().get(servoName);
    }

    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, double position, boolean waitForDone) {
        add(EVStates.servoTurn(stateName, nextStateName, getServo(servoName), position, waitForDone));
    }

    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, double position, double speed, boolean waitForDone) {
        add(EVStates.servoTurn(stateName, nextStateName, getServo(servoName), position, speed, waitForDone));
    }

    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum preset, boolean waitForDone) {
        add(EVStates.servoTurn(stateName, nextStateName, getServo(servoName), preset, waitForDone));
    }

    public void addServo(StateName stateName, StateName nextStateName, ServoName servoName, Enum preset, double speed, boolean waitForDone) {
        add(EVStates.servoTurn(stateName, nextStateName, getServo(servoName), preset, speed, waitForDone));
    }
    ///// END SERVO STATES /////

    public void addTelem(StateName stateName, String message, double value, List<Transition> transitions) {
        add(EVStates.telemetry(stateName, transitions, message, value));
    }

    public void addTelem(StateName stateName, String message, double value) {
        add(EVStates.telemetry(stateName, message, value));
    }

    public void addBeaconLineUp(StateName stateName, StateName successState, StateName failState, Angle direction, ResultReceiver<BeaconColorResult> beaconColorResult, Distance distance) {
        add(EVStates.beaconLineUp(stateName, successState, failState, mecanumControl, direction, gyro, distanceSensor, lineSensorArray, teamColor, beaconColorResult, distance));
    }
}
