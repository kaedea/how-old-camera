/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kaede on 2015/5/23.
 */
public interface IAnalysePresenter {
    public void doAnalyse(String imgPath);

    public void pickPhoto(Activity activity, int type);

    public void getImage(Context context, Intent intent);
}
