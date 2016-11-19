package ftc.evlib.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import ftc.electronvolts.util.MatchTimer;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.driverstation.Telem;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.Servos;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * adds useful features to the OpMode such as a MatchTimer, servo management, use of RobotCfg
 */
public abstract class AbstractOp<Type extends RobotCfg> extends OpMode {
    public MatchTimer matchTimer; // keeps track of the time left in the match
    public Servos servos; // coordinates all the servos to act with one command
    public Type robotCfg; // stores all the motors, servos, and sensors

    /**
     * This is implemented by the opmode to initialize the hardware
     *
     * @return a RobotCfg of the type specified by your opmode
     */
    protected abstract Type createRobotCfg();

    /**
     * This is implemented by the opmode
     * It is called when the init button is pressed on the driver station
     */
    protected abstract void setup();

    /**
     * This is implemented by the opmode
     * It is called continuously between the setup() and go() methods
     */
    protected abstract void setup_act();

    /**
     * This is implemented by the opmode
     * It is called when the start button on the driver station is pressed
     */
    protected abstract void go();

    /**
     * This is implemented by AbstractTeleOp to update the joysticks
     * It is called right before the act() method
     */
    protected abstract void pre_act();

    /**
     * This is implemented by the opmode
     * It is called between the go() and stop() methods
     */
    protected abstract void act();

    /**
     * This is implemented by AbstractAutoOp to run the StateMachine
     * It is called right after the act() method
     */
    protected abstract void post_act();

    /**
     * This is implemented by the opmode
     * It is called when the stop button is pressed on the driver station
     */
    protected abstract void end();

    /**
     * This is implemented by AbstractAutoOp and AbstractTeleOp to set the match time for autonomous and teleop
     *
     * @return The length of the match
     */
    protected abstract Time getMatchTime();

    @Override
    public void init() {
        Telem.telemetry = telemetry; //store the telemetry in a global location
        matchTimer = new MatchTimer(getMatchTime()); //create the MatchTimer
        robotCfg = createRobotCfg(); //ask the OpMode for the HardwareCfg object
        servos = robotCfg.getServos(); //create the servos object

        setup();
    }

    @Override
    public void init_loop() {
        setup_act();
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
        servos.servosAct(); //update the servo positions
    }

    @Override
    public void stop() {
        robotCfg.stop();
        servos.servosAct(); //update the servos so they will stop

        end();
    }

}
