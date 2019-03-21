package com.smargav.api.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class NotificationUtils {
    /*
     * @author Ashwin Surana
     */
    public static void createNotification(Context context, String title,
                                          String message, int notificationId, int icon) {

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), icon);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(icon).setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setVibrate(new long[]{1, 1, 1})
                .setTicker(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setLargeIcon(icon1)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_ALL);


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(notificationId, notification);
    }

    public static void createNotification(Context context, String title,
                                          String message, Intent resultIntent, int notificationID, int icon,
                                          boolean returnToHomeScreen) {

        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), icon);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(icon).setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setVibrate(new long[]{1, 1, 1})
                .setTicker(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setLargeIcon(icon1)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_ALL);


        if (resultIntent != null) {
            if (resultIntent != null && returnToHomeScreen) {
                TaskStackBuilder stackBuilder = TaskStackBuilder
                        .create(context);
                stackBuilder.addParentStack(resultIntent.getComponent());
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
            } else {
                PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                        resultIntent, 0);
                mBuilder.setContentIntent(pIntent);
            }
        } else {
            Log.e("NOTIFICATION", "ERROR CREATING A NOTIFICATION");
            return;
        }
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(notificationID, notification);
    }
}
