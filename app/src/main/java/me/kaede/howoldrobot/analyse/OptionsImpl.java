/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.presenter.IOptionsPresenter;
import me.kaede.howoldrobot.utils.DeviceUtil;
import me.kaede.howoldrobot.utils.NavigationUtil;

class OptionsImpl implements IOptionsPresenter {

    @Override
    public void onOptionsItemClick(Context context, int id) {
        switch (id) {
            case R.id.action_star:
                NavigationUtil.goToMarket(context, DeviceUtil.getPackageName(context));
                break;
            case R.id.action_web:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://how-old.net"));
                context.startActivity(browserIntent);
                break;
            default:
                break;
        }
    }
}
