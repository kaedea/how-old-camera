package kaede.me.howoldrobot.util;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.*;

/**
 * Created by kaede on 2015/5/24.
 */
public class BitmapUtil {

	private static final String TAG = "BitmapUtil";

	public static Bitmap zoomBitmapToWidth(Bitmap bitmap, int width) {
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bitmap.getWidth(), (float) width / bitmap.getWidth());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }

	public static Bitmap zoomBitmapToHeight(Bitmap bitmap, int height) {
		Matrix matrix = new Matrix();
		matrix.postScale((float) height / bitmap.getHeight(), (float) height / bitmap.getHeight());
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

	}

	public static Boolean saveBitmapToSd(Bitmap bitmap,int quality,String path) {
		File imagePath = new File(path);
		if (!imagePath.exists()) try {
			imagePath.createNewFile();
		} catch (IOException e) {
			Log.e(TAG,"[kaede] Create file fail!");
			e.printStackTrace();
			return false;
		}
		if (imagePath.delete()){
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(imagePath);
				bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
				fos.flush();
				fos.close();
				return true;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return false;
			}
		}else {
			Log.e(TAG,"[kaede] Image had exist and can not be deleted!");
			return false;
		}

	}

	public static Bitmap getViewBitmap(View view){
		View rootView = view.getRootView();
		view.invalidate();
		rootView.setDrawingCacheEnabled(true);
		return rootView.getDrawingCache();
	}

	public static Bitmap getBitmapFromView(View v,Boolean isUseDrawingCache) {
		if (v.getMeasuredHeight()<=0||v.getMeasuredWidth()<=0){
			// Either this
			//int specWidth = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.AT_MOST);// Or this
			int specWidth = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
			int specHeight = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
			v.measure(specWidth, specHeight);
			int questionWidth = v.getMeasuredWidth();
			int questionHeight = v.getMeasuredHeight();
			Log.d(TAG, "[kaede][getBitmapFromView] questionWidth = " + questionWidth + " questionHeight=" + questionHeight);
			v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		}
		return getBitmapFromVisiableView(v, isUseDrawingCache);
	}

	private static Bitmap getBitmapFromVisiableView(View view,Boolean isUseDrawingCache){
		if (isUseDrawingCache){
			View rootView = view.getRootView();
			view.invalidate();
			rootView.setDrawingCacheEnabled(true);
			return rootView.getDrawingCache();
		}
		Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		Drawable bgDrawable =view.getBackground();
		if (bgDrawable!=null)
			bgDrawable.draw(c);
		//else c.drawColor(Color.TRANSPARENT);
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.draw(c);
		return b;

	}

	public static Bitmap getScreenshot(Context context){
		FileInputStream graphics = null;
		try {
			graphics = new FileInputStream("/dev/graphics/fb0");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		DisplayMetrics dm = new DisplayMetrics();
		Display display =((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(dm);
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800p）

		PixelFormat pixelFormat = new PixelFormat();
		PixelFormat.getPixelFormatInfo(PixelFormat.RGBA_8888, pixelFormat);
		int deepth = pixelFormat.bytesPerPixel; // 位深
		byte[] piex = new byte[screenHeight * screenWidth * deepth]; // 像素

		try {
			DataInputStream dStream = new DataInputStream(graphics);
			dStream.readFully(piex);
			dStream.close();

			int[] colors = new int[screenHeight * screenWidth];
			// 将rgb转为色值
			for (int m = 0; m < colors.length; m++) {
				int r = (piex[m * 4] & 0xFF);
				int g = (piex[m * 4 + 1] & 0xFF);
				int b = (piex[m * 4 + 2] & 0xFF);
				int a = (piex[m * 4 + 3] & 0xFF);
				colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
			}

			return Bitmap.createBitmap(colors, screenWidth, screenHeight,
					Bitmap.Config.ARGB_8888);
		} catch (IOException e) {
			e.printStackTrace();
			return  null;
		}
	}
}
