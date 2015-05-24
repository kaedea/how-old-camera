package kaede.me.howoldrobot.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by kaede on 2015/5/24.
 */
public class BitmapUtil {
    public static Bitmap zoomBitmapToWidth(Bitmap bitmap, int width) {
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bitmap.getWidth(), (float) width / bitmap.getWidth());
        Bitmap bitmapResized = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmapResized;

    }
}
