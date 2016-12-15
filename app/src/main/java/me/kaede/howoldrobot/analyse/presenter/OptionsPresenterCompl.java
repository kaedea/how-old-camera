/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.utils.AppUtil;
import me.kaede.howoldrobot.utils.NavigationUtil;

/**
 * Created by kaede on 2015/5/26.
 */
public class OptionsPresenterCompl implements IOptionsPresenter {
    @Override
    public void onOptionsItemClick(Context context, int id) {
        switch (id) {
            case R.id.action_star:
                NavigationUtil.naviToMarket(context, AppUtil.getpackageName(context));
                break;
            case R.id.action_web:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://how-old.net"));
                context.startActivity(browserIntent);
                //NavigationUtil.toWebViewActivity(context,"http://how-old.net/","Online");
                break;
            default:
                break;
        }
    }
}
