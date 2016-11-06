package com.smargav.api.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class NotificationUtils {
	/*
	 * @author Ashwin Surana
	 */
	public static void createNotification(Context context, String title,
			String message, int notificationId, int icon) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(icon).setContentTitle(title)
				.setContentText(message).setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		mNotificationManager.notify(notificationId, notification);
	}

	public static void createNotification(Context context, String title,
			String message, Intent resultIntent, int notificationID, int icon,
			boolean returnToHomeScreen) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(icon).setContentTitle(title)
				.setContentText(message).setAutoCancel(true);
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
