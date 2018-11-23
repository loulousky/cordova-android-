package com.liuhai.jiugeh5.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by admin on 2017/4/22.
 */

public class ShuiYinView extends android.support.v7.widget.AppCompatImageView {
    private Paint mPaint;


    public ShuiYinView(Context context) {
        super(context);
    }

    public ShuiYinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint = new Paint();
        mPaint.setFakeBoldText(true);
        mPaint.setAlpha(50);
        mPaint.setTextSize(getWidth()/10);
        float widtha=mPaint.measureText("游戏狗水印");
        canvas.drawText("游戏狗水印", getWidth()-widtha, getHeight(), mPaint);
        canvas.drawLine(0,0,getWidth(),getHeight(),mPaint);


    }


    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 50;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
// If your control can fit within these bounds return that value.
            result = specSize;
        }
        return result;
    }


    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
// Default size if no limits are specified.
        int result = 500;

        if (specMode == MeasureSpec.AT_MOST)

        {

            result = specSize;

        } else if (specMode == MeasureSpec.EXACTLY)

        {
            result = specSize;
        }
        return result;
    }
}


