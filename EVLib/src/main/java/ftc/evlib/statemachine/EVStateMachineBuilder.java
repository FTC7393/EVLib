package ftc.evlib.statemachine;

import com.qualcomm.robotcore.hardware.GyroSensor;

import java.util.List;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.Transition;
import ftc.electronvolts.util.TeamColor;
import ftc.electronvolts.util.units.Angle;
import ftc.electronvolts.util.units.Distance;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.mechanisms.Shooter;
import ftc.evlib.hardware.sensors.DistanceSensor;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.vision.framegrabber.FrameGrabber;

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
    private final TeamColor teamColor;
    private final Angle tolerance;
    private final MecanumControl mecanumControl;
    private final GyroSensor gyro;
    private final FrameGrabber frameGrabber;
    private final Servos servos;
    private final DistanceSensor distanceSensor;
    private final Shooter shooter;

    /**
     * any of the parameters can be null if the robot does not have it
     *
     * @param firstStateName the state to start with
     * @param teamColor      the alliance you are on
     * @param tolerance      the tolerance on gyro angles
     * @param gyro           the gyro sensor
     * @param frameGrabber   access to the camera
     * @param servos         the servos
     * @param distanceSensor the distance sensor
     * @param mecanumControl the mecanum wheel controller
     * @param shooter        the helper class for the mecanism that shoots the particles
     */
    public EVStateMachineBuilder(StateName firstStateName, TeamColor teamColor, Angle tolerance, GyroSensor gyro, FrameGrabber frameGrabber, Servos servos, DistanceSensor distanceSensor, MecanumControl mecanumControl, Shooter shooter) {
        super(firstStateName);
        this.teamColor = teamColor;
        this.tolerance = tolerance;
        this.mecanumControl = mecanumControl;
        this.gyro = gyro;
        this.frameGrabber = frameGrabber;
        this.servos = servos;
        this.distanceSensor = distanceSensor;
        this.shooter = shooter;
    }

    public EVStateMachineBuilder(StateName firstStateName, EVStateMachineBuilder b) {
        this(firstStateName, b.teamColor, b.tolerance, b.gyro, b.frameGrabber, b.servos, b.distanceSensor, b.mecanumControl, b.shooter);
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

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation));
    }


    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, double directionDegrees, double orientationDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees), tolerance, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, double directionDegrees, double orientationDegrees) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees), tolerance));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, tolerance, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, tolerance));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation, Angle tolerance, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, tolerance, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, StateName nextStateName, Distance distance, double velocity, Angle direction, Angle orientation, Angle tolerance) {
        add(EVStates.mecanumDrive(stateName, nextStateName, distance, mecanumControl, gyro, velocity, direction, orientation, tolerance));
    }
    ///// END DRIVE STATES /////

    ///// START TURN STATES /////
    public void addGyroTurn(StateName stateName, List<Transition> transitions, double orientationDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, 0, Angle.zero(), Angle.fromDegrees(orientationDegrees), maxAngularSpeed));
    }

    public void addGyroTurn(StateName stateName, List<Transition> transitions, double orientationDegrees) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, 0, Angle.zero(), Angle.fromDegrees(orientationDegrees)));
    }

    public void addGyroTurn(StateName stateName, List<Transition> transitions, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, 0, Angle.zero(), orientation, maxAngularSpeed));
    }

    public void addGyroTurn(StateName stateName, List<Transition> transitions, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, 0, Angle.zero(), orientation));
    }

    public void addGyroTurn(StateName stateName, StateName nextStateName, double orientationDegrees, double toleranceDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, Distance.zero(), mecanumControl, gyro, 0, Angle.zero(), Angle.fromDegrees(orientationDegrees), Angle.fromDegrees(toleranceDegrees), maxAngularSpeed));
    }

    public void addGyroTurn(StateName stateName, StateName nextStateName, double orientationDegrees, double toleranceDegrees) {
        add(EVStates.mecanumDrive(stateName, nextStateName, Distance.zero(), mecanumControl, gyro, 0, Angle.zero(), Angle.fromDegrees(orientationDegrees), Angle.fromDegrees(toleranceDegrees)));
    }

    public void addGyroTurn(StateName stateName, StateName nextStateName, Angle orientation, Angle tolerance, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, nextStateName, Distance.zero(), mecanumControl, gyro, 0, Angle.zero(), orientation, tolerance, maxAngularSpeed));
    }

    public void addGyroTurn(StateName stateName, StateName nextStateName, Angle orientation, Angle tolerance) {
        add(EVStates.mecanumDrive(stateName, nextStateName, Distance.zero(), mecanumControl, gyro, 0, Angle.zero(), orientation, tolerance));
    }
    ///// END TURN STATES /////

    ///// START SERVO STATES /////
    private ServoControl getServo(ServoName servoName) {
        if (!servos.getServoMap().containsKey(servoName)) {
            throw new IllegalArgumentException("ServoName \"" + servoName + "\" was not found in the servoMap");
        }
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

//    public void addBeaconLineUp(StateName stateName, StateName successState, StateName failState, Angle direction, ResultReceiver<BeaconColorResult> beaconColorResult, Distance distance) {
//        add(EVStates.beaconLineUp(stateName, successState, failState, mecanumControl, direction, gyro, distanceSensor, lineSensorArray, teamColor, beaconColorResult, distance));
//    }

    public void addShoot(StateName stateName, StateName nextStateName, int shots) {
        add(EVStates.shoot(stateName, nextStateName, shooter, shots));
    }
}
