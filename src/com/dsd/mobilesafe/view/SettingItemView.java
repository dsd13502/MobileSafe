package com.dsd.mobilesafe.view;

import com.dsd.mobilesafe.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.im_dsd.mobilesafe";
	private CheckBox ck_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDeson;

	public SettingItemView(Context context) {
		this(context,null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		//xml  -->  view
		View.inflate(context, R.layout.setting_item_view, this);
		
		//获取控件
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		ck_box = (CheckBox) findViewById(R.id.cb_box);
		
		//获取原有属性，和自定义属性
		initAttr(attrs);
		
		//获取布局文件中定义的字符串，赋值给自定义组合控件的标题
		tv_title.setText(mDestitle);
		
	}

	/**
	 * 返回属性集合中自定义属性属性值，
	 * @param attrs 构造方法中维护好的属性集合
	 */
	private void initAttr(AttributeSet attrs) {
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
	
	public boolean isCheck()
	{
		return ck_box.isChecked();
	}
	
	public void setCheck( boolean isCheck)
	{
		ck_box.setChecked(isCheck);
		if(isCheck)
		{
			tv_des.setText(mDesoff);
		}
		else
		{
			tv_des.setText(mDeson);
		}
	}

}
