package com.smargav.api.logger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.format.PatternFormatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings(value = {"rawtypes"})
public class AppLogger {

    private Logger logger = LoggerFactory.getLogger();
    private File logFile;

    private String RELATIVE_LOG_DIR;
    private long purgeDuration;
    private FileAppender fileAppender;

    private static boolean isInitialized = false;

    private static AppLogger INSTANCE;

    public static boolean init(Context ctx, String logDir, long purgeDuration) {
        if (INSTANCE == null) {
            INSTANCE = new AppLogger(ctx, logDir, purgeDuration);
        }
        return isInitialized;
    }

    public static AppLogger get() {
        if (INSTANCE == null) {
            return new AppLogger();
        }
        return INSTANCE;
    }

    private AppLogger(Context ctx, String logDir, long purgeDuration) {
        RELATIVE_LOG_DIR = logDir;
        this.purgeDuration = purgeDuration;
        initLogger(ctx);
    }

    private AppLogger() {

    }

    private void initLogger(Context ctx) {

        try {

            File mSdCardLogFile = null;
            String externalStorageState = Environment.getExternalStorageState();
            if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
                File externalStorageDirectory = getExternalStorageDirectory(ctx);
                if (externalStorageDirectory != null) {
                    mSdCardLogFile = new File(externalStorageDirectory, RELATIVE_LOG_DIR);
                }
            }

            if (mSdCardLogFile == null) {
                Log.e("AppLogger", "Unable to initialze the Log File Appeneder");
                return;
            }

            if (!mSdCardLogFile.exists()) {
                mSdCardLogFile.mkdirs();
            }

            purgeLogs(mSdCardLogFile);

            long date = System.currentTimeMillis();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
            String logFilePath = new File(mSdCardLogFile, dateFormat.format(date) + "_log.txt").getAbsolutePath();

            fileAppender = new FileAppender();
            fileAppender.setFormatter(new LogFormatter());
            fileAppender.setAppend(true);
            fileAppender.setFileName(logFilePath);

            logFile = fileAppender.getLogFile();

            logger.addAppender(fileAppender);
            logger.setLevel(Level.INFO);


            String versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            int versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
            String info = "App Version - " + versionName + " (" + versionCode + ")";
            i(AppLogger.class, "App start time -  " + new Date());
            i(AppLogger.class, info);

            isInitialized = true;

        } catch (Exception ex) {
            System.out.println("ex: " + ex);
        }

    }

    protected synchronized File getExternalStorageDirectory(Context mContext) {

        File externalStorageDirectory = Environment
                .getExternalStorageDirectory();

        try {
            final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");

            File sdCard = new File(rawExternalStorage);

            if (sdCard.exists() && sdCard.canWrite()) {
                externalStorageDirectory = sdCard;
                return externalStorageDirectory;
            }else{
                externalStorageDirectory = Environment
                        .getExternalStorageDirectory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (externalStorageDirectory != null) {
            if (!externalStorageDirectory.exists()) {
                if (!externalStorageDirectory.mkdirs()) {
                    externalStorageDirectory = null;
                    Log.e("AppLogger", "mkdirs failed on externalStorageDirectory " + externalStorageDirectory);
                }
            }
        }
        return externalStorageDirectory;
    }

    private void checkFile() {
        try {
            if (!logFile.exists()) {
                fileAppender.open();
            }
        } catch (Exception e) {
        }
    }

    public void error(Class c, String msg) {
        try {
            String tag = getClassName(c);
            Log.e(tag, msg);
            checkFile();
            if (logger != null)
                logger.error(tag + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(Class c, Throwable e) {
        try {
            String tag = getClassName(c);
            Log.e(tag, e.getMessage(), e);
            checkFile();
            if (logger != null)
                logger.error(tag + ": " + e.getLocalizedMessage(), e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public void error(Class c, String msg, Throwable thr) {
        try {
            String tag = getClassName(c);
            Log.e(tag, msg, thr);
            checkFile();
            if (logger != null)
                logger.error(tag + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debug(Class c, String msg) {
        String tag = getClassName(c);
        Log.d(tag, msg);
        if (logger != null)
            logger.debug(tag + " - " + msg);
    }

    public void warn(Class c, String msg) {
        try {
            String tag = getClassName(c);
            Log.w(tag, msg);
            checkFile();
            if (logger != null)
                logger.warn(tag + " - " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(Class c, String msg) {
        try {
            String tag = getClassName(c);
            Log.i(tag, msg);
            checkFile();
            if (logger != null)
                logger.info(tag + " - " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public File getLogFile() {
        return logFile;
    }

    public void purgeLogs(File logDir) {
        try {

            File[] list = logDir.listFiles();
            // if file is older than 100 days delete.
            for (File file : list) {
                if (file.lastModified() < purgeDuration) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class LogFormatter extends PatternFormatter {

        public LogFormatter() {
            setPattern("%d %c{1} [%P] %m %T");
        }

        public String format(String clientID, String name, long time, Level level, Object message,
                             Throwable t) {
            return super.format(clientID, name, System.currentTimeMillis(), level, message, t);
        }
    }

    public static void e(Class c, String msg) {
        AppLogger.get().error(c, msg);
    }

    public static void e(Class c, Throwable thr) {
        AppLogger.get().error(c, thr.getMessage(), thr);
    }

    public static void e(Class c, String msg, Throwable thr) {
        AppLogger.get().error(c, msg, thr);
    }

    public static void d(Class c, String msg) {
        AppLogger.get().debug(c, msg);
    }

    public static void w(Class c, String msg) {
        AppLogger.get().warn(c, msg);
    }

    public static void i(Class c, String msg) {
        AppLogger.get().info(c, msg);
    }

    public static String getClassName(Class c) {
        return "SGV [" + c.getSimpleName() + "] ";
    }

}
