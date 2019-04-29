package com.smargav.api.logger;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Keep;
import android.util.Log;

import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.format.PatternFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;


@Keep
public class AppLogger {

    public static boolean DEBUG = false;
    private Logger logger = LoggerFactory.getLogger();
    private File logFile;

    private String relativeLogDirPath;
    private long purgeDuration;
    private FileAppender fileAppender;

    private static boolean isInitialized = false;

    private static AppLogger INSTANCE;
    private File logFolder;
    private Level level;

//    public static boolean init(Context ctx, String logDir, long purgeDurationInMillis) {
//        return init(ctx, new File(getExternalStorageDirectory(ctx), logDir), purgeDurationInMillis, Level.INFO);
//    }

    public static boolean init(Context ctx, File logDir, long purgeDurationInMillis) {
        return init(ctx, logDir, purgeDurationInMillis, Level.INFO);
    }

    public static boolean init(Context ctx, File logDir, long purgeDurationInMillis, Level loggingLevel) {
        if (INSTANCE == null) {
            INSTANCE = new AppLogger(ctx, logDir, purgeDurationInMillis, loggingLevel);
        }
        return isInitialized;
    }

    public static AppLogger get() {
        if (INSTANCE == null) {
            return new AppLogger();
        }
        return INSTANCE;
    }

    private AppLogger(Context ctx, File logDir, long purgeDurationInMillis, Level level) {
        logFolder = logDir;
        this.purgeDuration = purgeDurationInMillis;
        initLogger(ctx, level);
    }

    private AppLogger() {

    }

    private void initLogger(Context ctx, Level level) {

        try {
            boolean exists = logFolder.exists();
            if (!logFolder.exists() && logFolder.getParentFile().canWrite()) {
                exists = logFolder.mkdirs();
            }

            if (!exists) {
                Log.e("AppLogger", "Unable to create logs dir: " + logFolder);
                return;
            }

            purgeLogs(logFolder);
            createLogFile(level);

            String versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            int versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
            String info = "App Version - " + versionName + " (" + versionCode + ")";
            i(AppLogger.class, "App start time -  " + new Date());
            i(AppLogger.class, info);
            i(AppLogger.class, "Log file path: " + logFolder.getAbsolutePath());

            isInitialized = true;

        } catch (Exception ex) {
            System.out.println("ex: " + ex);
        }

    }

    private void createLogFile(Level level) {

        try {
            this.level = level;

            logFile = getRollingFileName(logFolder);

            String logFilePath = logFile.getAbsolutePath();


            fileAppender = new FileAppender();
            fileAppender.setFormatter(new LogFormatter());
            fileAppender.setAppend(true);
            fileAppender.setFileName(logFilePath);

            logFile = fileAppender.getLogFile();

            logger.removeAllAppenders();
            logger.addAppender(fileAppender);
            logger.setLevel(level);

            fileAppender.open();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getRollingFileName(File logFolder) {
        final String today = DateTime.now().toString("yyyy_MM_dd");
        String filenameFormat = today + ".%d.log";
        File defaultLogFileForToday = new File(logFolder, String.format(filenameFormat, 0));
        String[] files = logFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(today);
            }
        });


        if (files == null || files.length == 0) {
            return defaultLogFileForToday;
        }

        String lastFile = files[files.length - 1];

        //Check file size and rollover.
        File f = new File(lastFile);
        if (f.length() < FileUtils.ONE_MB * 2) {
            return f;
        }
        String number = StringUtils.substringBetween(lastFile, today + ".", ".log");
        if (StringUtils.isNumeric(number)) {
            int num = Integer.parseInt(number);
            num++;
            return new File(logFolder, String.format(filenameFormat, num));
        }

        return defaultLogFileForToday;
    }

    public static synchronized File getExternalStorageDirectory(Context mContext) {

        File externalStorageDirectory = Environment
                .getExternalStorageDirectory();

        try {
            final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");

            File sdCard = new File(rawExternalStorage);

            if (sdCard.exists() && sdCard.canWrite()) {
                externalStorageDirectory = sdCard;
                return externalStorageDirectory;
            } else {
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
            if (logFile.exists() && logFile.length() >= FileUtils.ONE_MB * 2) {
                createLogFile(level);
            }
            if (!logFile.exists()) {
                fileAppender.open();
            }
        } catch (Exception e) {

        }
    }

    public void error(Class c, String msg) {
        try {
            String tag = getClassName(c);
            if (DEBUG) {
                Log.e(tag, msg);
            }
            checkFile();
            if (logger != null) {
                logger.error(tag + ": " + msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(Class c, Throwable e) {
        try {
            String tag = getClassName(c);
            if (DEBUG) {
                Log.e(tag, e.getMessage(), e);
            }
            checkFile();
            if (logger != null) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public void error(Class c, String msg, Throwable thr) {
        try {
            String tag = getClassName(c);
            if (DEBUG) {
                Log.e(tag, msg, thr);
            }
            checkFile();
            if (logger != null)
                logger.error(tag + ": " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void debug(Class c, String msg) {
        String tag = getClassName(c);

        //if (DEBUG) {
        Log.d(tag, msg);
        //}
        if (logger != null)
            logger.debug(tag + " - " + msg);
    }

    public void warn(Class c, String msg) {
        try {
            String tag = getClassName(c);
            if (DEBUG) {
                Log.w(tag, msg);
            }
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
            if (DEBUG) {
                Log.i(tag, msg);
            }
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
            for (File file : list) {
                if (file.lastModified() + purgeDuration < System.currentTimeMillis()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class LogFormatter extends PatternFormatter {

        public LogFormatter() {
            setPattern("%d{3} %c{1} [%P] %m %T");
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
