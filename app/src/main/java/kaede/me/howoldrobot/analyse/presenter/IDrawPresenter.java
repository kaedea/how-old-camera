package kaede.me.howoldrobot.analyse.presenter;

import java.util.List;

import kaede.me.howoldrobot.analyse.model.Face;
import kaede.me.howoldrobot.widget.AgeIndicatorLayout;
import kaede.me.howoldrobot.widget.FaceImageView;

/**
 * Created by kaede on 2015/5/24.
 */
public interface IDrawPresenter {
    public void drawFaces(AgeIndicatorLayout ageIndicatorLayout, FaceImageView faceImageView,List<Face> faces);
    public void clearViews();
}
