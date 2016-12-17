/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.presenter.ISharePresenter;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.utils.BitmapUtil;

class ShareImpl implements ISharePresenter {

    private IPhotoView mPhotoView;
    private final File mCacheDir;

    public ShareImpl(IPhotoView photoView) {
        mPhotoView = photoView;
        File cacheDir = mPhotoView.getContext().getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = mPhotoView.getContext().getCacheDir();
        }
        mCacheDir = cacheDir;
    }

    @Override
    public void doShare(Context context, View view) {
        Bitmap bitmap = BitmapUtil.getViewBitmap(view);
        if (bitmap != null) {
            try {
                File temp = File.createTempFile("how_old_", "_share.jpg", mCacheDir);
                BitmapUtil.saveBitmapToSd(bitmap, 80, temp.getAbsolutePath());

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,
                        "Share Image", "How Old Camera's Share Image");
                Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);

                context.startActivity(Intent.createChooser(share, context.getResources()
                        .getString(R.string.share_select_title)));

            } catch (Exception e) {
                mPhotoView.toast(context.getResources().getString(R.string.share_fail));
                e.printStackTrace();
            }
        }
    }
}
