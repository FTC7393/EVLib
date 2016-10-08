package ftc.evlib.hardware.motors;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;

import ftc.electronvolts.util.Utility;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/11/16
 * <p>
 * Factory class for creating Motor wrapper classes.
 * Has methods for all the combinations of with/without encoders and forward/reversed.
 */
public class Motors {
    public static Motor combined(boolean hasEncoder, Motor motor1, Motor motor2) {
        return combined(hasEncoder, ImmutableList.of(motor1, motor2));
    }

    public static Motor combined(boolean hasEncoder, List<Motor> motors) {
        if (hasEncoder) {
            List<MotorEnc> motorEncs = new ArrayList<>();
            for (Motor motor : motors) {
                motorEncs.add((MotorEnc) motor);
            }
            return combinedWithEncoder(motorEncs);
        } else {
            return combinedWithoutEncoder(motors);
        }
    }

    public static Motor motor(boolean hasEncoder, DcMotor motor, boolean reversed) {
        if (hasEncoder) {
            return motorWithEncoder(motor, reversed);
        } else {
            return motorWithoutEncoder(motor, reversed);
        }
    }

    /**
     * combine two motors with encoders into one motor
     *
     * @param motor1 the first motor (with encoder)
     * @param motor2 the second motor (with encoder)
     * @return the motor that controls both (with encoder support)
     */
    public static MotorEnc combinedWithEncoder(MotorEnc motor1, MotorEnc motor2) {
        return combinedWithEncoder(ImmutableList.of(motor1, motor2));
    }

    /**
     * combines any number of motors with encoders into one
     *
     * @param motors the list of motors to combine (all must have encoders)
     * @return the motor that controls all of them (with encoder support)
     */
    public static MotorEnc combinedWithEncoder(final List<MotorEnc> motors) {
        return new MotorEnc() {
            @Override
            public void setSpeed(double speed) {
                for (MotorEnc motor : motors) motor.setSpeed(speed);
            }

            @Override
            public void setPosition(int encoderPosition) {
                for (MotorEnc motor : motors) motor.setPosition(encoderPosition);
            }

            @Override
            public void resetEncoder() {
                for (MotorEnc motor : motors) motor.resetEncoder();
            }

            @Override
            public int getEncoderPosition() {
                int total = 0;
                for (MotorEnc motor : motors) total += motor.getEncoderPosition();
                if (motors.size() == 0) {
                    return 0;
                } else {
                    return total / motors.size();
                }
            }

            @Override
            public void setPower(double power) {
                for (MotorEnc motor : motors) motor.setPower(power);
            }

            @Override
            public MotorMode getMode() {
                if (motors.size() == 0) {
                    return MotorMode.POWER;
                } else {
                    return motors.get(0).getMode();
                }
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {
                for (MotorEnc motor : motors) motor.setStopBehavior(stopBehavior);
            }
        };
    }

    /**
     * combine two motors with or without encoders into one motor
     *
     * @param motor1 the first motor
     * @param motor2 the second motor
     * @return the motor that controls both (without encoder support)
     */
    public static Motor combinedWithoutEncoder(Motor motor1, Motor motor2) {
        return combinedWithoutEncoder(ImmutableList.of(motor1, motor2));
    }

    /**
     * combines any number of motors with or without encoders into one
     *
     * @param motors the list of motors to combine
     * @return the motor that controls all of them (without encoder support)
     */
    public static Motor combinedWithoutEncoder(final List<Motor> motors) {
        return new Motor() {
            @Override
            public void setPower(double power) {
                for (Motor motor : motors) motor.setPower(power);
            }

            @Override
            public MotorMode getMode() {
                return MotorMode.POWER;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {
                for (Motor motor : motors) motor.setStopBehavior(stopBehavior);
            }
        };
    }

    /**
     * Wrap a DcMotor with no encoder and have it run forwards
     *
     * @param motor the DcMotor to be wrapped
     * @return the Motor wrapper class
     */
    public static Motor motorWithoutEncoderForward(final DcMotor motor) {
        return motorWithoutEncoder(motor, false);
    }

    /**
     * Wrap a DcMotor with no encoder and have it run backwards
     *
     * @param motor the DcMotor to be wrapped
     * @return the Motor wrapper class
     */
    public static Motor motorWithoutEncoderReversed(final DcMotor motor) {
        return motorWithoutEncoder(motor, true);
    }

    /**
     * A basic implementation of the Motor interface
     *
     * @param motor    the DcMotor to be wrapped
     * @param reversed true if the motor's direction should be reversed
     * @return the Motor wrapper class
     */
    public static Motor motorWithoutEncoder(final DcMotor motor, boolean reversed) {
        if (reversed) {
            motor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            motor.setDirection(DcMotor.Direction.FORWARD);
        }

        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        return new Motor() {
            @Override
            public void setPower(double power) {
                motor.setPower(Utility.motorLimit(power));
            }

            @Override
            public MotorMode getMode() {
                return MotorMode.POWER;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {
                if (stopBehavior == StopBehavior.BRAKE) {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                } else {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
            }
        };
    }

    /**
     * Wrap a DcMotor with an encoder and have it run forwards
     *
     * @param motor the DcMotor to be wrapped
     * @return the MotorEnc wrapper class
     */
    public static MotorEnc motorWithEncoderForward(final DcMotor motor) {
        return motorWithEncoder(motor, false);
    }

    /**
     * Wrap a DcMotor with an encoder and have it run backwards
     *
     * @param motor the DcMotor to be wrapped
     * @return the MotorEnc wrapper class
     */
    public static MotorEnc motorWithEncoderReversed(final DcMotor motor) {
        return motorWithEncoder(motor, true);
    }

    /**
     * A basic implementation of the MotorEnc interface
     *
     * @param motor    the DcMotor to be wrapped
     * @param reversed true if the motor's direction should be reversed
     * @return the MotorEnc wrapper class
     */
    public static MotorEnc motorWithEncoder(final DcMotor motor, boolean reversed) {
        if (reversed) {
            motor.setDirection(DcMotor.Direction.REVERSE);
        } else {
            motor.setDirection(DcMotor.Direction.FORWARD);
        }

        return new MotorEnc() {
            private int encoderZero = 0;
            private MotorMode mode = MotorMode.POWER;

            @Override
            public void setPower(double power) {
                mode = MotorMode.POWER;
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor.setPower(Utility.motorLimit(power));
            }

            @Override
            public void setSpeed(double speed) {
                mode = MotorMode.SPEED;
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.setPower(Utility.motorLimit(speed));
            }

            @Override
            public void setPosition(int encoderPosition) {
                mode = MotorMode.POSITION;
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setTargetPosition(encoderPosition);
            }

            @Override
            public void resetEncoder() {
                encoderZero = motor.getCurrentPosition();
            }

            @Override
            public int getEncoderPosition() {
                return motor.getCurrentPosition() - encoderZero;
            }

            @Override
            public MotorMode getMode() {
                return mode;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {
                if (stopBehavior == StopBehavior.BRAKE) {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                } else {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
            }
        };
    }

    /**
     * Wrap a continuous rotation servo as a normal motor and have it run forwards
     *
     * @param servo the Servo to be wrapped
     * @return the Motor wrapper class
     */
    public static Motor continuousServoForward(final Servo servo) {
        return continuousServo(servo, false);
    }

    /**
     * Wrap a continuous rotation servo as a normal motor and have it run backwards
     *
     * @param servo the Servo to be wrapped
     * @return the Motor wrapper class
     */
    public static Motor continuousServoReversed(final Servo servo) {
        return continuousServo(servo, true);
    }

    /**
     * Wraps a continuous rotation servo as a normal motor
     *
     * @param servo    the servo to be wrapped as a motor
     * @param reversed true if the servo should be reversed
     * @return the Motor wrapper class
     */
    public static Motor continuousServo(final Servo servo, boolean reversed) {
        if (reversed) {
            servo.setDirection(Servo.Direction.REVERSE);
        } else {
            servo.setDirection(Servo.Direction.FORWARD);
        }
        return new Motor() {
            @Override
            public void setPower(double power) {
                servo.setPosition(Utility.servoLimit(power * 0.5 + 0.5));
            }

            @Override
            public MotorMode getMode() {
                return MotorMode.POWER;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {

            }
        };
    }

    /**
     * @return a motor that does nothing
     */
    public static MotorEnc dummyMotorEnc() {
        return new MotorEnc() {
            private MotorMode mode = MotorMode.POWER;

            @Override
            public void setSpeed(double speed) {
                mode = MotorMode.SPEED;
            }

            @Override
            public void setPosition(int encoderPosition) {
                mode = MotorMode.POSITION;
            }

            @Override
            public void resetEncoder() {

            }

            @Override
            public int getEncoderPosition() {
                return 0;
            }

            @Override
            public void setPower(double power) {
                mode = MotorMode.POWER;
            }

            @Override
            public MotorMode getMode() {
                return mode;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {

            }
        };
    }

    /**
     * @return a motor that does nothing
     */
    public static Motor dummyMotor() {
        return new Motor() {
            @Override
            public void setPower(double power) {

            }

            @Override
            public MotorMode getMode() {
                return MotorMode.POWER;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {

            }
        };
    }

    public static MotorEnc simulatedNeveRest20() {
        return simulatedNeveRest(20);
    }

    public static MotorEnc simulatedNeveRest40() {
        return simulatedNeveRest(40);
    }

    public static MotorEnc simulatedNeveRest60() {
        return simulatedNeveRest(60);
    }

    public static MotorEnc simulatedNeveRest(double gearRatio) {
        return simulatedMotor(6400 / gearRatio, 7 * gearRatio);
    }

    /**
     * Simulate a motor with an encoder by calculating the encoder position as the integral of the motor speed
     *
     * @param revolutionsPerMinute      motor speed in rpm
     * @param encoderTicksPerRevolution encoder ticks per turn
     * @return the simulated motor
     */
    public static MotorEnc simulatedMotor(final double revolutionsPerMinute, final double encoderTicksPerRevolution) {
        return new MotorEnc() {
            private int encoderTicks = 0, targetEncoderPosition = 0;
            private double power = 0, targetPower = 0, powerError = 0;
            private MotorMode mode = MotorMode.POWER;
            private StopBehavior stopBehavior = StopBehavior.BRAKE;
            private long lastTime = System.currentTimeMillis();

            private void setPower1(double power) {
                targetPower = Utility.motorLimit(power);
                powerError = targetPower - this.power;
            }

            @Override
            public void setSpeed(double speed) {
                update();
                mode = MotorMode.SPEED;
                setPower1(speed);
            }

            @Override
            public void setPosition(int encoderPosition) {
                update();
                mode = MotorMode.POSITION;
                targetEncoderPosition = encoderPosition;
                setPower1(Math.signum(targetEncoderPosition - encoderTicks));
            }

            @Override
            public void resetEncoder() {
                update();
                encoderTicks = 0;
            }

            @Override
            public int getEncoderPosition() {
                update();
                return encoderTicks;
            }

            @Override
            public void setPower(double power) {
                update();
                mode = MotorMode.POWER;
                setPower1(power);
            }

            @Override
            public MotorMode getMode() {
                update();
                return mode;
            }

            @Override
            public void setStopBehavior(StopBehavior stopBehavior) {
                update();
                this.stopBehavior = stopBehavior;
            }

            private static final double MILLISECOND_PER_MIN = 60 * 1000;
            private static final double K = -0.01; //related to the moment of inertia of the motor

            private void update() {
//                long now = System.currentTimeMillis();
//                long deltaTime = lastTime - now;
//
//                double maxTicks = deltaTime / MILLISECOND_PER_MIN * revolutionsPerMinute * encoderTicksPerRevolution;
//
//                if (mode == MotorMode.POSITION) {
//                    int encoderError = targetEncoderPosition - encoderTicks;
//                    if (maxTicks >= Math.abs(encoderError)) {
//                        if (maxTicks == 0) {
//                            targetPower = 0;
//                        } else {
//                            targetPower = encoderError / maxTicks;
//                        }
//                    } else {
//                        targetPower = Math.signum(encoderError);
//                    }
//                }
//                power = targetPower;
//                encoderTicks += power * maxTicks;
//
//                lastTime = now;

                long now = System.currentTimeMillis();
                double deltaTimeMin = (lastTime - now) / MILLISECOND_PER_MIN;

                powerError *= Math.exp(deltaTimeMin * K);
                power = targetPower - powerError;

                double maxTicks = (deltaTimeMin * targetPower - powerError) * revolutionsPerMinute * encoderTicksPerRevolution;

                if (mode == MotorMode.POSITION) {
                    int encoderError = targetEncoderPosition - encoderTicks;
                    if (maxTicks >= encoderError) {
                        power = 0;
                        encoderTicks += encoderError;
                    } else {
                        encoderTicks += maxTicks;
                    }
                } else {
                    encoderTicks += maxTicks;
                }

                lastTime = now;
            }
        };
    }
}
