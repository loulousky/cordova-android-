package com.liuhai.jiugeh5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MoveImage extends android.support.v7.widget.AppCompatImageView {

    private int lastX = 0;
    private int lastY = 0;

    private int dx;
    private int dy;
    private float movex = 0;
    private float movey = 0;

    private int screenWidth;
    private int screenHeight;
    private boolean isleft = true;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public MoveImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = ScreenUtils.getWidth(context);
        screenHeight =ScreenUtils.getHeight(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                movex = lastX;
                movey = lastY;
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;

                left = getLeft() + dx;
                top = getTop() + dy;
                right = getRight() + dx;
                bottom = getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }


                if (left <= screenWidth / 2) {
                    isleft = true;
                } else {
                    isleft = false;
                }

//
//			ObjectAnimator y = ObjectAnimator.ofFloat(this, "y", getY(), dy);
//			ObjectAnimator x = ObjectAnimator.ofFloat(this, "x", getX(), dx);
//
//			AnimatorSet animatorSet = new AnimatorSet();
//			animatorSet.playTogether(x, y);
//			animatorSet.setDuration(0);
//			animatorSet.start();	// 属性动画移动


                layout(left, top, right, bottom);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //避免滑出触发点击事件
                if ((int) (event.getRawX() - movex) >5
                        || (int) (event.getRawY() - movey) > 5) {

                   RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams)getLayoutParams();
                    lp.setMargins(left,top,0,0);
                   setLayoutParams(lp);


                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }




    public boolean isleft() {

        return isleft;
    }


}
