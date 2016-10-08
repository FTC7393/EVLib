package ftc.evlib.opmodes;

import ftc.electronvolts.util.InputScaler;
import ftc.evlib.driverstation.GamepadManager;
import ftc.evlib.hardware.config.RobotCfg;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 * <p>
 * adds gamepad management to the OpMode
 */
public abstract class AbstractTeleOp<Type extends RobotCfg> extends AbstractOp<Type> {
    public GamepadManager driver1;
    public GamepadManager driver2;

    protected abstract InputScaler getJoystickInputScaler();

    @Override
    public int getMatchTime() {
//        return 60 * 1000;
        return 2 * 60 * 1000;
    }

    @Override
    public void start() {
        gamepad1.setJoystickDeadzone(.1F);
        gamepad2.setJoystickDeadzone(.1F);
        //store the gamepads
        driver1 = new GamepadManager(gamepad1, getJoystickInputScaler());
        driver2 = new GamepadManager(gamepad2, getJoystickInputScaler());
        super.start();
    }

    @Override
    public void pre_act() {
        driver1.update();
        driver2.update();
    }

    @Override
    public void post_act() {

    }
}
