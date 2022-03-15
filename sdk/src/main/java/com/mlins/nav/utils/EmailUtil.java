package com.mlins.nav.utils;

import android.app.Activity;
import android.content.Intent;

import com.mlins.utils.logging.Log;

public class EmailUtil {
    private final static String TAG = "com.mlins.nav.utils.EmailUtil";

    public static void sendMailto(String address, Activity act) {
        Log.getInstance().debug(TAG, "Enter, sendMailto()");
        try {
            if (act != null && address != null && !address.equals("")) {
                Intent emailintent = new Intent(
                        Intent.ACTION_SEND);
                emailintent
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailintent.setType("application/image");
                emailintent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{address});
                // emailintent.putExtra(Intent.EXTRA_STREAM,
                // Uri.parse("file://"+FULLFILE+".png"));
                act.startActivity(Intent.createChooser(emailintent,
                        "send mail..."));
            }

        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit");
    }

}
