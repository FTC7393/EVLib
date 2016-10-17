package ftc.evlib.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import ftc.electronvolts.util.MatchTimer;
import ftc.evlib.driverstation.Telem;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.Servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * adds useful features to the OpMode such as a MatchTimer, servo management, use of HardwareCfg
 */
public abstract class AbstractOp<Type extends RobotCfg> extends OpMode {
    public MatchTimer matchTimer;
    public Servos servos;
    public Type robotCfg;

    protected abstract Type createHardwareCfg();

    protected abstract void setup();

    protected abstract void setup_loop();

    protected abstract void go();

    protected abstract void pre_act();

    protected abstract void act();

    protected abstract void post_act();

    protected abstract void end();

    protected abstract int getMatchTime();

    @Override
    public void init() {
        Telem.telemetry = telemetry; //store the telemetry in a global location
        matchTimer = new MatchTimer(getMatchTime()); //create the MatchTimer
        robotCfg = createHardwareCfg(); //ask the OpMode for the HardwareCfg object
        servos = robotCfg.getServos(); //create the servos object

        setup();
    }

    @Override
    public void init_loop() {
        setup_loop();
    }

    @Override
    public void start() {
        matchTimer.start(); //start the match

        go();
    }

    @Override
    public void loop() {
        double deltaTime = matchTimer.update(); //update the delta time since the last loop
        telemetry.addData("Delta Time: ", deltaTime);

        //stop the robot when the match ends
        if (matchTimer.isMatchJustOver()) stop();
        if (matchTimer.isMatchOver()) return;

        telemetry.addData("Time left", matchTimer.getTimeLeft() / 1000.0);

        pre_act();
        act();
        post_act();

        robotCfg.act();
        servos.servosAct();
    }

    @Override
    public void stop() {
        servos.servosAct();

        end();
    }

}
