package ftc.evlib.opmodes;

import ftc.electronvolts.util.OptionsFile;
import ftc.electronvolts.util.units.Time;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/29/16
 *
 * extends AbstractTeleOp and adds saving and loading an OptionsFile and removes the match timer
 */
public abstract class AbstractOptionsOp extends AbstractTeleOp<RobotCfg> {
    public OptionsFile optionsFile;
    private final String filename;

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
    public Time getMatchTime() {
        return null; //the OptionsOp has no time limit
    }

    /**
     * Load the options from the file
     */
    public void loadOptionsFile() {
        optionsFile = new OptionsFile(FileUtil.getFile(filename));
    }

    /**
     * save the options from the file
     */
    public void saveOptionsFile() {
        optionsFile.writeToFile(FileUtil.getFile(filename));
    }

    @Override
    public void post_act() {
        super.post_act();

        telemetry.addData("* back button => erase changes", "");
//        telemetry.addData("* Hit the \"Back\" button to erase changes since the last save (reload the options file).", "");

        if (driver1.back.justPressed()) {
            loadOptionsFile();
        }

        telemetry.addData("* start button => save", "");
//        telemetry.addData("* Hit \"Start\" to save the options.", "");
        if (driver1.start.justPressed()) {
            saveOptionsFile();
        }

        telemetry.addData("* Stop the opmode to save and quit.", "");
//        telemetry.addData("* Hit \"Stop\" on the driver station to save the options and quit.", "");
    }

    @Override
    protected void setup() {
        loadOptionsFile(); //load the file when the opmode starts
    }

    @Override
    protected void setup_act() {

    }

    @Override
    protected void go() {

    }

    @Override
    protected void end() {
        saveOptionsFile(); //save the file when the opmode ends
    }
}
