/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    private static final String TAG = "age.bitmap";

    public static Bitmap zoomBitmapToWidth(Bitmap bitmap, int width) {
        Log.v(TAG, "width = " + width);
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bitmap.getWidth(), (float) width / bitmap.getWidth());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    public static Bitmap zoomBitmapToHeight(Bitmap bitmap, int height) {
        Log.v(TAG, "height = " + height);
        Matrix matrix = new Matrix();
        matrix.postScale((float) height / bitmap.getHeight(), (float) height / bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

    public static Boolean saveBitmap(Bitmap bitmap, int quality, String path) {
        File imageFile = new File(path);
        try {
            FileUtil.checkCreateFile(imageFile);
        } catch (IOException e) {
            Log.w(TAG, e);
            return false;
        }

        return saveBitmap(bitmap, quality, imageFile);
    }

    public static boolean saveBitmap(Bitmap bitmap, int quality, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            fos.getFD().sync();
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Nullable
    public static Bitmap getBitmapFromView(View view) {
        return getBitmapFromView(view, true);
    }

    @Nullable
    public static Bitmap getBitmapFromView(View view, Boolean isUseDrawingCache) {
        if (view.getVisibility() != View.VISIBLE || view.getMeasuredHeight() <= 0
                || view.getMeasuredWidth() <= 0) {
            // Either this
            // int specWidth = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.AT_MOST);
            // Or this
            int specWidth = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
            int specHeight = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
            view.measure(specWidth, specHeight);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            Log.d(TAG, "Manually measure invisible view, measuredWidth = " + measuredWidth
                    + ", measuredHeight = " + measuredHeight);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        return getBitmapFromVisibleView(view, isUseDrawingCache);
    }

    private static Bitmap getBitmapFromVisibleView(View view, Boolean useDrawingCache) {
        if (useDrawingCache) {
            view.setDrawingCacheEnabled(true);
            return view.getDrawingCache();
        }

        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("This method should called in UI thread.");
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        }
        // else canvas.drawColor(Color.TRANSPARENT);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }
}
