package com.jrue.appframe.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jrue on 17/2/15.
 */
public class FlashView extends View {

    private String[]  mText={"ON","1","2","3","4","5","OFF"};

    private Paint mTextPaint;
    private Paint mLinePaint;

    private float mMinSize;
    private float mCenter;
    private float mRadius;
    private double degree = -Math.PI/2;

    private double oldDistance = -1;

    public static final int STATE_1 = 1;
    public static final int STATE_2 = 2;
    public static final int STATE_3 = 3;
    public static final int STATE_4 = 4;
    public static final int STATE_5 = 5;
    public static final int STATE_OFF = 6;
    public static final int STATE_ON = 7;

    private FlashCallBack callBack = null;

    public FlashView(Context context){
        super(context, null);
    }

    public FlashView(Context context, AttributeSet attrs){
        this(context, attrs, 0);

    }

    public FlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface FlashCallBack{
        public void update(int state);

    }
    public void setCallBack(FlashCallBack callBack){
        this.callBack = callBack;
    }

    private void init(){
        DisplayMetrics displayMetrics = getContext().getResources()
                .getDisplayMetrics();
        int  width = displayMetrics.widthPixels;
        mMinSize = width / 600f;
        mCenter = width / 2;
        mRadius = 160 * mMinSize;

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mMinSize * 30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(mMinSize * 4);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        double stopX = mCenter - mRadius * Math.cos(degree);
        double stopY = mCenter - mRadius * Math.sin(degree);
        canvas.drawLine(mCenter, mCenter, (float) stopX, (float) stopY, mLinePaint);
        for (int i = 0; i < 7; i++){
            canvas.drawText(mText[i], mCenter, mCenter + mRadius + 50 * mMinSize, mTextPaint);
            canvas.rotate(51.428f, mCenter, mCenter);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        degree = Math.atan2(mCenter - y, mCenter - x);
        if(degree < 0){
            degree = degree + 2 * Math.PI;
        }
        double distance = Math.sqrt((x - mCenter) * (x - mCenter) + (y - mCenter) * (y - mCenter));
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if( distance > mRadius + 50 * mMinSize){
                    return true;
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if( distance > mRadius + 50 * mMinSize && oldDistance < 0){
                    return true;
                }
                else if (distance > mRadius + 50 * mMinSize && oldDistance > 0){
                    setDegree(degree);
                    oldDistance = -1;
                }else if (distance < mRadius + 50 * mMinSize){
                    oldDistance = distance;
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP:{
                oldDistance = -1;
                setDegree(degree);
                invalidate();
                break;
            }
        }
        return true;
    }

    private void setDegree(double dg){
        double d = dg + Math.PI / 2;
        int count = (int)(d  /Math.PI * 180 / 51);
        if ((d / Math.PI * 180) % 51.428 > 51.428 / 2)
            count++;
        d = count * 51.428;
        degree = d / 180 * Math.PI - Math.PI / 2;

        if (callBack != null){
            if (count > 7)
                count-=7;
            callBack.update(count);
        }
    }
}
