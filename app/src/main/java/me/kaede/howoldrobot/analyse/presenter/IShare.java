/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.presenter;

import android.support.annotation.WorkerThread;
import android.view.View;

public interface IShare {
    @WorkerThread
    String save(View view) throws Exception;

    void doShare(View view);

    void doShare(String imgPath);
}
