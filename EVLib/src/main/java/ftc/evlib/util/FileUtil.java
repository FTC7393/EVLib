package ftc.evlib.util;

import android.os.Environment;

import java.io.File;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/2/16
 */

public class FileUtil {
    public static File getFile(String filename) {
        return new File(getDir(), filename);
    }

    public static File getDir() {
        return Environment.getExternalStorageDirectory();
    }
}
