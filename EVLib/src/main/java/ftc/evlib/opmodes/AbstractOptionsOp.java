package ftc.evlib.opmodes;

import ftc.electronvolts.util.OptionsFile;
import ftc.evlib.hardware.config.RobotCfg;
import ftc.evlib.util.FileUtil;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/29/16
 */

public abstract class AbstractOptionsOp<Type extends RobotCfg> extends AbstractTeleOp<Type> {
    public OptionsFile optionsFile;
    private final String filename;

    public AbstractOptionsOp(String filename) {
        this.filename = filename;
    }

    @Override
    public int getMatchTime() {
        return -1;
    }

    public void loadOptionsFile() {
        optionsFile = new OptionsFile(FileUtil.getFile(filename));
    }

    public void saveOptionsFile() {
        optionsFile.writeToFile(FileUtil.getFile(filename));
    }

    @Override
    public void post_act() {
        super.post_act();

        telemetry.addData("* Hit the \"Back\" button to erase changes since the last save (reload the options file).", "");

        if (driver1.back.justPressed()) {
            loadOptionsFile();
        }

        telemetry.addData("* Hit \"Start\" to save the options.", "");
        if (driver1.start.justPressed()) {
            saveOptionsFile();
        }

        telemetry.addData("* Hit \"Stop\" on the driver station to save the options and quit.", "");
    }

    @Override
    protected void setup() {
        loadOptionsFile();
    }

    @Override
    protected void end() {
        saveOptionsFile();
    }
}
