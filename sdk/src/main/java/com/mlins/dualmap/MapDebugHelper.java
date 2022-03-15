package com.mlins.dualmap;

import android.content.Context;

import com.spreo.spreosdk.BuildConfig;

public class MapDebugHelper {

    private static final boolean ENABLED = false;

    public static void onException(Context context, Throwable t) {
        if(ENABLED
                && BuildConfig.DEBUG
                && ("com.mlins.dual".equals(context.getPackageName())
                        /*|| "com.rbs.dual".equals(context.getPackageName())*/)
                ) {
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        } else t.printStackTrace();
    }

}
