package ftc.evlib.statemachine;

import com.qualcomm.robotcore.hardware.GyroSensor;

import java.util.HashMap;
import java.util.List;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.Transition;
import ftc.electronvolts.util.Angle;
import ftc.electronvolts.util.Distance;
import ftc.electronvolts.util.Time;
import ftc.electronvolts.util.Velocity;
import ftc.evlib.hardware.control.MecanumControl;
import ftc.evlib.hardware.motors.MecanumMotors;
import ftc.evlib.hardware.motors.Motor;
import ftc.evlib.hardware.motors.Motors;
import ftc.evlib.hardware.sensors.FakeGyroSensor;
import ftc.evlib.hardware.servos.ServoCommand;
import ftc.evlib.hardware.servos.ServoControl;
import ftc.evlib.hardware.servos.ServoName;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.vision.framegrabber.FrameGrabber;
import ftc.evlib.vision.framegrabber.GlobalFrameGrabber;
import ftc.evlib.vision.processors.ImageProcessor;
import ftc.evlib.vision.processors.Location;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 5/10/16
 * <p>
 * Builder that uses the EVStates Factory class to create states and build them into a StateMachine
 * extends StateMachineBuilder which has the basic builder methods as well as some useful addXYZ methods
 */
public class EVStateMachineBuilder extends StateMachineBuilder {
    private final MecanumControl mecanumControl;
    private final GyroSensor gyro;
    private final FrameGrabber frameGrabber;
    private final Servos servos;

    public static EVStateMachineBuilder dummy() {
        return new EVStateMachineBuilder(
                new StateName() {
                    @Override
                    public String name() {
                        return "";
                    }
                },
                new MecanumControl(
                        new MecanumMotors(
                                Motors.dummyMotor(), Motors.dummyMotor(), Motors.dummyMotor(), Motors.dummyMotor(),
                                false, Motor.StopBehavior.BRAKE
                        ), new Velocity(Distance.fromMeters(1), Time.fromSeconds(1))
                ),
                new FakeGyroSensor(),
                GlobalFrameGrabber.frameGrabber,
                new Servos(new HashMap<ServoName, ServoControl>())
        );
    }

    /**
     * any of the parameters can be null if the robot does not have it
     *
     * @param firstStateName the state to start with
     * @param mecanumControl the mecanum wheel controller
     * @param gyro           the gyro sensor
     * @param frameGrabber   access to the camera
     * @param servos         the servos
     */
    public EVStateMachineBuilder(StateName firstStateName, MecanumControl mecanumControl, GyroSensor gyro, FrameGrabber frameGrabber, Servos servos) {
        super(firstStateName);
        this.mecanumControl = mecanumControl;
        this.gyro = gyro;
        this.frameGrabber = frameGrabber;
        this.servos = servos;
    }

    public EVStateMachineBuilder(StateName firstStateName, EVStateMachineBuilder builder) {
        this(firstStateName, builder.mecanumControl, builder.gyro, builder.frameGrabber, builder.servos);
    }

    public void addCameraTracking(StateName stateName, StateName doneState, StateName lostObjectState, long timeoutMillis, StateName timeoutState, ImageProcessor<? extends Location> imageProcessor) {
        add(EVStates.mecanumCameraTrack(stateName, doneState, lostObjectState, timeoutMillis, timeoutState, mecanumControl, frameGrabber, imageProcessor));
    }

    public void addServoInit(StateName stateName, StateName nextStateName) {
        add(EVStates.servoInit(stateName, servos, nextStateName));
    }

    public void addCalibrateGyro(StateName stateName, StateName nextStateName) {
        add(EVStates.calibrateGyro(stateName, gyro, nextStateName));
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

    public void addDrive(StateName stateName, Distance distance, StateName nextStateName, double velocity, double directionDegrees, double orientationDegrees, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, distance, nextStateName, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees), maxAngularSpeed));
    }

    public void addDrive(StateName stateName, Distance distance, StateName nextStateName, double velocity, double directionDegrees, double orientationDegrees) {
        add(EVStates.mecanumDrive(stateName, distance, nextStateName, mecanumControl, gyro, velocity, Angle.fromDegrees(directionDegrees), Angle.fromDegrees(orientationDegrees)));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, List<Transition> transitions, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, transitions, mecanumControl, gyro, velocity, direction, orientation));
    }

    public void addDrive(StateName stateName, Distance distance, StateName nextStateName, double velocity, Angle direction, Angle orientation, double maxAngularSpeed) {
        add(EVStates.mecanumDrive(stateName, distance, nextStateName, mecanumControl, gyro, velocity, direction, orientation, maxAngularSpeed));
    }

    public void addDrive(StateName stateName, Distance distance, StateName nextStateName, double velocity, Angle direction, Angle orientation) {
        add(EVStates.mecanumDrive(stateName, distance, nextStateName, mecanumControl, gyro, velocity, direction, orientation));
    }
    ///// END DRIVE STATES /////

    public void addServo(StateName stateName, ServoCommand servoCommand, boolean waitForDone, StateName nextStateName) {
        add(EVStates.servoTurn(stateName, servoCommand, waitForDone, nextStateName));
    }

    public void addTelem(StateName stateName, String message, double value, List<Transition> transitions) {
        add(EVStates.telemetry(stateName, transitions, message, value));
    }

    public void addTelem(StateName stateName, String message, double value) {
        add(EVStates.telemetry(stateName, message, value));
    }

    public void addBasicEmpty(StateName stateName, StateName nextStateName) {
        add(EVStates.basicEmpty(stateName, nextStateName));
    }
}
