package com.dsd.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司的工具类，减少代码服用
 * @author im_dsd
 *
 */
public class ToastUtils {

	/**
	 * @param context 上下文环境
	 * @param msg 打印文本内容
	 */
	public  static void show(Context context,String msg)
	{
		Toast.makeText(context, msg, 0).show();
	}
}
