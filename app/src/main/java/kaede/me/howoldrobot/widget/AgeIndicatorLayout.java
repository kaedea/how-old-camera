package kaede.me.howoldrobot.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.util.BitmapUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kaede on 2015/5/23.
 */
public class AgeIndicatorLayout extends LinearLayout {
    private static final String TAG = "AgeIndicatorLayout";
    Boolean isDrawFace = false;
    List<Face> faces;
    Paint paint;
    private int xOffset;
    private int yOffset;

    private void init(){
        this.isDrawFace = false;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
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
        if (isDrawFace&&faces!=null){
            Iterator<Face> iterator = faces.iterator();
            while (iterator.hasNext()){
                Face item = iterator.next();
                //draw age indicator
                View ageIndicateView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.item_age_indicator, null);
                if (ageIndicateView!=null){
                    TextView tvAge = (TextView) ageIndicateView.findViewById(R.id.tv_ageindicator_age);
                    tvAge.setText(String.valueOf(item.attributes.age));
                    ImageView ivGender = (ImageView) ageIndicateView.findViewById(R.id.iv_ageindicator_gender);
                    if (item.attributes.gender.equalsIgnoreCase("Male")){
                        ivGender.setImageResource(R.drawable.icon_gende_male);
                    }else if(item.attributes.gender.equalsIgnoreCase("Female")){
                        ivGender.setImageResource(R.drawable.icon_gender_female);
                    }
                    Bitmap bitmap = BitmapUtil.getBitmapFromView(ageIndicateView,true);
                    if (bitmap != null) {
                       //BitmapUtil.saveBitmapToSd(bitmap, 100, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp.jpg");
                        canvas.drawBitmap(bitmap,item.faceRectangle.left+xOffset - (ageIndicateView.getMeasuredWidth() - item.faceRectangle.width)/2,item.faceRectangle.top+yOffset-bitmap.getHeight(),paint);
                    }
                    Log.i(TAG, "ageIndicateView getMeasuredHeight()= " + ageIndicateView.getMeasuredHeight() + " getHeight=" + ageIndicateView.getHeight());

                }
            }

        }
    }


    public void drawAges( List<Face> faces,int xOffset,int yOffset){
        this.faces = faces;
        this.isDrawFace = true;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        invalidate();
    }

    public void clearAges(){
        this.faces = null;
        this.isDrawFace = false;
        invalidate();
    }
}
