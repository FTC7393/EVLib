package ftc.evlib.tasks;

import ftc.electronvolts.statemachine.StateName;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/30/16
 */

public class Stage<T extends Task> {
    private final long waitTime;
    private final StateName waitState;
    private final T task;
    private final StateName taskState;
    private final StateName taskFailState;
    private final StateName taskTimeoutState;
    private final StateName maneuverState;
    private final StateName maneuverFailState;

    public Stage(long waitTime, StateName waitState, T task, StateName taskState, StateName taskFailState, StateName taskTimeoutState, StateName maneuverState, StateName maneuverFailState) {
        this.waitTime = waitTime;
        this.waitState = waitState;
        this.task = task;
        this.taskState = taskState;
        this.taskFailState = taskFailState;
        this.taskTimeoutState = taskTimeoutState;
        this.maneuverState = maneuverState;
        this.maneuverFailState = maneuverFailState;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public StateName getWaitState() {
        return waitState;
    }

    public T getTask() {
        return task;
    }

    public StateName getTaskState() {
        return taskState;
    }

    public StateName getTaskFailState() {
        return taskFailState;
    }

    public StateName getTaskTimeoutState() {
        return taskTimeoutState;
    }

    public StateName getManeuverState() {
        return maneuverState;
    }

    public StateName getManeuverFailState() {
        return maneuverFailState;
    }
}
