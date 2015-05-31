package me.kaede.howoldrobot.analyse.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.util.BitmapUtil;
import me.kaede.howoldrobot.util.FileUtil;

/**
 * Created by kaede on 2015/5/25.
 */
public class SharePresenterCompl implements ISharePresenter {
	IPhotoView iPhotoView;
	File appBaseDir;

	public SharePresenterCompl(IPhotoView iPhotoView) {
		this.iPhotoView = iPhotoView;
		File dir = new File(FileUtil.getSdpath() + File.separator + "Moe Studio");
		dir.mkdir();
		appBaseDir = new File(dir.getAbsolutePath() + File.separator + "How Old Robot");
		appBaseDir.mkdir();
	}

	@Override
	public void doShare(Context context, View view) {
		Bitmap b = BitmapUtil.getViewBitmap(view);
		if (b!=null){
			File temp = FileUtil.getTempFile(appBaseDir.getAbsolutePath(),".jpg");
			if (temp != null) {
				BitmapUtil.saveBitmapToSd(b,80,temp.getAbsolutePath());
			}
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
			String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),b, "Share Image", "How Old Camera's Share Image");
			Uri imageUri =  Uri.parse(path);
			share.putExtra(Intent.EXTRA_STREAM, imageUri);
            try {
                context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_select_title)));
            } catch (Exception e) {
                iPhotoView.toast(context.getResources().getString(R.string.share_fail));
                e.printStackTrace();
            }
        }
	}
}
