/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface IAnalyse {
    void pickPhoto(Activity activity, int type);

    void getImage(Context context, Intent intent);

    void doAnalyse(String imgPath);
}
