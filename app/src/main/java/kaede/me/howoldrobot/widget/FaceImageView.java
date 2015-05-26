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

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.widget.TextView;
import kaede.me.howoldrobot.R;
import kaede.me.howoldrobot.model.Face;
import kaede.me.howoldrobot.util.BitmapUtil;

/**
 * Created by kaede on 2015/5/23.
 */
public class FaceImageView extends ImageView{
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

    private void init(){
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
        if (isDrawFace&&faces!=null){
            Iterator<Face> iterator = faces.iterator();
            while (iterator.hasNext()){
                Face item = iterator.next();
                //draw rect
                canvas.drawRect(item.faceRectangle.left,item.faceRectangle.top,item.faceRectangle.left+item.faceRectangle.width,item.faceRectangle.top+item.faceRectangle.height, paint);
            }

        }
    }

    public void drawFaces( List<Face> faces){
        this.faces = faces;
        this.isDrawFace = true;
        invalidate();
    }

    public void clearFaces(){
        this.faces = null;
        this.isDrawFace = false;
        invalidate();
    }
}
