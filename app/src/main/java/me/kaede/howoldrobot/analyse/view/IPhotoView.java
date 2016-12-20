/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

import me.kaede.howoldrobot.analyse.model.Face;

public interface IPhotoView {

    void onGetFaces(List<Face> faces);

    void onGetImage(Bitmap bitmap, String imgPath);

    void showProgressDialog(Boolean isShow, String msg);

    void toast(String msg);

    View getContainer();

    Context getContext();
}
