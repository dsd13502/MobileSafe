package com.dsd.mobilesafe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dsd.mobilesafe.R;

public class SettingItemView extends RelativeLayout {

	private static final String tag = "SettingItemView";
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.dsd.mobilesafe";
	private CheckBox ck_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// xml--->view 将设置界面的一个条目转换成view对象,直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.setting_item_view, this);

		// 等同于以下两行代码
		/*
		 * View view = View.inflate(context, R.layout.setting_item_view, null);
		 * this.addView(view);
		 */

		// 自定义组合控件中的标题描述
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		ck_box = (CheckBox) findViewById(R.id.cb_box);

		// 获取自定义以及原生属性的操作,写在此处,AttributeSet attrs对象中获取
		initAttrs(attrs);
		// 获取布局文件中定义的字符串,赋值给自定义组合控件的标题
		tv_title.setText(mDestitle);
		tv_des.setText(mDesoff);

	}

	/**
	 * 返回属性集合中自定义属性属性值，
	 * 
	 * @param attrs
	 *            构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {
		/*
		 * //获取属性的总个数 Log.i(tag,
		 * "attrs.getAttributeCount() = "+attrs.getAttributeCount());
		 * //获取属性名称以及属性值 for(int i=0;i<attrs.getAttributeCount();i++){
		 * Log.i(tag, "name = "+attrs.getAttributeName(i)); Log.i(tag,
		 * "value = "+attrs.getAttributeValue(i)); Log.i(tag,
		 * "分割线 ================================= "); }
		 */

		// 通过名空间+属性名称获取属性值

		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

	}

	@SuppressLint("NewApi")
	public SettingItemView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		this(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public boolean isCheck() {
		return ck_box.isChecked();
	}

	public void setCheck(boolean isCheck) {
		ck_box.setChecked(isCheck);
		if (isCheck) {
			tv_des.setText(mDesoff);
		} else {
			tv_des.setText(mDeson);
		}
	}

}
