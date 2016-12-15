package ftc.evlib.util;

import android.os.Environment;

import java.io.File;

/**
 * This file was made by the electronVolts, FTC team 7393
 * Date Created: 10/2/16
 *
 * Utilities to get files and directories on the phone storage
 * All files will be under the "FTC" directory in the phone's root directory
 */

public class FileUtil {
    private static final String APP_DIR_NAME = "FTC";

    /**
     * @return a reference to the root phone directory
     */
    public static File getRootDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @return a reference to the root phone directory
     */
    public static File getAppDir() {
        File dir = new File(getRootDir(), "/" + APP_DIR_NAME);
        mkdirsOrThrowException(dir);
        return dir;
    }

    /**
     * Get a reference to a file in the app directory
     *
     * @param filename the name of the file
     * @return the File
     */
    public static File getAppFile(String filename) {
        return new File(getAppDir(), filename);
    }

    public static File getDir(String dirname) {
        File dir = new File(getAppDir(), dirname);
        mkdirsOrThrowException(dir);
        return dir;
    }

    public static File getFile(String dirname, String filename) {
        return new File(getDir(dirname), filename);
    }

    public static File getPicturesDir() {
//        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return getDir("pictures");
    }

    public static File getPicturesFile(String filename) {
        return new File(getPicturesDir(), filename);
    }

    public static File getLogsDir() {
        return getDir("logs");
    }

    public static File getLogsFile(String filename) {
        return new File(getLogsDir(), filename);
    }

    public static File getOptionsDir() {
        return getDir("options");
    }

    public static File getOptionsFile(String filename) {
        return new File(getOptionsDir(), filename);
    }

    public static File getConfigsDir() {
        return getDir("configs");
    }

    public static File getConfigsFile(String filename) {
        return new File(getConfigsDir(), filename);
    }

    public static void mkdirsOrThrowException(File dir) {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Error creating directory \"" + dir + '"');
        }
    }
}
