package kaede.me.howoldrobot.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import kaede.me.howoldrobot.util.BitmapUtil;
import kaede.me.howoldrobot.view.IPhotoView;

import java.io.ByteArrayOutputStream;

/**
 * Created by kaede on 2015/5/25.
 */
public class SharePresenterCompl implements ISharePresenter {
	IPhotoView iPhotoView;

	public SharePresenterCompl(IPhotoView iPhotoView) {
		this.iPhotoView = iPhotoView;
	}

	@Override
	public void doShare(Context context, View view) {
		Bitmap b = BitmapUtil.getViewBitmap(view);
		if (b!=null){
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
			String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
					b, "Title", null);
			Uri imageUri =  Uri.parse(path);
			share.putExtra(Intent.EXTRA_STREAM, imageUri);
			context.startActivity(Intent.createChooser(share, "Select"));
		}
	}
}
