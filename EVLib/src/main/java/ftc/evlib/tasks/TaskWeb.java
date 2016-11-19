package ftc.evlib.tasks;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ftc.electronvolts.statemachine.StateMachineBuilder;
import ftc.electronvolts.statemachine.StateName;
import ftc.electronvolts.statemachine.States;
import ftc.electronvolts.util.TeamColor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/7/16
 */

public abstract class TaskWeb<T extends Task, B extends StateMachineBuilder> {
    private final StateName startState, successState, failState, timeoutState;

    public TeamColor allianceColor;
    public final B builder;

    public abstract B createBuilder(StateName startState);

    public TaskWeb(TeamColor allianceColor, StateName start, StateName success, StateName fail, StateName timeout, B builder) {
        this.allianceColor = allianceColor;
        this.startState = start;
        this.successState = success;
        this.failState = fail;
        this.timeoutState = timeout;
        this.builder = builder;
//        builder = createBuilder(startState);
    }

    public void setAllianceColor(TeamColor allianceColor) {
        this.allianceColor = allianceColor;
    }

    public TeamColor getAllianceColor() {
        return allianceColor;
    }

    public void addTask(T task, StateName stateName, StateName successState, StateName failState, StateName timeoutState) {
        B b = createBuilder(startState);

        //add sub-states to the builder
        addTaskSubStates(task, b);

        //link ending sub-states to main state machine
        Map<StateName, StateName> subStateToState = new HashMap<>();
        subStateToState.put(this.successState, successState);
        subStateToState.put(this.failState, failState);
        subStateToState.put(this.timeoutState, timeoutState);

        builder.add(States.subStates(stateName, subStateToState, builder));
    }

    /**
     * Add the states associated with a task to an StateMachineBuilder
     *
     * @param task the task to add
     * @param b    the builder to add it to
     * @return the amount of time in milliseconds it will take. Returns -1 if it cannot be done, 0 if it is instantaneous
     */
    public abstract int addTaskSubStates(T task, B b);

    public void addManeuver(T from, T to, StateName stateName, StateName successState, StateName failState) {
        B b = createBuilder(startState);

        //add sub-states to the builder
        addManeuverSubStates(from, to, b);

        //link ending sub-states to main state machine
        Map<StateName, StateName> subStateToState = new HashMap<>();
        subStateToState.put(this.successState, successState);
        subStateToState.put(this.failState, failState);

        builder.add(States.subStates(stateName, subStateToState, builder));
    }


    /**
     * Add the states associated with a maneuver to an StateMachineBuilder
     *
     * @param from the task to maneuver from
     * @param to   the task to maneuver to
     * @param b    the builder to add the maneuver to
     * @return the amount of time in milliseconds it will take. Returns -1 if it cannot be done, 0 if it is instantaneous
     */
    public abstract int addManeuverSubStates(T from, T to, B b);

    public void addStages(List<Stage<T>> stages) {
        for (int i = 0; i < stages.size(); i++) {
            Stage<T> stage = stages.get(i);

            T currentTask = stage.getTask();
            long waitTime = stage.getWaitTime();

            StateName waitState = stage.getWaitState();
            StateName taskState = stage.getTaskState();
            StateName maneuverState = stage.getManeuverState();

            StateName taskFailState = stage.getTaskFailState();
            StateName taskTimeoutState = stage.getTaskTimeoutState();
            StateName maneuverFailState = stage.getManeuverFailState();

            builder.addWait(waitState, taskState, waitTime);
            addTask(currentTask, taskState, maneuverState, taskFailState, taskTimeoutState);

            if (i < stages.size() - 1) {
                Stage<T> nextStage = stages.get(i + 1);
                T nextTask = nextStage.getTask();
                StateName nextWaitState = nextStage.getWaitState();
                addManeuver(currentTask, nextTask, maneuverState, nextWaitState, maneuverFailState);
            }
        }
    }


    public static void notImplemented(Task from, Task to) {
        notImplemented("Path between " + from + " and " + to);
    }

    public static void notImplemented(Task task) {
        notImplemented("Task " + task);
    }

    public static void notImplemented(String message) {
        message += " is not written yet.";
        Log.e("Tasks", message);
//        throw new UnsupportedOperationException(message);
    }


    public static void notPossible(Task from, Task to) {
        notPossible("Path between " + from + " and " + to);
    }

    public static void notPossible(Task task) {
        notPossible("Task " + task);
    }

    public static void notPossible(String message) {
        message += " is not possible (or it is not planned to be implemented).";
        Log.e("Tasks", message);
//        throw new UnsupportedOperationException(message);
    }
}
