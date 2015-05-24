package kaede.me.howoldrobot.presenter;

import android.app.Activity;

import java.util.List;

import kaede.me.howoldrobot.Model.Face;
import kaede.me.howoldrobot.widget.FaceImageView;

/**
 * Created by kaede on 2015/5/24.
 */
public interface IDrawPresenter {
    public void drawFaces(Activity activity, FaceImageView faceImageView,List<Face> faces);
    public void clearViews();
}
