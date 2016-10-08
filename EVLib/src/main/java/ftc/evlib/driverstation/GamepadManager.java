package ftc.evlib.driverstation;

import com.qualcomm.robotcore.hardware.Gamepad;

import ftc.electronvolts.util.AnalogInputManager;
import ftc.electronvolts.util.DigitalInputManager;
import ftc.electronvolts.util.InputScaler;
import ftc.electronvolts.util.InputScalers;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 1/9/16
 * <p>
 * Manages edge detection on all the gamepad inputs
 */
public class GamepadManager {
    public final DigitalInputManager a, b, x, y, left_bumper, right_bumper,
            dpad_up, dpad_down, dpad_left, dpad_right,
            left_stick_button, right_stick_button, back, start;

    public final AnalogInputManager left_stick_x, left_stick_y, right_stick_x, right_stick_y,
            left_trigger, right_trigger;


    public GamepadManager(Gamepad gamepad) {
        this(gamepad, InputScalers.none());
    }

    public GamepadManager(Gamepad gamepad, InputScaler inputScaler) {
        a = new DigitalInputManager(GamepadIEFactory.a(gamepad));
        b = new DigitalInputManager(GamepadIEFactory.b(gamepad));
        x = new DigitalInputManager(GamepadIEFactory.x(gamepad));
        y = new DigitalInputManager(GamepadIEFactory.y(gamepad));
        left_bumper = new DigitalInputManager(GamepadIEFactory.left_bumper(gamepad));
        right_bumper = new DigitalInputManager(GamepadIEFactory.right_bumper(gamepad));
        dpad_up = new DigitalInputManager(GamepadIEFactory.dpad_up(gamepad));
        dpad_down = new DigitalInputManager(GamepadIEFactory.dpad_down(gamepad));
        dpad_left = new DigitalInputManager(GamepadIEFactory.dpad_left(gamepad));
        dpad_right = new DigitalInputManager(GamepadIEFactory.dpad_right(gamepad));
        left_stick_button = new DigitalInputManager(GamepadIEFactory.left_stick_button(gamepad));
        right_stick_button = new DigitalInputManager(GamepadIEFactory.right_stick_button(gamepad));
        back = new DigitalInputManager(GamepadIEFactory.back(gamepad));
        start = new DigitalInputManager(GamepadIEFactory.start(gamepad));

        left_stick_x = new AnalogInputManager(GamepadIEFactory.left_stick_x(gamepad), inputScaler);
        left_stick_y = new AnalogInputManager(GamepadIEFactory.left_stick_y(gamepad), inputScaler);
        right_stick_x = new AnalogInputManager(GamepadIEFactory.right_stick_x(gamepad), inputScaler);
        right_stick_y = new AnalogInputManager(GamepadIEFactory.right_stick_y(gamepad), inputScaler);
        left_trigger = new AnalogInputManager(GamepadIEFactory.left_trigger(gamepad), inputScaler);
        right_trigger = new AnalogInputManager(GamepadIEFactory.right_trigger(gamepad), inputScaler);
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


        /*Hardware.getTelem().addData("a", a.isPressed());
        Hardware.getTelem().addData("b", b.isPressed());
        Hardware.getTelem().addData("x", x.isPressed());
        Hardware.getTelem().addData("y", y.isPressed());
        Hardware.getTelem().addData("LB", left_bumper.isPressed());
        Hardware.getTelem().addData("RB", right_bumper.isPressed());
        Hardware.getTelem().addData("DU", dpad_up.isPressed());
        Hardware.getTelem().addData("DD", dpad_down.isPressed());
        Hardware.getTelem().addData("DL", dpad_left.isPressed());
        Hardware.getTelem().addData("DR", dpad_right.isPressed());
        Hardware.getTelem().addData("LSB", left_stick_button.isPressed());
        Hardware.getTelem().addData("RSB", right_stick_button.isPressed());
        Hardware.getTelem().addData("Back", back.isPressed());
        Hardware.getTelem().addData("Start", start.isPressed());

        Hardware.getTelem().addData("Left X", left_stick_x.isPressed());
        Hardware.getTelem().addData("Left Y", left_stick_y.isPressed());
        Hardware.getTelem().addData("Right X", right_stick_x.isPressed());
        Hardware.getTelem().addData("Right Y", right_stick_y.isPressed());
        Hardware.getTelem().addData("Left Trigger", left_trigger.isPressed());
        Hardware.getTelem().addData("Right Trigger", right_trigger.isPressed());
        */
    }
}