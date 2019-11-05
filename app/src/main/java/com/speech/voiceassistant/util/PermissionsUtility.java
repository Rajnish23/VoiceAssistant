package com.speech.voiceassistant.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsUtility {

    public static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 11;
    public static final int READ_CONTACT_CALL_PERMISSION_REQUEST_CODE = 12;
    public static final int WRITE_CONTACT_CALL_PERMISSION_REQUEST_CODE = 13;

    public static void checkRequiredPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        }
    }

    public static void checkReadContactAndCallPerrmission(Activity context) {

        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE},
                READ_CONTACT_CALL_PERMISSION_REQUEST_CODE);

    }
    public static void checkWriteContactPerrmission(Activity context) {

        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_CONTACTS},
                WRITE_CONTACT_CALL_PERMISSION_REQUEST_CODE);

    }

}
