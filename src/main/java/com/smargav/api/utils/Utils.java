package com.smargav.api.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        //Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
//        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//        if (v != null) {
//            v.setGravity(Gravity.CENTER);
//            v.setTextSize(18);
//        }
//        toast.show();

        showToast(ctx, ctx.getString(message));
    }

    public static void showToast(Context ctx, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextSize(22);
        }
        toast.show();
    }

    public static void showPrompt(Activity ctx, int title, int messageId) {
        showPrompt(ctx, ctx.getString(title), ctx.getString(messageId));
    }

    public static void showPrompt(Activity ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        builder.setCancelable(false);
        ((TextView) customTitleView.findViewById(R.id.dialog_title)).setText(title);
        builder.setCustomTitle(customTitleView).setMessage(message).setPositiveButton("OK", null);
        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showPrompt(final Activity ctx, int title, int message, final boolean finish) {
        showPrompt(ctx, ctx.getString(title), ctx.getString(message), finish);

    }

    public static void showPrompt(final Activity ctx, String title, String message, final boolean finish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        ((TextView) customTitleView.findViewById(R.id.dialog_title)).setText(title);
        builder.setCancelable(false);
        builder.setCustomTitle(customTitleView).setMessage(message)
                .setPositiveButton("OK", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finish) {
                            ctx.setResult(Activity.RESULT_OK);
                            ctx.finish();
                        }
                    }
                });
        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                if (finish) {
                    ctx.setResult(Activity.RESULT_OK);
                    ctx.finish();
                }
            }
        });
        dialog.show();
    }


    public static void showYesNoPrompt(final Activity ctx, int title, int message,
                                       OnClickListener listener) {
        showYesNoPrompt(ctx, ctx.getString(title), ctx.getString(message), listener);

    }

    public static void showYesNoPrompt(final Activity ctx, String title, String message,
                                       OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        ((TextView) customTitleView.findViewById(R.id.dialog_title)).setText(title);
        builder.setCancelable(false);
        builder.setCustomTitle(customTitleView).setMessage(message).setPositiveButton("Yes", listener)
                .setNegativeButton("No", listener);
        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showPrompt(final Activity ctx, int title, int message,
                                  OnClickListener listener, int[] buttons) {
        showPrompt(ctx, ctx.getString(title), ctx.getString(message), listener, buttons);
    }

    public static void showPrompt(final Activity ctx, String title, String message,
                                  OnClickListener listener, int[] buttons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        TextView titleText = ((TextView) customTitleView.findViewById(R.id.dialog_title));
        titleText.setText(title);
        builder.setCustomTitle(customTitleView).setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(buttons[0], listener);
        if (buttons.length == 2) {
            builder.setNegativeButton(buttons[1], listener);
        }
        if (buttons.length == 3) {
            builder.setNeutralButton(buttons[2], listener);
        }

        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public static void showPrompt(final Activity ctx, String title, String message,
                                  OnClickListener listener, String[] buttons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        TextView titleText = ((TextView) customTitleView.findViewById(R.id.dialog_title));
        titleText.setText(title);
        builder.setCustomTitle(customTitleView).setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(buttons[0], listener);
        if (buttons.length == 2) {
            builder.setNegativeButton(buttons[1], listener);
        }
        if (buttons.length == 3) {
            builder.setNeutralButton(buttons[2], listener);
        }

        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    public static void showNonCancelablePrompt(final Activity ctx, String title, String message,
                                               OnClickListener listener, int[] buttons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        TextView titleText = ((TextView) customTitleView.findViewById(R.id.dialog_title));
        titleText.setText(title);
        builder.setCustomTitle(customTitleView).setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(buttons[0], listener);
        if (buttons.length == 2) {
            builder.setNegativeButton(buttons[1], listener);
        }
        if (buttons.length == 3) {
            builder.setNeutralButton(buttons[2], listener);
        }

        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showNonCancelablePrompt(final Activity ctx, String title, String message,
                                               OnClickListener listener, String[] buttons) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        TextView titleText = ((TextView) customTitleView.findViewById(R.id.dialog_title));
        titleText.setText(title);
        builder.setCustomTitle(customTitleView).setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(buttons[0], listener);
        if (buttons.length == 2) {
            builder.setNegativeButton(buttons[1], listener);
        }
        if (buttons.length == 3) {
            builder.setNeutralButton(buttons[2], listener);
        }

        final Dialog dialog = builder.create();
        customTitleView.findViewById(R.id.dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDobDialog(final Activity ctx, final TextView field, LocalDate startYear,
                                     final LocalDate fromYears, final LocalDate toYears) {

        // Calendar mcurrentDate = Calendar.getInstance();
        // mcurrentDate.setTime(startYear);
        DateTime startOfDay = startYear.toDateTimeAtStartOfDay();
        int mYear = startOfDay.getYear();
        int mMonth = startOfDay.getMonthOfYear() - 1;
        int mDay = startOfDay.getDayOfMonth();

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(ctx, new OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth,
                                  int selectedday) {

                String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                        "Nov", "Dec"};
                LocalDate c = new LocalDate(selectedyear, selectedmonth + 1, selectedday);

                field.setText(c.toString(DATE_FORMATTER));
                field.setTag(c.toDateTimeAtStartOfDay().getMillis());

            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        if (mDatePicker.getDatePicker() != null) {
            mDatePicker.getDatePicker().setCalendarViewShown(false);
            mDatePicker.getDatePicker().setMaxDate(toYears.toDateTimeAtCurrentTime().getMillis());
            mDatePicker.getDatePicker().setMinDate(fromYears.toDateTimeAtStartOfDay().getMillis());
        }

        mDatePicker.show();
    }

    public static void showIDExpiryDialog(final Activity ctx, final TextView field,
                                          LocalDate startYear, final LocalDate fromYears, final LocalDate toYears, final int messageId) {

        AlertDialog.Builder mDatePicker = new AlertDialog.Builder(ctx);

        final DatePicker datePicker = new DatePicker(ctx);
        datePicker.setMaxDate(toYears.toDateTimeAtStartOfDay().getMillis());
        datePicker.setMinDate(fromYears.toDateTimeAtStartOfDay().getMillis());

        DateTime startOfDay = startYear.toDateTimeAtStartOfDay();
        int mYear = startOfDay.getYear();
        int mMonth = startOfDay.getMonthOfYear() - 1;
        int mDay = startOfDay.getDayOfMonth();

        datePicker.updateDate(mYear, mMonth, mDay);
        datePicker.setCalendarViewShown(false);

        mDatePicker.setView(datePicker);
        mDatePicker.setPositiveButton("Done", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                int selectedyear = datePicker.getYear();
                int selectedmonth = datePicker.getMonth();
                int selectedday = datePicker.getDayOfMonth();
                String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                        "Nov", "Dec"};
                LocalDate selectedDate = new LocalDate(selectedyear, selectedmonth + 1, selectedday);

                field.setText(selectedDate.toString(DATE_FORMATTER));
                field.setTag(selectedDate.toDateTimeAtStartOfDay().getMillis());

            }
        });

        mDatePicker.setTitle("Select Date");

        mDatePicker.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        mDatePicker.setNeutralButton("Clear", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                field.setText(null);
                field.setTag(null);
            }
        });

        mDatePicker.show();
    }

    public static void showCalendarDialog(Context ctx, final TextView textView,
                                          final LocalDate fromYears, final LocalDate toYears) {
        LocalDate ld = null;
        if (textView.getTag() != null) {
            ld = (LocalDate) textView.getTag();
        } else {
            ld = LocalDate.now();
        }

        DateTime startOfDay = ld.toDateTimeAtStartOfDay();
        int mYear = startOfDay.getYear();
        int mMonth = startOfDay.getMonthOfYear() - 1;
        int mDay = startOfDay.getDayOfMonth();

//        int mYear = ld.getYear();
        //      int mMonth = ld.getMonthOfYear() - 1;
        //    int mDay = ld.getDayOfMonth();

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(ctx, new OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth,
                                  int selectedday) {

                LocalDate localDate = new LocalDate(selectedyear, selectedmonth + 1, selectedday);
                textView.setText(localDate.toString(Utils.DATE_FORMATTER));
                textView.setTag(localDate);
                textView.setError(null);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");

        if (mDatePicker.getDatePicker() != null) {
            mDatePicker.getDatePicker().setCalendarViewShown(false);
            mDatePicker.getDatePicker().setMaxDate(toYears.toDateTimeAtCurrentTime().getMillis());
            mDatePicker.getDatePicker().setMinDate(fromYears.toDateTimeAtStartOfDay().getMillis());
        }

        mDatePicker.show();
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
