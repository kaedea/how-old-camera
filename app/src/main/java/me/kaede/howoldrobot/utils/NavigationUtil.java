/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class NavigationUtil {

    public static void goToMarket(Context context, String packageName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "找不到安卓市场", Toast.LENGTH_LONG).show();
        }
    }

}
