package ftc.evlib.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import ftc.electronvolts.util.MatchTimer;
import ftc.electronvolts.util.files.Logger;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.driverstation.Telem;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.hardware.servos.Servos;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/12/16
 *
 * adds useful features to the OpMode such as a MatchTimer, servo management, use of RobotCfg
 */
public abstract class AbstractOp<Type extends RobotCfg> extends OpMode {
    /**
     * keeps track of the time left in the match
     */
    public MatchTimer matchTimer;

    /**
     * coordinates all the servos to act with one command
     */
    public Servos servos;

    /**
     * logs values to a file
     */
    private Logger logger;

    /**
     * stores all the motors, servos, and sensors
     */
    public Type robotCfg;


    /**
     * This is implemented by the opmode to initialize the hardware
     *
     * @return a RobotCfg of the type specified by your opmode
     */
    protected abstract Type createRobotCfg();

    /**
     * This is implemented by the opmode to log values
     *
     * @return a Logger that has been configured return null for no logging
     */
    protected abstract Logger createLogger();

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
     *
     * @see AbstractTeleOp
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
     *
     * @see AbstractAutoOp
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

        robotCfg = createRobotCfg(); //ask the OpMode for the HardwareCfg object
        servos = robotCfg.getServos(); //create the servos object

        matchTimer = new MatchTimer(getMatchTime()); //create the MatchTimer
        logger = createLogger(); //create the logger

        setup();
    }

    @Override
    public void init_loop() {
        setup_act();
    }

    @Override
    public void start() {
        matchTimer.start(); //start the match

        if (logger != null) logger.start(FileUtil.getLogsDir()); // start the logging

        go();
    }

    @Override
    public void loop() {
        double deltaTime = matchTimer.update(); //update the delta time since the last loop
        telemetry.addData("Delta Time: ", deltaTime);

        //stop the robot when the match ends
        if (matchTimer.isMatchJustOver()) stop();
        if (matchTimer.isMatchOver()) return;

        long timeLeft = matchTimer.getTimeLeft();
        if (timeLeft > 0) {
            telemetry.addData("Time left", timeLeft / 1000.0);
        }

        pre_act();

        if (logger != null) logger.act();

        act();
        post_act();

        robotCfg.act();
        servos.servosAct(); //update the servo positions
    }

    @Override
    public void stop() {
        robotCfg.stop();
        robotCfg.getStoppers().stop();
        servos.servosAct(); //update the servos so they will stop
        if (logger != null) logger.stop();

        end();
    }

}
