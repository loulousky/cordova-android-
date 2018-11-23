package com.liuhai.jiugeh5.view;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.liuhai.jiugeh5.R;

import java.util.Timer;
import java.util.TimerTask;


public class FloatView extends FrameLayout implements OnTouchListener {

    private final int HANDLER_TYPE_HIDE_LOGO = 100;// 隐藏LOGO
    private final int HANDLER_TYPE_CANCEL_ANIM = 101;// 退出动画

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Activity mContext;

    // private View mRootFloatView;
    private ImageView mIvFloatLogo;
    //	private ImageView mIvFloatLoader;
    private LinearLayout mLlFloatMenu;
    private RelativeLayout mTvAccount;

    private FrameLayout mFlFloatLogo;

    private boolean mIsRight;// logo是否在右边
    private boolean mCanHide;// 是否允许隐藏
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;
    private boolean mShowLoader = true;

    private Timer mTimer;
    private TimerTask mTimerTask;

    final Handler mTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
                // 比如隐藏悬浮框
                if (mCanHide) {
                    mCanHide = false;
                    if (mIsRight) {
                        //右边收起图标
                        mIvFloatLogo
                                .setImageResource(R.mipmap.gd_image_float_logo);
                        mIvFloatLogo.setScaleType(ImageView.ScaleType.FIT_END);
                        mLlFloatMenu.setBackgroundResource(R.mipmap.gamedog_menu_bg);
                    } else {
                        //左边收起图标
                        mIvFloatLogo
                                .setImageResource(R.mipmap.gd_image_float_logo);
                        mIvFloatLogo.setScaleType(ImageView.ScaleType.FIT_START);
                        mLlFloatMenu.setBackgroundResource(R.mipmap.gamedog_menu_bg_left);
                    }
                    mWmParams.alpha = 0.7f;
                    mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                    refreshFloatMenu(mIsRight);
                    mLlFloatMenu.setVisibility(View.GONE);
                }
            } else if (msg.what == HANDLER_TYPE_CANCEL_ANIM) {
                //		mIvFloatLoader.clearAnimation();
                //		mIvFloatLoader.setVisibility(View.GONE);
                mIvFloatLogo
                        .setImageResource(R.mipmap.gd_image_float_logo_nor);
                mShowLoader = false;
            }
            super.handleMessage(msg);
        }
    };
//    private RelativeLayout mTvGl;
    private RelativeLayout mTvactive;
    private RelativeLayout mTvGift;
    private WebView webView;
    private String url;
    private String name;
    private String icon;

    public FloatView(@NonNull Context context) {
        super(context);
    }

    public FloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Activity context, WebView webView,
                     String url, String name, String icon) {
        super(context);
        this.webView = webView;
        this.url=url;
        this.name=name;
        this.icon=icon;
        init(context);

    }



    private void init(Activity mContext) {
        this.mContext = mContext;

        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置�?
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;

        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);

        mTimer = new Timer();
        hide();
        refreshFloatMenu(false);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:// 横屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT:// 竖屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    /**
     * 创建Float view
     *
     * @param context
     * @return
     */
    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        View rootFloatView = inflater.inflate(
                R.layout.gamedog_widget_float_view,
                null);
        mFlFloatLogo = (FrameLayout) rootFloatView.findViewById(R.id.gd_float_view);

        mIvFloatLogo = (ImageView) rootFloatView.findViewById(R.id.gd_float_view_icon_imageView);
//		mIvFloatLoader = (ImageView) rootFloatView.findViewById(ResourceUtils
//				.getId(context, "gd_float_view_icon_notify"));
        mLlFloatMenu = (LinearLayout) rootFloatView.findViewById(R.id.ll_menu);
        mTvAccount = (RelativeLayout) rootFloatView.findViewById(R.id.tv_back);
        mTvGift = (RelativeLayout) rootFloatView.findViewById(R.id.tv_gift);
        mTvactive = (RelativeLayout) rootFloatView.findViewById(R.id.tv_active);
        //mTvGl = (RelativeLayout) rootFloatView.findViewById(R.id.tv_gl);

        mTvGift.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                opengiftcenter();

            }
        });

        mTvactive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                openActive();
            }
        });

//        mTvGl.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                openGlcenter();
//            }
//        });

        mTvAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mLlFloatMenu.setVisibility(View.GONE);
                openUserCenter();
            }
        });

        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDraging) {
                    if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
                        mLlFloatMenu.setVisibility(View.GONE);
                    } else {
                        mLlFloatMenu.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mIvFloatLogo.setImageResource(R.mipmap.gd_image_float_logo);
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    mLlFloatMenu.setVisibility(View.GONE);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                mIvFloatLogo.setImageResource(R.mipmap.gd_image_float_logo);
                refreshFloatMenu(mIsRight);
                timerForHide();
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        setVisibility(View.GONE);
        Message message = mTimerHandler.obtainMessage();
        message.what = HANDLER_TYPE_HIDE_LOGO;
        mTimerHandler.sendMessage(message);
        removeTimerTask();
    }

    /**
     * 显示悬浮窗
     */
    public void show() {

        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);

            if (mShowLoader) {
                mIvFloatLogo.setImageResource(R.mipmap.gd_image_float_logo);
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);

                timerForHide();

                mShowLoader = false;
//				Animation rotaAnimation = AnimationUtils.loadAnimation(
//						mContext,
//						ResourceUtils.getAnimId(mContext, "pj_loading_anim"));
//				rotaAnimation.setInterpolator(new LinearInterpolator());
                //	mIvFloatLoader.startAnimation(rotaAnimation);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimerHandler
                                .sendEmptyMessage(HANDLER_TYPE_CANCEL_ANIM);
                    }
                }, 3000);
            }
        }
    }

    /**
     * 刷新float view menu
     *
     * @param right
     */
    private void refreshFloatMenu(boolean right) {
        if (right) {
            LayoutParams paramsFloatImage = (LayoutParams) mIvFloatLogo
                    .getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT;
            mIvFloatLogo.setLayoutParams(paramsFloatImage);
            LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo
                    .getLayoutParams();
            paramsFlFloat.gravity = Gravity.RIGHT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0, mContext.getResources()
                            .getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 62, mContext.getResources()
                            .getDisplayMetrics());
            FrameLayout.LayoutParams paramsMenuAccount = (FrameLayout.LayoutParams) mLlFloatMenu
                    .getLayoutParams();
            paramsMenuAccount.rightMargin = padding52;
            paramsMenuAccount.leftMargin = padding;
            mLlFloatMenu.setLayoutParams(paramsMenuAccount);

            mLlFloatMenu.setBackgroundResource(R.mipmap.gamedog_menu_bg);
//			LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback
//					.getLayoutParams();
//			paramsMenuFb.rightMargin = padding52;
//			paramsMenuFb.leftMargin = padding;
//			mTvFeedback.setLayoutParams(paramsMenuFb);

        } else {
            LayoutParams params = (LayoutParams) mIvFloatLogo
                    .getLayoutParams();
            params.gravity = Gravity.LEFT;
            mIvFloatLogo.setLayoutParams(params);
            LayoutParams paramsFlFloat = (LayoutParams) mFlFloatLogo
                    .getLayoutParams();
            paramsFlFloat.gravity = Gravity.LEFT;
            mFlFloatLogo.setLayoutParams(paramsFlFloat);

            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0, mContext.getResources()
                            .getDisplayMetrics());
            int padding52 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 62, mContext.getResources()
                            .getDisplayMetrics());

            FrameLayout.LayoutParams paramsMenuAccount = (FrameLayout.LayoutParams) mLlFloatMenu
                    .getLayoutParams();

            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = padding52;
            mLlFloatMenu.setLayoutParams(paramsMenuAccount);
            mLlFloatMenu.setBackgroundResource(R.mipmap.gamedog_menu_bg_left);
//			LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) mTvFeedback
//					.getLayoutParams();
//			paramsMenuFb.rightMargin = padding;
//			paramsMenuFb.leftMargin = padding;
//			mTvFeedback.setLayoutParams(paramsMenuFb);

        }
    }

    /**
     * 定时隐藏float view
     */
    private void timerForHide() {
        mCanHide = true;

        // 结束任务
        if (mTimerTask != null) {
            try {
                mTimerTask.cancel();
                mTimerTask = null;
            } catch (Exception e) {
            }

        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimerHandler.obtainMessage();
                message.what = HANDLER_TYPE_HIDE_LOGO;
                mTimerHandler.sendMessage(message);
            }
        };
        if (mCanHide) {
            mTimer.schedule(mTimerTask, 6000, 3000);
        }
    }

    /**
     * 返回
     */
    private void openUserCenter() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                Toast.makeText(getContext(), "当前已经是最前页面了",1).show();
            }
        }

        mLlFloatMenu.setVisibility(View.GONE);

    }

    /**
     * 关闭
     */
    private void opengiftcenter() {

        if (mContext != null) {
            destroy();
            mContext.finish();
        }


    }

    /**
     * 刷新
     */
    private void openGlcenter() {
        mLlFloatMenu.setVisibility(View.GONE);


    }

    /**
     * 分享
     */
    private void openActive() {
        if (webView != null) {
            webView.reload();
        }
        mLlFloatMenu.setVisibility(View.GONE);

    }


    /**
     * 是否Float view
     */
    public void destroy() {
        hide();
        removeFloatView();
        removeTimerTask();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        try {
            mTimerHandler.removeMessages(1);
        } catch (Exception e) {
        }
    }
}
