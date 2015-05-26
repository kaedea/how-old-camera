package kaede.me.howoldrobot.presenter;

import android.app.Activity;

import java.util.List;

import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.widget.AgeIndicatorLayout;
import kaede.me.howoldrobot.widget.FaceImageView;

/**
 * Created by kaede on 2015/5/24.
 */
public interface IDrawPresenter {
    public void drawFaces(AgeIndicatorLayout ageIndicatorLayout, FaceImageView faceImageView,List<Face> faces);
    public void clearViews();
}
