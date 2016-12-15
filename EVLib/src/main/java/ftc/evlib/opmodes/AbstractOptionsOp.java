package ftc.evlib.opmodes;

import ftc.electronvolts.util.files.Logger;
import ftc.electronvolts.util.files.OptionsFile;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.util.EVConverters;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/29/16
 *
 * extends AbstractTeleOp and adds saving and loading an OptionsFile and removes the match timer
 *
 * @see AbstractTeleOp
 * @see OptionsFile
 */
public abstract class AbstractOptionsOp extends AbstractTeleOp<RobotCfg> {
    private final String filename;
    public OptionsFile optionsFile;

    /**
     * The filename will be set by the subclasses
     *
     * @param filename the name of the file where the options are stored
     */
    public AbstractOptionsOp(String filename) {
        this.filename = filename;
    }

    /**
     * @return a dummy RobotCfg
     */
    @Override
    protected RobotCfg createRobotCfg() {
        return new RobotCfg(hardwareMap) {
            @Override
            public void act() {

            }

            @Override
            public void stop() {

            }
        };
    }

    @Override
    protected Logger createLogger() {
        //the OptionsOp has no logging
        return null;
    }

    @Override
    public Time getMatchTime() {
        //the OptionsOp has no time limit
        return null;
    }

    /**
     * Load the options from the file
     */
    public void loadOptionsFile() {
        optionsFile = new OptionsFile(EVConverters.getInstance(), FileUtil.getOptionsFile(filename));
    }

    /**
     * save the options from the file
     */
    public void saveOptionsFile() {
        optionsFile.writeToFile(FileUtil.getOptionsFile(filename));
    }

    @Override
    protected void setup() {
        //load the file when the opmode starts
        loadOptionsFile();
    }

    @Override
    protected void setup_act() {

    }

    @Override
    protected void go() {

    }

    @Override
    public void post_act() {
        super.post_act();

        //display telemetry instructions
        telemetry.addData("* back button => erase changes", "");
        //reload the file if the back button is pressed
        if (driver1.back.justPressed()) loadOptionsFile();

        //display telemetry instructions
        telemetry.addData("* start button => save", "");
        //save the file if the start button is pressed
        if (driver1.start.justPressed()) saveOptionsFile();

        telemetry.addData("* Stop the opmode to save and quit.", "");
    }

    @Override
    protected void end() {
        //save the file when the opmode ends
        saveOptionsFile();
    }
}
