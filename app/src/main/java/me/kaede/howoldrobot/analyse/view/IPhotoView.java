package me.kaede.howoldrobot.analyse.view;

import android.graphics.Bitmap;

import java.util.List;

import android.view.View;
import me.kaede.howoldrobot.analyse.model.Face;

/**
 * Created by kaede on 2015/5/23.
 */
public interface IPhotoView {
    public void onGetFaces(List<Face> faces);
    public void onGetImage(Bitmap bitmap,String imgPath);
	public void showProgressDialog(Boolean isShow);
    public void toast(String msg);
    public View getPhotoContainer();
}
