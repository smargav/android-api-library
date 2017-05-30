package com.smargav.api.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.smargav.api.R;
import com.smargav.api.utils.DialogUtils;

/**
 * Utility class to request for all the required permissions in advance when the App starts.
 * This is handy when you really want users to grant all the permissions that your app requires.
 */
public class PermissionsActivity extends AppCompatActivity {
    public static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS};

    /**
     * @return
     */
    public static String[] getPermissions() {
        return permissions;
    }

    public static void setPermissions(String[] permissions) {
        PermissionsActivity.permissions = permissions;
    }

    public static boolean hasAllRequiredPerms(Context context) {
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // VARIABLE FOR MIXPANEL ANALYTICS


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_Transparent);

        if (hasAllRequiredPerms(this)) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        startRuntimePerms();

    }

    private void startRuntimePerms() {
        ActivityCompat.requestPermissions(this, permissions, 100);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean allGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
            }
        }

        if (!allGranted) {
            DialogUtils.showNonCancelablePrompt(this, "Error", "Please grant all permissions else App cannot be used ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startRuntimePerms();
                }
            }, new String[]{"OK"});
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }
}
