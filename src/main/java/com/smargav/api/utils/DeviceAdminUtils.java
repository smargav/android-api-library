package com.smargav.api.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceAdminUtils {
	public static <T> boolean isAppAdmin(Context context, Class<T> t) {
		DevicePolicyManager dpm = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminComponent = new ComponentName(context, t);
		return dpm.isAdminActive(adminComponent);
	}

	public static <T> Intent enableDeviceAdmin(Activity activity, Class<T> t) {

		try {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					new ComponentName(activity, t));
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					"Ask your Employer ");
			return intent;
		} catch (Exception e) {
			Log.w("DEVICEADMIN", "Exception hwile enabling", e);
		}

		return null;
	}

}
