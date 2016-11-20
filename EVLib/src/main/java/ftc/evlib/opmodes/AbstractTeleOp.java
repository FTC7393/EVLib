package ftc.evlib.opmodes;

import ftc.electronvolts.util.Function;
import ftc.electronvolts.util.Functions;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.driverstation.GamepadManager;
import ftc.evlib.hardware.config.RobotCfg;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * extends AbstractOp and adds gamepad edge detection and scaling, and a 2 minute timer
 *
 * @see AbstractOp
 * @see GamepadManager
 */
public abstract class AbstractTeleOp<Type extends RobotCfg> extends AbstractOp<Type> {
    public GamepadManager driver1;
    public GamepadManager driver2;

    /**
     * This is implemented by the teleop opmode
     *
     * @return a Function to scale the joysticks by
     */
    protected abstract Function getJoystickScalingFunction();

    @Override
    public Time getMatchTime() {
        return Time.fromMinutes(2); //teleop is 2 minutes
    }

    @Override
    public void start() {
        //set the joystick deadzone
//        gamepad1.setJoystickDeadzone(.1F);
//        gamepad2.setJoystickDeadzone(.1F);

        //get the scaling function
        Function f = getJoystickScalingFunction();
        if (f == null) {
            //if it is null set it to none
            f = Functions.none();
        }

        //apply the function to the gamepads and store them
        driver1 = new GamepadManager(gamepad1, f);
        driver2 = new GamepadManager(gamepad2, f);
        super.start();
    }

    @Override
    public void pre_act() {
        //update the joystick values
        driver1.update();
        driver2.update();
    }

    @Override
    public void post_act() {

    }
}
