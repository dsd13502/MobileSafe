package com.im_dsd.mobilesafe.view;

import com.example.mobilesafe.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private CheckBox ck_box;
	private TextView tv_des;

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
		
	}

	@SuppressLint("NewApi")
	public SettingItemView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
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
			tv_des.setText("自动更新以开启");
		}
		else
		{
			tv_des.setText("自动更新以关闭");
		}
	}

}
