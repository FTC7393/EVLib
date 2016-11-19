package ftc.evlib.util;

import android.os.Environment;

import java.io.File;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/2/16
 *
 * Utilities to get directories on the phone storage
 */

public class FileUtil {
    /**
     * Get a reference to a file in the root directory
     *
     * @param filename the name of the file
     * @return the File
     */
    public static File getFile(String filename) {
        return new File(getDir(), filename);
    }

    /**
     * @return a reference to the root phone directory
     */
    public static File getDir() {
        return Environment.getExternalStorageDirectory();
    }
}
