package ftc.evlib.opmodes;

import ftc.electronvolts.statemachine.StateMachine;
import ftc.evlib.hardware.config.RobotCfg;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/13/16
 */
public abstract class AbstractAutoOp<Type extends RobotCfg> extends AbstractOp<Type> {
    private StateMachine stateMachine;

    public abstract StateMachine buildStates();

    @Override
    public int getMatchTime() {
        return 30 * 1000;
    }

    @Override
    public void setup() {
        stateMachine = buildStates();
    }

    @Override
    public void pre_act() {

    }

    @Override
    public void post_act() {
        stateMachine.act();
    }
}
