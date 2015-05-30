package kaede.me.howoldrobot.analyse.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.util.AppUtil;
import kaede.me.howoldrobot.util.NavigationUtil;

/**
 * Created by kaede on 2015/5/26.
 */
public class OptionsPresenterCompl implements IOptionsPresenter {
    @Override
    public void onOptionsItemClick(Context context, int id) {
        switch(id) {
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
