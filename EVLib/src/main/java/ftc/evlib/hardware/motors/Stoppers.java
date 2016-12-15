package ftc.evlib.hardware.motors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/13/16
 *
 * Takes care of grouping Stopper objects to stop multiple mechanisms at the same time
 *
 * @see Stopper
 */

public class Stoppers {
    /**
     * Stores all the Stopper objects it is given
     */
    private Set<Stopper> stoppers = new HashSet<>();

    /**
     * create a Stoppers object with no stoppers in it yet
     */
    public Stoppers() {
    }

    /**
     * Create a Stoppers object from an existing collection of Stopper objects
     * @param stoppers the collection of Stopper objects to copy
     */
    public Stoppers(Collection<Stopper> stoppers) {
        this.stoppers.addAll(stoppers);
    }

    public Stoppers(Stoppers stoppers) {
        this.stoppers.addAll(stoppers.stoppers);
    }

    /**
     * Add a Stopper to the list
     *
     * @param stopper the Stopper to add
     */
    public void add(Stopper stopper) {
        stoppers.add(stopper);
    }

    /**
     * Loop through all the stoppers and stop each one
     */
    public void stop() {
        for (Stopper stopper : stoppers) {
            stopper.stop();
        }
    }
}
