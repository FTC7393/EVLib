package ftc.evlib.hardware.control;

import ftc.electronvolts.util.Vector2D;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 9/19/16
 * <p>
 * Controls the translation of a mecanum robot
 */
public interface TranslationControl {
    /**
     * update the velocity x and velocity y values
     *
     * @return true if there were no problems
     */
    boolean act();

    /**
     * @return the translational velocity
     */
    Vector2D getTranslation();
}
