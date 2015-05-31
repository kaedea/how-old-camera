package me.kaede.howoldrobot.analyse.presenter;

import android.app.Activity;
import android.view.View;

import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.analyse.view.IPhotoView;
import me.kaede.howoldrobot.widget.FaceImageView;
import me.kaede.howoldrobot.widget.AgeIndicatorLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaede on 2015/5/24.
 */
public class DrawPresenterCompl implements IDrawPresenter {
    List<View>views;
    IPhotoView iPhotoView;
    /*private WindowManager windowManager;
    private WindowManager.LayoutParams params;*/

    public DrawPresenterCompl(Activity activity,IPhotoView iPhotoView) {
        this.iPhotoView = iPhotoView;
        views = new ArrayList<>();

        /*windowManager = activity.getWindowManager();
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.LAST_APPLICATION_WINDOW,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;*/

    }


    @Override
    public void drawFaces(AgeIndicatorLayout ageIndicatorLayout, FaceImageView faceImageView, List<Face> faces) {
        faceImageView.drawFaces(faces);
        ageIndicatorLayout.drawAges(faces, (ageIndicatorLayout.getMeasuredWidth()-faceImageView.getMeasuredWidth())/2, (ageIndicatorLayout.getMeasuredHeight()-faceImageView.getMeasuredHeight())/2);


        //使用WindowManager显示AgeIndicator
        /*View photoContainer = activity.findViewById(R.id.layout_main_photo);
        *//* 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;*//*
        int x_center = photoContainer.getMeasuredWidth()/2;
        int y_center = photoContainer.getMeasuredHeight()/2;
        int x = x_center - faceImageView.getMeasuredWidth()/2 + activity.getResources().getDimensionPixelSize(R.dimen.margin_main_left);
        int y = y_center - faceImageView.getMeasuredHeight()/2+ activity.getResources().getDimensionPixelSize(R.dimen.margin_main_top) +  activity.getResources().getDimensionPixelSize(R.dimen.common_actionbar_height);


        Iterator<Face> iterator = faces.iterator();
        while (iterator.hasNext()){
            Face item = iterator.next();
            params.x = x+item.faceRectangle.left - (activity.getResources().getDimensionPixelSize(R.dimen.indicator_width) - item.faceRectangle.width)/2;
            params.y = y+item.faceRectangle.top - activity.getResources().getDimensionPixelSize(R.dimen.indicator_height);
            View ageIndicateView = LayoutInflater.from(activity.getApplicationContext()).inflate(
                    R.layout.item_age_indicator, null);
            if (ageIndicateView!=null){
	            TextView tvAge = (TextView) ageIndicateView.findViewById(R.id.tv_ageindicator_age);
	            tvAge.setText(String.valueOf(item.attributes.age));
	            ImageView ivGender = (ImageView) ageIndicateView.findViewById(R.id.iv_ageindicator_gender);
	            if (item.attributes.gender.equalsIgnoreCase("Male")){
		            ivGender.setImageResource(R.drawable.icon_gende_male);
	            }else if(item.attributes.gender.equalsIgnoreCase("Female")){
		            ivGender.setImageResource(R.drawable.icon_gender_female);
	            }
                views.add(ageIndicateView);
                windowManager.addView(ageIndicateView, params);
            }
        }*/
    }

    @Override
    public void clearViews() {
        /*Iterator<View> iterator = views.iterator();
        while (iterator.hasNext()){
            View view = iterator.next();
            windowManager.removeViewImmediate(view);
        }
        views.clear();*/
    }
}
