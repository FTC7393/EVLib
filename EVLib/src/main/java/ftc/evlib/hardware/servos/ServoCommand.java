package ftc.evlib.hardware.servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 3/2/16
 * <p>
 * stores a command to send to a ServoControl
 */
public class ServoCommand {
    private final ServoControl servo;
    private final double speed;
    private final double position;

    /**
     * position with speed
     *
     * @param servo    the servo to move
     * @param position the position to move to
     * @param speed    the speed to move at
     */
    public ServoCommand(ServoControl servo, double position, double speed) {
        this.servo = servo;
        this.position = position;
        this.speed = speed;
    }

    /**
     * position with max speed
     *
     * @param servo    the servo to move
     * @param position the position to move to
     */
    public ServoCommand(ServoControl servo, double position) {
        this.servo = servo;
        this.position = position;
        this.speed = ServoControl.MAX_SPEED; //go as fast as possible
    }

    /**
     * preset with speed
     *
     * @param servoPreset the preset to move to
     * @param servo       the servo to move
     * @param speed       the speed to move at
     */
    public ServoCommand(Enum servoPreset, ServoControl servo, double speed) {
        this.servo = servo;
        this.position = servo.getPresets().get(servoPreset);
        this.speed = speed;
    }

    /**
     * preset with max speed
     *
     * @param servoPreset the preset to move to
     * @param servo       the servo to move
     */
    public ServoCommand(Enum servoPreset, ServoControl servo) {
        this.servo = servo;
        this.position = servo.getPresets().get(servoPreset);
        this.speed = ServoControl.MAX_SPEED; //go as fast as possible
    }

    public ServoControl getServo() {
        return servo;
    }

    public double getSpeed() {
        return speed;
    }

    public double getPosition() {
        return position;
    }
}
