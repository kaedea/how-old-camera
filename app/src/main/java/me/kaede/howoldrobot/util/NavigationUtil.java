package me.kaede.howoldrobot.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by kaede on 2015/5/27.
 */
public class NavigationUtil {
    public static void naviToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "找不到安卓市场", Toast.LENGTH_LONG).show();;
        }
    }

}
