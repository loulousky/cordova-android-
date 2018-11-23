package com.liuhai.jiugeh5.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

class ScreenUtils {

	/**
	 * 获取手机屏幕大小
	 * 
	 * @author
	 * 
	 */

	public static int getWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 高
	 * 
	 * @return
	 */
	public static int getHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

}
