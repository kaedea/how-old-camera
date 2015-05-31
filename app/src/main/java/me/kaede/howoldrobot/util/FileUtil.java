package me.kaede.howoldrobot.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kaede on 2015/5/25.
 */
public class FileUtil {

	private static final String TAG = "FileUtil";

	public static String getSdpath(){
		return  Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static File getTempFile(String dir, String fieExtension) {
		String filePath = dir + File.separator + String.valueOf(System.currentTimeMillis()) + fieExtension;
		File file = new File(filePath);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(TAG,e.getMessage(),e );
				return null;
			}
		}
		return file;

	}

	public static Boolean makeFile(String[] dirs) throws IOException {
		String path = getSdpath();
		for (int i = 1; i <= dirs.length; i++) {
			if (i != dirs.length) {
				path = path + File.separator + dirs[i - 1];
				File dir = new File(path);
				if (!dir.exists())
					dir.mkdir();
			} else {
				path = path + File.separator + dirs[i - 1];
				File file = new File(path);
				if (!file.exists())
					file.createNewFile();
			}
		}
		return true;
	}

	public static File makeSdFile(String[] dirs) {
		String path = getSdpath();
		try {
			for (int i = 1; i <= dirs.length; i++) {
				if (i != dirs.length) {
					path = path + File.separator + dirs[i - 1];
					File dir = new File(path);
					if (!dir.exists())
						dir.mkdir();
				} else {
					path = path + File.separator + dirs[i - 1];
					File file = new File(path);
					if (!file.exists())
						file.createNewFile();
					return file;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return null;
	}



	public static String UriToFileName(String url, String replace) {
		return url.replaceAll("[/:\\\\|?*<>\"]", replace);

	}

}
