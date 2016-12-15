/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Iterator;
import java.util.List;

import me.kaede.howoldrobot.analyse.model.Face;

/**
 * Created by kaede on 2015/5/23.
 */
public class FaceImageView extends ImageView {
    private static final String TAG = "FaceImageView";
    Boolean isDrawFace = false;
    List<Face> faces;
    private int width;
    private int height;
    private Paint paint;

    public FaceImageView(Context context) {
        super(context);
        init();
    }

    public FaceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.isDrawFace = false;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawFace && faces != null) {
            Iterator<Face> iterator = faces.iterator();
            while (iterator.hasNext()) {
                Face item = iterator.next();
                //draw rect
                canvas.drawRect(item.faceRectangle.left, item.faceRectangle.top, item.faceRectangle.left + item.faceRectangle.width, item.faceRectangle.top + item.faceRectangle.height, paint);
            }

        }
    }

    public void drawFaces(List<Face> faces) {
        this.faces = faces;
        this.isDrawFace = true;
        invalidate();
    }

    public void clearFaces() {
        this.faces = null;
        this.isDrawFace = false;
        invalidate();
    }
}
