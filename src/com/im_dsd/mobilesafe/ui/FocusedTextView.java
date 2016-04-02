package com.im_dsd.mobilesafe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义跑马灯TextView控件，
 * @author im_dsd
 *
 */
public class FocusedTextView extends TextView {

	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	public FocusedTextView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	//重写获取焦点方法，由系统调用，一直获取焦点
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
}
