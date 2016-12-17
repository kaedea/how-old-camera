/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;


public class DeviceUtil {

    public static int width(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int height(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static String packageName(Context context) {
        return context.getPackageName();
    }
}
