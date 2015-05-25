package kaede.me.howoldrobot.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by kaede on 2015/5/24.
 */
public class AppUtil {
    public static int getScreenWitdh(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return  dm.widthPixels;
    }

	public static int getScreenHeight(Context context){
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return  dm.heightPixels;
	}
}
