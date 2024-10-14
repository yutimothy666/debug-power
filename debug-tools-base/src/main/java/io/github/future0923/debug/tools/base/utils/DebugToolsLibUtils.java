package io.github.future0923.debug.tools.base.utils;

import io.github.future0923.debug.tools.base.logging.AnsiLog;

import java.io.File;

/**
 * @author future0923
 */
public class DebugToolsLibUtils {


    private static File DEBUG_TOOLS_HOME_DIR;
    private static final File DEBUG_TOOLS_LIB_DIR;

    static {
        String debugToolsLibDirEnv = System.getenv("DEBUG_TOOLS_HOME_DIR");
        if (debugToolsLibDirEnv != null) {
            DEBUG_TOOLS_HOME_DIR = new File(debugToolsLibDirEnv);
            AnsiLog.info("DEBUG_TOOLS_LIB_DIR: " + debugToolsLibDirEnv);
        } else {
            DEBUG_TOOLS_HOME_DIR = new File(System.getProperty("user.home") + File.separator + ".debugTools");
        }
        try {
            DEBUG_TOOLS_HOME_DIR.mkdirs();
        } catch (Throwable t) {
            //ignore
        }
        if (!DEBUG_TOOLS_HOME_DIR.exists()) {
            // try to set a temp directory
            DEBUG_TOOLS_HOME_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".debugTools");
            try {
                DEBUG_TOOLS_HOME_DIR.mkdirs();
            } catch (Throwable e) {
                // ignore
            }
        }
        if (!DEBUG_TOOLS_HOME_DIR.exists()) {
            System.err.println("Can not find directory to save debug tools lib. please try to set user home by -Duser.home=");
        }
        DEBUG_TOOLS_LIB_DIR = new File(DEBUG_TOOLS_HOME_DIR, "lib");
        if (!DEBUG_TOOLS_LIB_DIR.exists()) {
            try {
                DEBUG_TOOLS_LIB_DIR.mkdirs();
            } catch (Throwable e) {
                // ignore
            }
        }

    }

    public static File getDebugToolsHomeDir() {
        return DEBUG_TOOLS_HOME_DIR;
    }

    public static File getDebugToolsLibDir() {
        return DEBUG_TOOLS_LIB_DIR;
    }
}
