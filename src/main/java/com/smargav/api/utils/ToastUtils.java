package com.smargav.api.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {

    private static void makeToast(Context ctx, String message, int duration,
                                  int gravity) {

        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showCenteredToast(Context ctx, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) {
            v.setGravity(Gravity.CENTER);
            v.setTextSize(22);
        }
        toast.show();
    }

    public static void showToast(Context ctx, String message) {
        makeToast(ctx, message, -1, -1);
    }

    public static void showToast(Context ctx, String message, int duration) {
        makeToast(ctx, message, duration, -1);

    }

}
