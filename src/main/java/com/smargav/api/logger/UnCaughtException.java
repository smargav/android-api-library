package com.smargav.api.logger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UnCaughtException implements UncaughtExceptionHandler {

    public interface UnCaughtExceptionListener {
        public List<String> getAttachments();
        public String getEmail();
    }

    private UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private Context context;
    private UnCaughtExceptionListener listener;


    public UnCaughtException(Context ctx) {
        context = ctx;
    }


    public UnCaughtException(Context ctx, UnCaughtExceptionListener listener) {
        context = ctx;
        this.listener = listener;
    }


    private StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }

    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private void addInformation(StringBuilder message) {
        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
            message.append("Could not get Version information for ").append(context.getPackageName());
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT).append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
        StatFs stat = getStatFs();
        message.append("Total Internal memory: ").append(getTotalInternalMemorySize(stat)).append('\n');
        message.append("Available Internal memory: ").append(getAvailableInternalMemorySize(stat))
                .append('\n');
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            StringBuilder report = new StringBuilder();
            Date curDate = new Date();
            report.append("Error Report collected on : ").append(curDate.toString()).append('\n')
                    .append('\n');
            report.append("Informations :").append('\n');
            addInformation(report);
            report.append('\n').append('\n');
            report.append("Stack:\n");
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            report.append(result.toString());
            printWriter.close();
            report.append('\n');
            report.append("**** End of current Report ***");

            AppLogger.e(getClass(), report.toString());
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(t, e);
            }

            List<String> attachments = new ArrayList<>();
            String email;
            if (listener != null) {
                attachments = listener.getAttachments();
                email = listener.getEmail();

            }
            context.startActivity(LogsMailer.appCrashEmailIntent(report.toString(), context));
            System.exit(2);
        } catch (Exception ignore) {
            AppLogger.e(getClass(), "Error while sending error e-mail", ignore);
        }
    }

    /**
     * This method for call alert dialog when application crashed!
     */
    public void sendErrorMail(String report) {

    }
}