/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.util.Log;

class Logger {

    private static final String TAG = "age.analyse";

    static void v(String msg) {
        Log.v(TAG, msg);
    }

    static void d(String msg) {
        Log.d(TAG, msg);
    }

    static void w(String msg) {
        Log.w(TAG, msg);
    }

    static void w(Throwable throwable) {
        Log.w(TAG, throwable);
    }
}
