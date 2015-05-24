package kaede.me.howoldrobot.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by kaede on 2015/5/23.
 */
public class MesureLayout extends LinearLayout {

    private int width;
    private int height;
    public MesureLayout(Context context) {
        super(context);
    }

    public MesureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }


}
