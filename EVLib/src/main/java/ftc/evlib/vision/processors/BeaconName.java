package ftc.evlib.vision.processors;

import com.google.common.collect.ImmutableList;

import java.util.List;

import ftc.electronvolts.util.TeamColor;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 11/15/16
 *
 * enum that represents the different types of beacon images
 */
public enum BeaconName {
    WHEELS,
    TOOLS,
    LEGOS,
    GEARS;

    /**
     * Get the names of the beacons for a certain team color
     *
     * @param teamColor your team's color
     * @return the list of the beacon names
     */
    public static List<BeaconName> getNamesForTeamColor(TeamColor teamColor) {
        if (teamColor == TeamColor.RED) {
            return ImmutableList.of(BeaconName.GEARS, BeaconName.TOOLS);
        } else {
            return ImmutableList.of(BeaconName.WHEELS, BeaconName.LEGOS);
        }
    }
}
