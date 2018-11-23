package com.liuhai.jiugeh5.utils;

/** 异步调用数据回调接口 */
public interface GetDataBackcall {
	/** 正常获取数据后执行的回调函数  */
	public void backcall(Object option);
	/** 获取数据异常后执行的回调函数  */
	public void errorBackcall(Object option);
}
