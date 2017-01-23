package com.smargav.api.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.smargav.api.logger.AppLogger;

public class DeviceAdminUtils {
	public static <T> boolean isAppAdmin(Context context, Class<T> t) {
		DevicePolicyManager dpm = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminComponent = new ComponentName(context, t);
		return dpm.isAdminActive(adminComponent);
	}

	public static <T> Intent enableDeviceAdmin(Context context, Class<T> t) {

		try {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					new ComponentName(context, t));
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"Enable device admin");
			return intent;
		} catch (Exception e) {
			AppLogger.e(DeviceAdminUtils.class, e);
		}

		return null;
	}

	public static <T> Intent enableDeviceAdmin(Context context, Class<T> t, String explanation) {

		try {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					new ComponentName(context, t));
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					explanation);
			return intent;
		} catch (Exception e) {
			AppLogger.e(DeviceAdminUtils.class, e);
		}

		return null;
	}

	public static <T> Intent disableDeviceAdmin(Context context, Class<T> t) {

		try {
			DevicePolicyManager dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
			dpm.removeActiveAdmin(new ComponentName(context, t));
		} catch (Exception e) {
			AppLogger.e(DeviceAdminUtils.class, e);
		}

		return null;
	}

}
