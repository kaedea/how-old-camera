/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String getSdpath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
