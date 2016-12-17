/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import java.util.List;

import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.analyse.presenter.IDraw;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.widget.AgeIndicatorLayout;
import me.kaede.howoldrobot.widget.FaceImageView;

class DrawImpl implements IDraw {

    public DrawImpl(IPhotoView iPhotoView) {

    }

    @Override
    public void drawFaces(AgeIndicatorLayout ageIndicatorLayout, FaceImageView faceImageView,
                          List<Face> faces) {
        faceImageView.drawFaces(faces);
        ageIndicatorLayout.drawAges(faces, (ageIndicatorLayout.getMeasuredWidth()
                - faceImageView.getMeasuredWidth()) / 2,
                (ageIndicatorLayout.getMeasuredHeight() - faceImageView.getMeasuredHeight()) / 2);
    }

    @Override
    public void clearViews() {
    }
}
