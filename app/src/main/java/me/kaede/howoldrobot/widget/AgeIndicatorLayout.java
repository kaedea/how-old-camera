/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.kaede.howoldrobot.R;
import me.kaede.howoldrobot.analyse.model.Face;
import me.kaede.howoldrobot.utils.BitmapUtil;


public class AgeIndicatorLayout extends LinearLayout {
    private static final String TAG = "age.indicator";

    private List<Face> mFaces;
    private Paint mPaint;
    private int xOffset;
    private int yOffset;
    private Boolean mIsDrawFace = false;

    private void init() {
        mIsDrawFace = false;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
    }

    public AgeIndicatorLayout(Context context) {
        super(context);
        init();
    }

    public AgeIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsDrawFace && mFaces != null) {
            for (Face item : mFaces) {
                // Draw age indicator
                View ageIndicateView = LayoutInflater.from(getContext().getApplicationContext())
                        .inflate(R.layout.item_age_indicator, null);

                if (ageIndicateView != null) {
                    TextView tvAge = (TextView) ageIndicateView.findViewById(R.id.tv_age);
                    tvAge.setText(String.valueOf(item.attributes.age));
                    ImageView ivGender = (ImageView) ageIndicateView.findViewById(R.id.iv_gender);
                    Drawable drawable = ageIndicateView.getBackground();

                    if (item.attributes.gender.equalsIgnoreCase("Male")) {
                        ivGender.setImageResource(R.drawable.icon_gende_male);
                        drawable = tintDrawable(drawable, ColorStateList.valueOf(ResourcesCompat
                                .getColor(getResources(), R.color.colorPrimaryDark, null)));
                    } else if (item.attributes.gender.equalsIgnoreCase("Female")) {
                        ivGender.setImageResource(R.drawable.icon_gender_female);
                        drawable = tintDrawable(drawable, ColorStateList.valueOf(ResourcesCompat
                                .getColor(getResources(), R.color.colorAccent, null)));
                    }

                    ViewCompat.setBackground(ageIndicateView, drawable);

                    Bitmap bitmap = BitmapUtil.getBitmapFromView(ageIndicateView, true);
                    if (bitmap != null) {
                        // BitmapUtil.saveBitmapToSd(bitmap, 100, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp.jpg");
                        canvas.drawBitmap(bitmap, item.faceRectangle.left + xOffset - (ageIndicateView.getMeasuredWidth() - item.faceRectangle.width) / 2,
                                item.faceRectangle.top + yOffset - bitmap.getHeight(), mPaint);
                    }
                    Log.i(TAG, "ageIndicateView getMeasuredHeight()= " + ageIndicateView.getMeasuredHeight()
                            + ", getHeight=" + ageIndicateView.getHeight());

                }
            }

        }
    }

    public void drawAges(List<Face> faces, int xOffset, int yOffset) {
        this.mFaces = faces;
        this.mIsDrawFace = true;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        invalidate();
    }

    public void clearAges() {
        this.mFaces = null;
        this.mIsDrawFace = false;
        invalidate();
    }

    private Drawable tintDrawable(Drawable drawable, ColorStateList color) {
        final Drawable tempDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(tempDrawable, color);
        return tempDrawable;
    }
}
