package ftc.evlib.driverstation;

import com.qualcomm.robotcore.hardware.Gamepad;

import ftc.electronvolts.util.AnalogInputScaler;
import ftc.electronvolts.util.DigitalInputEdgeDetector;
import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 1/9/16
 * <p>
 * Manages edge detection on all the gamepad inputs
 */
public class GamepadManager {
    public final DigitalInputEdgeDetector a, b, x, y, left_bumper, right_bumper,
            dpad_up, dpad_down, dpad_left, dpad_right,
            left_stick_button, right_stick_button, back, start;

    public final AnalogInputScaler left_stick_x, left_stick_y, right_stick_x, right_stick_y,
            left_trigger, right_trigger;


    public GamepadManager(Gamepad gamepad) {
        this(gamepad, Functions.none());
    }

    public GamepadManager(Gamepad gamepad, Function inputScaler) {
        a = new DigitalInputEdgeDetector(GamepadIEFactory.a(gamepad));
        b = new DigitalInputEdgeDetector(GamepadIEFactory.b(gamepad));
        x = new DigitalInputEdgeDetector(GamepadIEFactory.x(gamepad));
        y = new DigitalInputEdgeDetector(GamepadIEFactory.y(gamepad));
        left_bumper = new DigitalInputEdgeDetector(GamepadIEFactory.left_bumper(gamepad));
        right_bumper = new DigitalInputEdgeDetector(GamepadIEFactory.right_bumper(gamepad));
        dpad_up = new DigitalInputEdgeDetector(GamepadIEFactory.dpad_up(gamepad));
        dpad_down = new DigitalInputEdgeDetector(GamepadIEFactory.dpad_down(gamepad));
        dpad_left = new DigitalInputEdgeDetector(GamepadIEFactory.dpad_left(gamepad));
        dpad_right = new DigitalInputEdgeDetector(GamepadIEFactory.dpad_right(gamepad));
        left_stick_button = new DigitalInputEdgeDetector(GamepadIEFactory.left_stick_button(gamepad));
        right_stick_button = new DigitalInputEdgeDetector(GamepadIEFactory.right_stick_button(gamepad));
        back = new DigitalInputEdgeDetector(GamepadIEFactory.back(gamepad));
        start = new DigitalInputEdgeDetector(GamepadIEFactory.start(gamepad));

        left_stick_x = new AnalogInputScaler(GamepadIEFactory.left_stick_x(gamepad), inputScaler);
        left_stick_y = new AnalogInputScaler(GamepadIEFactory.left_stick_y(gamepad), inputScaler);
        right_stick_x = new AnalogInputScaler(GamepadIEFactory.right_stick_x(gamepad), inputScaler);
        right_stick_y = new AnalogInputScaler(GamepadIEFactory.right_stick_y(gamepad), inputScaler);
        left_trigger = new AnalogInputScaler(GamepadIEFactory.left_trigger(gamepad), inputScaler);
        right_trigger = new AnalogInputScaler(GamepadIEFactory.right_trigger(gamepad), inputScaler);
    }

    public void update() {
        a.update();
        b.update();
        x.update();
        y.update();

        left_bumper.update();
        right_bumper.update();

        left_trigger.update();
        right_trigger.update();

        dpad_up.update();
        dpad_down.update();
        dpad_left.update();
        dpad_right.update();

        left_stick_button.update();
        right_stick_button.update();

        back.update();
        start.update();

        left_stick_x.update();
        left_stick_y.update();

        right_stick_x.update();
        right_stick_y.update();


        /*telemetry.addData("a", a.isPressed());
        telemetry.addData("b", b.isPressed());
        telemetry.addData("x", x.isPressed());
        telemetry.addData("y", y.isPressed());
        telemetry.addData("LB", left_bumper.isPressed());
        telemetry.addData("RB", right_bumper.isPressed());
        telemetry.addData("DU", dpad_up.isPressed());
        telemetry.addData("DD", dpad_down.isPressed());
        telemetry.addData("DL", dpad_left.isPressed());
        telemetry.addData("DR", dpad_right.isPressed());
        telemetry.addData("LSB", left_stick_button.isPressed());
        telemetry.addData("RSB", right_stick_button.isPressed());
        telemetry.addData("Back", back.isPressed());
        telemetry.addData("Start", start.isPressed());

        telemetry.addData("Left X", left_stick_x.isPressed());
        telemetry.addData("Left Y", left_stick_y.isPressed());
        telemetry.addData("Right X", right_stick_x.isPressed());
        telemetry.addData("Right Y", right_stick_y.isPressed());
        telemetry.addData("Left Trigger", left_trigger.isPressed());
        telemetry.addData("Right Trigger", right_trigger.isPressed());
        */
    }
}