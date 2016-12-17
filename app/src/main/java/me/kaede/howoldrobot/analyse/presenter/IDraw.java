/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.presenter;

import java.util.List;

import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.widget.AgeIndicatorLayout;
import me.kaede.howoldrobot.widget.FaceImageView;

/**
 * Created by kaede on 2015/5/24.
 */
public interface IDraw {
    public void drawFaces(AgeIndicatorLayout ageIndicatorLayout, FaceImageView faceImageView, List<Face> faces);

    public void clearViews();
}
