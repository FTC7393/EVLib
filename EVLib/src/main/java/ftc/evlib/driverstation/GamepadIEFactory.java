package ftc.evlib.driverstation;

import com.qualcomm.robotcore.hardware.Gamepad;

import ftc.electronvolts.util.InputExtractor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 1/9/16
 * <p>
 * Factory class for gamepad InputExtractors
 * each method extracts one input from the gamepad
 *
 * @see InputExtractor
 * @see GamepadManager
 */
public class GamepadIEFactory {

    public static InputExtractor<Boolean> a(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.a;
            }
        };
    }

    public static InputExtractor<Boolean> b(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.b;
            }
        };
    }

    public static InputExtractor<Boolean> x(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.x;
            }
        };
    }

    public static InputExtractor<Boolean> y(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.y;
            }
        };
    }

    public static InputExtractor<Boolean> left_bumper(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.left_bumper;
            }
        };
    }

    public static InputExtractor<Boolean> right_bumper(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.right_bumper;
            }
        };
    }

    public static InputExtractor<Boolean> dpad_up(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.dpad_up;
            }
        };
    }

    public static InputExtractor<Boolean> dpad_down(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.dpad_down;
            }
        };
    }

    public static InputExtractor<Boolean> dpad_left(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.dpad_left;
            }
        };
    }

    public static InputExtractor<Boolean> dpad_right(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.dpad_right;
            }
        };
    }

    public static InputExtractor<Boolean> left_stick_button(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.left_stick_button;
            }
        };
    }

    public static InputExtractor<Boolean> right_stick_button(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.right_stick_button;
            }
        };
    }

    public static InputExtractor<Boolean> back(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.back;
            }
        };
    }

    public static InputExtractor<Boolean> start(final Gamepad gamepad) {
        return new InputExtractor<Boolean>() {
            @Override
            public Boolean getValue() {
                return gamepad.start;
            }
        };
    }

    public static InputExtractor<Double> left_stick_x(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.left_stick_x;
            }
        };
    }

    public static InputExtractor<Double> left_stick_y(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.left_stick_y;
            }
        };
    }

    public static InputExtractor<Double> right_stick_x(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.right_stick_x;
            }
        };
    }

    public static InputExtractor<Double> right_stick_y(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.right_stick_y;
            }
        };
    }

    public static InputExtractor<Double> left_trigger(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.left_trigger;
            }
        };
    }

    public static InputExtractor<Double> right_trigger(final Gamepad gamepad) {
        return new InputExtractor<Double>() {
            @Override
            public Double getValue() {
                return (double) gamepad.right_trigger;
            }
        };
    }
}
