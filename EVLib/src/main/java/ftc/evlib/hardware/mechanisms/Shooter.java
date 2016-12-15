package ftc.evlib.hardware.mechanisms;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 12/4/16
 */

public interface Shooter {
    void act();

    void shoot(int shots);

    void stop();

    int getShots();

    String getModeName();

    boolean isDone();

    void initialize();
}
