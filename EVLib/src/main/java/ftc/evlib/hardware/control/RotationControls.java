package ftc.evlib.hardware.control;

import com.qualcomm.robotcore.hardware.GyroSensor;

import ftc.electronvolts.util.InputExtractor;
import ftc.electronvolts.util.Vector2D;
import ftc.electronvolts.util.units.Angle;

import static ftc.evlib.driverstation.Telem.telemetry;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/20/16
 * <p>
 * Factory class for RotationControl
 *
 * @see RotationControl
 */

public class RotationControls {
    /**
     * No movement
     */
    public static final RotationControl ZERO = constant(0);

    /**
     * rotate at a constant velocity
     *
     * @param velocityR the velocity to rotate at
     * @return the created RotationControl
     */
    public static RotationControl constant(final double velocityR) {
        return new RotationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public double getVelocityR() {
                return velocityR;
            }

            @Override
            public Angle getPolarDirectionCorrection() {
                return Angle.fromRadians(0);
            }
        };
    }

    /**
     * Rotate according to an InputExtractor's value (such as a driver joystick)
     *
     * @param rotation the InputExtractor
     * @return the created RotationControl
     */
    public static RotationControl inputExtractor(final InputExtractor<Double> rotation) {
        return new RotationControl() {
            @Override
            public boolean act() {
                return true;
            }

            @Override
            public double getVelocityR() {
                return rotation.getValue();
            }

            @Override
            public Angle getPolarDirectionCorrection() {
                return Angle.fromRadians(0);
            }
        };
    }

    public static RotationControl gyro(GyroSensor gyro, Angle targetHeading) {
        return gyro(gyro, targetHeading, RotationControl.DEFAULT_MAX_ANGULAR_SPEED);
    }

    /**
     * Controls the rotation of a mecanum robot with a gyro sensor
     *
     * @param gyro            the gyro sensor
     * @param targetHeading   the direction to rotate to
     * @param maxAngularSpeed the max speed to rotate at
     * @return the created RotationControl
     */
    public static RotationControl gyro(final GyroSensor gyro, final Angle targetHeading, final double maxAngularSpeed) {
        final double GYRO_GAIN = 0.2;
//        final ControlLoop GYRO_PID = new PIDController(.1, 0, 0, maxAngularSpeed);
//        final ControlLoop GYRO_PID = new PIDController(.1, .000005, 0, maxAngularSpeed);
//        final ControlLoop GYRO_PID = new PIDController(.1, 0, .05, maxAngularSpeed);
//        final double GYRO_GAIN = .1;
//        final double GYRO_GAIN = 1;
//        final double GYRO_DEADZONE = 0.01;
        final double GYRO_DEADZONE = 0.01;
        final double minAngularSpeed = 0.05;


        final Vector2D targetHeadingVector = new Vector2D(1, targetHeading);

        return new RotationControl() {
            private double gyroHeading, rotationCorrection;

            @Override
            public boolean act() {

                //get the gyro heading and convert it to a vector
                gyroHeading = gyro.getHeading();
                Vector2D gyroVector = new Vector2D(1, Angle.fromDegrees(gyroHeading));

                //find the "signed angular separation", the magnitude and direction of the error
                double angleRadians = Vector2D.signedAngularSeparation(targetHeadingVector, gyroVector).radians();
                telemetry.addData("signed angular separation", angleRadians);

//              This graph shows angle error vs. rotation correction
//              ____________________________
//              | correction.       ____   |
//              |           .      /       |
//              |           .   __/        |
//              | ........__.__|.......... |
//              |      __|  .     error    |
//              |     /     .              |
//              | ___/      .              |
//              |__________________________|
//
//              The following code creates this graph:

                //scale the signedAngularSeparation by a constant
                rotationCorrection = GYRO_GAIN * angleRadians;
//                rotationCorrection = GYRO_PID.computeCorrection(0, angleRadians);

                if (Math.abs(rotationCorrection) > maxAngularSpeed) {
                    //cap the rotationCorrection at +/- maxAngularSpeed
                    rotationCorrection = Math.signum(rotationCorrection) * maxAngularSpeed;
                } else if (Math.abs(rotationCorrection) < GYRO_DEADZONE) {
                    //set it to 0 if it is in the deadzone
                    rotationCorrection = 0;
                } else if (Math.abs(rotationCorrection) < minAngularSpeed) {
                    //set it to the minimum if it is below
                    rotationCorrection = Math.signum(rotationCorrection) * minAngularSpeed;
                }

                telemetry.addData("rotationCorrection", rotationCorrection);

                return true;
            }

            @Override
            public double getVelocityR() {
                return rotationCorrection;
            }

            @Override
            public Angle getPolarDirectionCorrection() {
                return Angle.fromDegrees(-gyroHeading);
            }
        };
    }

    /**
     * Use driver input and do gyro stabilization when the input is zero
     * Apply corrections at the default max rotation speed
     *
     * @param driver the driver input
     * @param gyro   the gyro to use for stabilization
     * @return the created RotationControl
     */
    public static RotationControl teleOpGyro(InputExtractor<Double> driver, GyroSensor gyro) {
        return teleOpGyro(driver, gyro, RotationControl.DEFAULT_MAX_ANGULAR_SPEED);
    }


    private enum TeleOpGyroMode {
        INIT,
        DRIVER,
        WAIT,
        GYRO
    }

    /**
     * Use driver input and do gyro stabilization when the input is zero
     *
     * @param driver          the driver input
     * @param gyro            the gyro to use for stabilization
     * @param maxAngularSpeed the maximum speed to rotate at when doing gyro stabilization
     * @return the created RotationControl
     */
    public static RotationControl teleOpGyro(final InputExtractor<Double> driver, final GyroSensor gyro, final double maxAngularSpeed) {
        final long DELAY_BEFORE_GYRO_CONTROL = 500;
        final long INIT_TIME = 4000;

        final long startTime = System.currentTimeMillis();

        gyro.calibrate();

        return new RotationControl() {

            //keeps track of who is in control: the driver or the gyro
            private TeleOpGyroMode mode = TeleOpGyroMode.INIT;

            //the output of this controller
            private double velocityR;

            //the RotationControl that uses the gyro
            private RotationControl gyroControl;

            private long driverEndTime;

            @Override
            public boolean act() {
//                Log.v("TeleOpGyro", "driver: " + driver.getValue() + "  mode: " + mode + "  gyroHeading: " + gyro.getHeading());

                velocityR = driver.getValue(); //get the value from the driver

                if (mode == TeleOpGyroMode.INIT) {
                    if (System.currentTimeMillis() - startTime < INIT_TIME) {
                        return true;
                    } else {
                        mode = TeleOpGyroMode.DRIVER;
                    }
                }
                //if the driver's input is 0, use the gyro control
                if (velocityR == 0) {
                    double gyroHeading = gyro.getHeading(); //get the gyro heading

                    //if the driver input just dropped to 0
                    if (mode == TeleOpGyroMode.DRIVER) {
                        mode = TeleOpGyroMode.WAIT;
                        driverEndTime = System.currentTimeMillis();
                    }
                    if (mode == TeleOpGyroMode.WAIT) {
                        if (System.currentTimeMillis() - driverEndTime >= DELAY_BEFORE_GYRO_CONTROL) {
                            mode = TeleOpGyroMode.GYRO;
                            //initialize the gyroControl
                            gyroControl = gyro(gyro, Angle.fromDegrees(gyroHeading), maxAngularSpeed);
                        } else {
                            return true;
                        }
                    }
                    //update the gyro control
                    if (!gyroControl.act()) return false;

                    //use the gyro control's output
                    velocityR = -gyroControl.getVelocityR();
                } else {
                    mode = TeleOpGyroMode.DRIVER;
                }
                return true;
            }

            @Override
            public double getVelocityR() {
                return velocityR;
            }

            @Override
            public Angle getPolarDirectionCorrection() {
                //if the gyro is controlling
                if (mode == TeleOpGyroMode.GYRO) {
                    //use the gyro heading to correct for the angle
                    return gyroControl.getPolarDirectionCorrection();
                } else {
                    //otherwise, don't apply a correction to the translation angle
                    return Angle.zero();
                }
            }
        };
    }
}
