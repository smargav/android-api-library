package com.smargav.api.gcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.smargav.api.logger.AppLogger;
import com.smargav.api.prefs.PreferencesUtil;

import java.io.IOException;

/**
 * Created by ashwin on 18/03/15.
 * Updated by Amit on 10/02/16
 */
public class GCMRegister {

    public static final String REGISTRATION_ID = "gcmRegId";

    public static String getRegId(Context context) {
        return PreferencesUtil.getString(context, REGISTRATION_ID, null);
    }

    public static String registerToGCM(final Context context, final String projectNumber) throws IOException {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String regId = null;

        regId = gcm.register(projectNumber);
        AppLogger.i(GCMRegister.class, "GCM : " + regId);

        PreferencesUtil.putString(context, REGISTRATION_ID, regId);

        return regId;
        //AppLogger.i(getClass(), "GCM ID " + regId);
    }


    public static void sendDummyNotif(Context ctx) {
        Intent intent = new Intent();
        intent.setAction("com.google.android.c2dm.intent.RECEIVE");
        intent.addCategory(ctx.getPackageName());
        intent.putExtra("message_type", "gcm");
        intent.putExtra("message", "{'title':'Title','text':'Hey.. This is from GCM', 'time':123123123123}");
        ctx.sendBroadcast(intent);

    }

}
