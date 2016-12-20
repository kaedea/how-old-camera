/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot;

import android.app.Application;

public class RobotApplication extends Application {

    @Override
    public void onCreate() {
        Dispatcher.instance().start();
    }
}
