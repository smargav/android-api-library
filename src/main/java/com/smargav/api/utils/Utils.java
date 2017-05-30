package com.smargav.api.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.smargav.api.R;
import com.smargav.api.validator.AbstractValidator;
import com.smargav.api.validator.Form;
import com.smargav.api.validator.Validate;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;

public class Utils {

    public final static String DATE_FORMAT = "dd-MM-yyyy";

    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
            .forPattern(DATE_FORMAT);

    public final static DateTimeFormatter REVERSE_DATE_FORMATTER = DateTimeFormat
            .forPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
            .forPattern("dd-MM-yyyy HH:mm");

    public static String getFormattedDate(long date) {
        if (date == -1) {
            return "";
        }

        LocalDate dt = new LocalDate(date);

        return dt.toString(DATE_FORMATTER);
    }

    public static String getFormattedDateReverse(long date) {
        if (date == -1) {
            return "";
        }

        LocalDate dt = new LocalDate(date);

        return dt.toString(REVERSE_DATE_FORMATTER);
    }

    public static String getFormattedDateTime(long date) {
        if (date == -1) {
            return "";
        }
        DateTime dt = new DateTime(date);

        return dt.toString(DATE_TIME_FORMATTER);
    }

    public static boolean hasNetwork(Context ctx) {
        ConnectivityManager mgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void showToast(Context ctx, int message) {
        DialogUtils.showToast(ctx, ctx.getString(message));
    }


    public static boolean isAppInFG(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // The first in the list of RunningTasks is always the foreground task.
        RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        return StringUtils.equals(foregroundTaskPackageName, context.getPackageName());
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static String getNetworkClass(NetworkInfo info) {

        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                default:
                    return "NA";
            }
        }
        return "NA";
    }

    public static boolean isLuhnFormat(String custUrn) {

        int[] digits = new int[custUrn.length()];

        for (int i = 0; i < custUrn.length(); i++) {
            digits[i] = Character.getNumericValue(custUrn.charAt(i));
        }

        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            // get digits in reverse order
            int digit = digits[length - i - 1];
            // every 2nd number multiply with 2
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return sum % 10 == 0;
    }


    public static void playNotificationTone(Context ctx) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ctx, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLargePhotoPreview(Context ctx, String data, int width, int height) {

        if (StringUtils.isBlank(data)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle("Image Size: " + (StringUtils.length(data) / 1024) + " KB");
        builder.setPositiveButton("OK", null);

        byte[] decoded = data.getBytes();
        Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);

        View ll = View.inflate(ctx, R.layout.large_image_preview, null);
        ImageView view = (ImageView) ll.findViewById(R.id.large_image_preview);

        view.setImageBitmap(bmp);

        builder.setView(ll);

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    public static Validate addValidator(Form form, TextView view, AbstractValidator validator) {
        Validate validate = new Validate(view);
        validate.addValidator(validator);
        form.addValidates(validate);
        return validate;
    }


}
