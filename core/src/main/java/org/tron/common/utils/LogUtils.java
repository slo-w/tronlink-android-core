
package org.tron.common.utils;

import android.util.Log;

/**
 * A tool class for log input, which can dynamically modify the output level
 */
public class LogUtils {

    /**
     * log output level  NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * log output level  V
     */
    public static final int LEVEL_VERBOSE = 1;
    /**
     * log output level  D
     */
    public static final int LEVEL_DEBUG = 2;
    /**
     * log output level  I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * log output level  W
     */
    public static final int LEVEL_WARN = 4;
    /**
     * log output level  E
     */
    public static final int LEVEL_ERROR = 5;

    /**
     * log output TAG
     */
    private static String mTag = "Tron";

    /**
     * Whether to allow log output
     */
    private static int mDebuggable = LEVEL_NONE;

    /**
     * Whether the current environment is debug.
     * Controlled by the calling app via init(boolean, int).
     * When false, all log output is suppressed regardless of level.
     */
    private static boolean mIsDebug = false;

    /**
     * variable for timing
     */
    private static long mTimestamp = 0;
    /**
     * lock object for writing files
     */
    private static final Object mLogLock = new Object();

    /**
     * Initialize with debug flag and log level.
     * Recommended usage in the calling app's Application:
     *   LogUtils.init(BuildConfig.DEBUG, LogUtils.LEVEL_VERBOSE);
     * In release builds, pass isDebug=false to suppress all log output.
     *
     * @param isDebug    pass BuildConfig.DEBUG from the calling app
     * @param debugLevel desired log level (only effective when isDebug=true)
     */
    public static void init(boolean isDebug, int debugLevel) {
        mIsDebug = isDebug;
        if (isDebug && debugLevel >= LEVEL_NONE && debugLevel <= LEVEL_ERROR) {
            mDebuggable = debugLevel;
        } else {
            mDebuggable = LEVEL_NONE;
        }
    }

    /**
     * @deprecated Use {@link #init(boolean, int)} instead.
     */
    @Deprecated
    public static void init(int debugLevel) {
        if (debugLevel >= LEVEL_NONE && debugLevel <= LEVEL_ERROR) {
            mDebuggable = debugLevel;
        }
    }

    /**
     * output LOG as level v
     */
    public static void v(String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level v
     */
    public static void v(String mTag, String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_VERBOSE) {
            Log.v(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level d
     */
    public static void d(String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, "" + msg);

        }
    }

    /**
     * output LOG as level d
     */
    public static void d(String mTag, String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_DEBUG) {
            Log.d(mTag, "" + msg);

        }
    }

    /**
     * output LOG as level i
     */
    public static void i(String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level i
     */
    public static void i(String mTag, String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_INFO) {
            Log.i(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level w
     */
    public static void w(String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level w
     */
    public static void w(String mTag, String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "" + msg);
        }
    }

    /**
     * output Throwable LOG as level w
     */
    public static void w(Throwable tr) {
        if (mIsDebug && mDebuggable >= LEVEL_WARN) {
            Log.w(mTag, "", tr);
        }
    }

    /**
     * output Throwable & LOG as level w
     */
    public static void w(String msg, Throwable tr) {
        if (mIsDebug && mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(mTag, "" + msg, tr);
        }
    }

    /**
     * output LOG as level e
     */
    public static void e(String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "" + msg);
        }
    }

    /**
     * output LOG as level e
     */
    public static void e(String mTag, String msg) {
        if (mIsDebug && mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "" + msg);
        }
    }

    /**
     * output Throwable LOG as level e
     */
    public static void e(Throwable tr) {
        if (mIsDebug && mDebuggable >= LEVEL_ERROR) {
            Log.e(mTag, "", tr);
        }
    }

    /**
     * output Throwable & LOG as level e
     */
    public static void e(String msg, Throwable tr) {
        if (mIsDebug && mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(mTag, "" + msg, tr);
        }
    }
}
