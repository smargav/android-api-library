package com.smargav.api.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.smargav.api.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by Amit S on 30/05/17.
 */

public class DialogUtils {
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finish) {
                            ctx.setResult(Activity.RESULT_OK);
                            ctx.finish();
                        }
                    }
                });
        final Dialog dialog = builder.create();

        dialog.show();
    }

    public static void showYesNoPrompt(final Activity ctx, int title, int message,
                                       DialogInterface.OnClickListener listener) {
        showYesNoPrompt(ctx, ctx.getString(title), ctx.getString(message), listener);

    }

    public static void showYesNoPrompt(final Activity ctx, String title, String message,
                                       DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View customTitleView = ctx.getLayoutInflater().inflate(R.layout.dialog_title, null);
        ((TextView) customTitleView.findViewById(R.id.dialog_title)).setText(title);
        builder.setCancelable(false);
        builder.setCustomTitle(customTitleView).setMessage(message).setPositiveButton("Yes", listener)
                .setNegativeButton("No", listener);
        final Dialog dialog = builder.create();

        dialog.show();
    }

    public static void showPrompt(final Activity ctx, int title, int message,
                                  DialogInterface.OnClickListener listener, int[] buttons) {
        showPrompt(ctx, ctx.getString(title), ctx.getString(message), listener, buttons);
    }

    public static void showPrompt(final Activity ctx, String title, String message,
                                  DialogInterface.OnClickListener listener, int[] buttons) {
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

        dialog.show();
    }

    public static void showPrompt(final Activity ctx, String title, String message,
                                  DialogInterface.OnClickListener listener, String[] buttons) {
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

        dialog.show();
    }

    public static void showNonCancelablePrompt(final Activity ctx, String title, String message,
                                               DialogInterface.OnClickListener listener, int[] buttons) {
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

        dialog.show();
    }

    public static void showNonCancelablePrompt(final Activity ctx, String title, String message,
                                               DialogInterface.OnClickListener listener, String[] buttons) {
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
        dialog.show();
    }

    public static void showDobDialog(final Activity ctx, final TextView field, LocalDate startYear,
                                     final LocalDate fromYears, final LocalDate toYears) {


        DateTime startOfDay = startYear.toDateTimeAtStartOfDay();
        int mYear = startOfDay.getYear();
        int mMonth = startOfDay.getMonthOfYear() - 1;
        int mDay = startOfDay.getDayOfMonth();

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth,
                                  int selectedday) {

                String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                        "Nov", "Dec"};
                LocalDate c = new LocalDate(selectedyear, selectedmonth + 1, selectedday);

                field.setText(c.toString(DateUtils.DATE_FORMATTER));
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
        mDatePicker.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                int selectedyear = datePicker.getYear();
                int selectedmonth = datePicker.getMonth();
                int selectedday = datePicker.getDayOfMonth();
                String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
                        "Nov", "Dec"};
                LocalDate selectedDate = new LocalDate(selectedyear, selectedmonth + 1, selectedday);

                field.setText(selectedDate.toString(DateUtils.DATE_FORMATTER));
                field.setTag(selectedDate.toDateTimeAtStartOfDay().getMillis());

            }
        });

        mDatePicker.setTitle("Select Date");

        mDatePicker.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        mDatePicker.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
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
        mDatePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth,
                                  int selectedday) {

                LocalDate localDate = new LocalDate(selectedyear, selectedmonth + 1, selectedday);
                textView.setText(localDate.toString(DateUtils.DATE_FORMATTER));
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

    public static void showTimeDialog(Context ctx, final TextView textView) {
        LocalDate ld = null;
        if (textView.getTag() != null) {
            ld = (LocalDate) textView.getTag();
        } else {
            ld = LocalDate.now();
        }

        DateTime startOfDay = DateTime.now();
        int mH = startOfDay.getHourOfDay();
        int mM = startOfDay.getMinuteOfDay();

        TimePickerDialog mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hh, int mm) {
                textView.setTag(hh * 60 * 60 * 1000 + mm * 60 * 1000);
                textView.setText(hh + ":" + mm);
            }
        }, mH, mM, true);

        mTimePicker.setTitle("Select Time");

        mTimePicker.show();
    }
}
