/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.IOException;

import me.kaede.howoldrobot.Dispatcher;
import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.presenter.IShare;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.utils.BitmapUtil;
import me.kaede.howoldrobot.utils.FileUtil;

class ShareImpl implements IShare {

    private IPhotoView mPhotoView;
    private final File mShareDir;
    private final Handler mHandler;

    public ShareImpl(IPhotoView photoView) {
        mPhotoView = photoView;
        mHandler = new Handler(Looper.getMainLooper());
        File fileDir = mPhotoView.getContext().getExternalFilesDir("");
        if (fileDir == null) {
            fileDir = mPhotoView.getContext().getFilesDir();
        }
        mShareDir = new File(fileDir, "share");
        try {
            FileUtil.checkCreateDir(mShareDir);
        } catch (IOException e) {
            Logger.w(e);
        }
    }

    @Override
    @WorkerThread
    public String save(View view) throws Exception {
        Bitmap bitmap = BitmapUtil.getBitmapFromView(view);
        if (bitmap != null) {
            File shareImage = File.createTempFile("age_", "_share.jpg", mShareDir);
            String shareImgPath = shareImage.getAbsolutePath();
            BitmapUtil.saveBitmap(bitmap, 80, shareImgPath);
            return shareImgPath;
        }
        throw new Exception("Can not get bitmap from view.");
    }

    @Override
    public void doShare(final View view) {
        final Bitmap bitmap = BitmapUtil.getBitmapFromView(view);
        if (bitmap != null) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        File shareImage = File.createTempFile("age_", "_share.jpg", mShareDir);
                        final String shareImgPath = shareImage.getAbsolutePath();
                        BitmapUtil.saveBitmap(bitmap, 80, shareImgPath);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                shareBitmap(view.getContext(), shareImgPath);
                            }
                        });

                    } catch (IOException e) {
                        Logger.w(e);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoView.toast(view.getContext().getResources().getString(R.string.share_fail));
                            }
                        });
                    }
                }
            };
            Dispatcher.instance().post(runnable);
            return;
        }
        mPhotoView.toast(view.getContext().getResources().getString(R.string.share_fail));
    }

    @Override
    public void doShare(String imgPath) {
        if (!TextUtils.isEmpty(imgPath)) {
            shareBitmap(mPhotoView.getContext(), imgPath);
            return;
        }
        mPhotoView.toast(mPhotoView.getContext().getResources().getString(R.string.share_fail));
    }

    private void shareBitmap(Context context, String imgPath) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), imgPath,
                    "Share Image", "How Old Camera's Share Image");
            Uri imageUri = Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            // Calling startActivity() from outside of an Activity.
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(Intent.createChooser(share, context.getResources()
                    .getString(R.string.share_select_title)));
        } catch (Throwable e) {
            Logger.w(e);
            mPhotoView.toast(context.getResources().getString(R.string.share_fail));
        }
    }
}
